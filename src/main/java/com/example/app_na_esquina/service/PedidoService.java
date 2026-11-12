package com.example.app_na_esquina.service;

import com.example.app_na_esquina.dto.pedido.CriarPedidoDTO;
import com.example.app_na_esquina.dto.pedido.ItemPedidoDTO;
import com.example.app_na_esquina.dto.pedido.PedidoResponseDTO;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;
import com.example.app_na_esquina.model.*;
import com.example.app_na_esquina.model.enums.StatusPedido;
import com.example.app_na_esquina.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MercadinhoRepository mercadinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Value("${vapid.public.key:}")
    private String vapidPublicKey;

    @Value("${vapid.private.key:}")
    private String vapidPrivateKey;

    @Transactional
    public PedidoResponseDTO criarPedido(CriarPedidoDTO dto) {
        if (dto.getIdCliente() == null || dto.getIdMercadinho() == null) {
            throw new RegraNegocioException("Cliente e mercadinho são obrigatórios.");
        }
        if (dto.getItens() == null || dto.getItens().isEmpty()) {
            throw new RegraNegocioException("O pedido precisa ter pelo menos um item.");
        }

        UsuarioModel cliente = usuarioRepository.findById(dto.getIdCliente())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        MercadinhoModel mercadinho = mercadinhoRepository.findById(dto.getIdMercadinho())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mercadinho não encontrado."));

        PedidoModel pedido = new PedidoModel();
        pedido.setCliente(cliente);
        pedido.setMercadinho(mercadinho);
        pedido.setStatus(StatusPedido.PENDENTE);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDto : dto.getItens()) {
            if (itemDto.getIdProduto() == null) {
                throw new RegraNegocioException("Há um item sem produto no pedido.");
            }
            if (itemDto.getQuantidade() == null || itemDto.getQuantidade() < 1 || itemDto.getQuantidade() > 99) {
                throw new RegraNegocioException("A quantidade de cada item deve ser de 1 a 99.");
            }

            ProdutoModel produto = produtoRepository.findById(itemDto.getIdProduto())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Um dos produtos do pedido não existe mais."));

            if (produto.getMercadinho() != null &&
                    !produto.getMercadinho().getIdMercadinho().equals(mercadinho.getIdMercadinho())) {
                throw new RegraNegocioException("O produto '" + produto.getNome() + "' não pertence ao mercadinho informado.");
            }

            BigDecimal precoUnitario = BigDecimal.valueOf(produto.getPreco());

            ItemPedidoModel item = new ItemPedidoModel();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(precoUnitario);

            pedido.getItens().add(item);

            BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(itemDto.getQuantidade()));
            total = total.add(subtotal);
        }

        pedido.setValorTotal(total);
        return PedidoResponseDTO.de(pedidoRepository.save(pedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorCliente(Long idCliente) {
        return pedidoRepository.findByClienteIdUsuarioOrderByDataCriacaoDesc(idCliente).stream()
                .map(PedidoResponseDTO::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorMercadinho(Long idMercadinho) {
        return pedidoRepository.findByMercadinhoIdMercadinhoOrderByDataCriacaoDesc(idMercadinho).stream()
                .map(PedidoResponseDTO::de)
                .toList();
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long idPedido, StatusPedido novoStatus, String motivo) {
        PedidoModel pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));

        pedido.setStatus(novoStatus);

        if (novoStatus == StatusPedido.CANCELADO && motivo != null && !motivo.trim().isEmpty()) {
            pedido.setMotivoCancelamento(motivo);
        }

        PedidoModel salvo = pedidoRepository.save(pedido);

        notificarMudancaStatus(salvo, novoStatus);

        return PedidoResponseDTO.de(salvo);
    }

    private void notificarMudancaStatus(PedidoModel pedido, StatusPedido novoStatus) {
        try {
            UsuarioModel cliente = pedido.getCliente();
            if (cliente != null && cliente.getPushToken() != null && !cliente.getPushToken().trim().isEmpty()) {
                String nomeMercadinho = (pedido.getMercadinho() != null && pedido.getMercadinho().getNomeFantasia() != null)
                        ? pedido.getMercadinho().getNomeFantasia()
                        : "Mercadinho";

                String titulo = "Atualização do seu pedido #" + pedido.getIdPedido();
                String mensagem = "Seu pedido em " + nomeMercadinho + " mudou para: " + formatarStatus(novoStatus);

                String token = cliente.getPushToken().trim();

                // Se começar com '{', é uma subscrição Web Push (JSON); caso contrário, é Expo Mobile Token
                if (token.startsWith("{")) {
                    enviarWebPushNotification(token, titulo, mensagem);
                } else {
                    enviarExpoPushNotification(token, titulo, mensagem, pedido.getIdPedido());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar notificação push: " + e.getMessage());
        }
    }

    private void enviarWebPushNotification(String subscriptionJson, String titulo, String mensagem) {
        try {
            if (java.security.Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME) == null) {
                java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(subscriptionJson);

            String endpoint = root.path("endpoint").asText();
            String p256dh = root.path("keys").path("p256dh").asText();
            String auth = root.path("keys").path("auth").asText();

            nl.martijndwars.webpush.Subscription subscription = new nl.martijndwars.webpush.Subscription(
                    endpoint,
                    new nl.martijndwars.webpush.Subscription.Keys(p256dh, auth)
            );

            String payload = mapper.writeValueAsString(Map.of(
                    "title", titulo,
                    "body", mensagem
            ));

            nl.martijndwars.webpush.PushService pushService = new nl.martijndwars.webpush.PushService(
                    vapidPublicKey,
                    vapidPrivateKey,
                    "mailto:contato@naesquina.com"
            );

            pushService.send(new nl.martijndwars.webpush.Notification(subscription, payload));
        } catch (Exception e) {
            System.err.println("Erro ao enviar Web Push: " + e.getMessage());
        }
    }

    private void enviarExpoPushNotification(String pushToken, String titulo, String mensagem, Long idPedido) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "to", pushToken,
                    "title", titulo,
                    "body", mensagem,
                    "sound", "default",
                    "data", Map.of("idPedido", idPedido, "tipo", "STATUS_PEDIDO")
            );

            var entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity("https://exp.host/--/api/v2/push/send", entity, String.class);
        } catch (Exception e) {
            System.err.println("Erro ao comunicar com a API do Expo Push: " + e.getMessage());
        }
    }

    private String formatarStatus(StatusPedido status) {
        if (status == null) return "";
        return switch (status) {
            case PENDENTE -> "Pedido Pendente";
            case EM_SEPARACAO -> "Produto em separação";
            case AGUARDANDO_RETIRADA -> "Produto aguardando retirada";
            case RETIRADA_CONCLUIDO -> "Produto Entregue";
            case CANCELADO -> "Pedido Cancelado";
            default -> status.name();
        };
    }
}