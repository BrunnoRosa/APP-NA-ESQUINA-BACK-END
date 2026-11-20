package com.example.app_na_esquina.service;

import com.example.app_na_esquina.dto.avaliacao.AvaliacaoResponseDTO;
import com.example.app_na_esquina.dto.avaliacao.CriarAvaliacaoDTO;
import com.example.app_na_esquina.dto.avaliacao.ResumoAvaliacoesMercadinhoDTO;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;
import com.example.app_na_esquina.model.AvaliacaoModel;
import com.example.app_na_esquina.model.PedidoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.model.enums.StatusPedido;
import com.example.app_na_esquina.repository.AvaliacaoRepository;
import com.example.app_na_esquina.repository.PedidoRepository;
import com.example.app_na_esquina.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public AvaliacaoResponseDTO avaliarPedido(CriarAvaliacaoDTO dto, String emailUsuario) {
        if (dto.getIdPedido() == null) {
            throw new RegraNegocioException("Informe o pedido a ser avaliado.");
        }

        validarNota(dto.getNotaAtendimento(), "Atendimento");
        validarNota(dto.getNotaProdutos(), "Produtos");
        validarNota(dto.getNotaLocalizacao(), "Localização");
        validarNota(dto.getNotaAgilidade(), "Agilidade");
        validarNota(dto.getNotaCustoBeneficio(), "Custo-Benefício");

        PedidoModel pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));

        if (!pedido.getCliente().getEmail().equalsIgnoreCase(emailUsuario)) {
            throw new RegraNegocioException("Você só pode avaliar os seus próprios pedidos.");
        }

        if (pedido.getStatus() != StatusPedido.RETIRADA_CONCLUIDO) {
            throw new RegraNegocioException("Só é possível avaliar pedidos já concluídos.");
        }

        if (avaliacaoRepository.existsByPedidoIdPedido(pedido.getIdPedido())) {
            throw new RegraNegocioException("Este pedido já foi avaliado.");
        }

        double soma = dto.getNotaAtendimento()
                + dto.getNotaProdutos()
                + dto.getNotaLocalizacao()
                + dto.getNotaAgilidade()
                + dto.getNotaCustoBeneficio();
        double media = Math.round((soma / 5.0) * 10.0) / 10.0;

        String comentarioLimpo = null;
        if (dto.getComentario() != null && !dto.getComentario().isBlank()) {
            comentarioLimpo = dto.getComentario().trim();
            if (comentarioLimpo.length() > 500) {
                comentarioLimpo = comentarioLimpo.substring(0, 500);
            }
        }

        AvaliacaoModel avaliacao = new AvaliacaoModel();
        avaliacao.setPedido(pedido);
        avaliacao.setCliente(pedido.getCliente());
        avaliacao.setMercadinho(pedido.getMercadinho());
        avaliacao.setNotaAtendimento(dto.getNotaAtendimento());
        avaliacao.setNotaProdutos(dto.getNotaProdutos());
        avaliacao.setNotaLocalizacao(dto.getNotaLocalizacao());
        avaliacao.setNotaAgilidade(dto.getNotaAgilidade());
        avaliacao.setNotaCustoBeneficio(dto.getNotaCustoBeneficio());
        avaliacao.setNotaGeral(media);
        avaliacao.setComentario(comentarioLimpo);

        return AvaliacaoResponseDTO.de(avaliacaoRepository.save(avaliacao));
    }

    @Transactional(readOnly = true)
    public ResumoAvaliacoesMercadinhoDTO obterResumoPorMercadinho(Long idMercadinho) {
        List<AvaliacaoModel> lista = avaliacaoRepository
                .findByMercadinhoIdMercadinhoOrderByDataAvaliacaoDesc(idMercadinho);

        ResumoAvaliacoesMercadinhoDTO resumo = new ResumoAvaliacoesMercadinhoDTO();
        resumo.setIdMercadinho(idMercadinho);
        resumo.setTotalAvaliacoes(lista.size());
        resumo.setAvaliacoes(lista.stream().map(AvaliacaoResponseDTO::de).toList());

        if (lista.isEmpty()) {
            return resumo;
        }

        double somaGeral = 0, somaAtend = 0, somaProd = 0, somaLoc = 0, somaAgil = 0, somaCusto = 0;
        for (AvaliacaoModel a : lista) {
            somaGeral += a.getNotaGeral();
            somaAtend += a.getNotaAtendimento();
            somaProd += a.getNotaProdutos();
            somaLoc += a.getNotaLocalizacao();
            somaAgil += a.getNotaAgilidade();
            somaCusto += a.getNotaCustoBeneficio();
        }

        int total = lista.size();
        resumo.setMediaGeral(arredondar(somaGeral / total));
        resumo.setMediaAtendimento(arredondar(somaAtend / total));
        resumo.setMediaProdutos(arredondar(somaProd / total));
        resumo.setMediaLocalizacao(arredondar(somaLoc / total));
        resumo.setMediaAgilidade(arredondar(somaAgil / total));
        resumo.setMediaCustoBeneficio(arredondar(somaCusto / total));

        return resumo;
    }

    @Transactional(readOnly = true)
    public List<Long> listarIdsPedidosAvaliados(String emailUsuario) {
        UsuarioModel usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        return avaliacaoRepository.findByClienteIdUsuario(usuario.getIdUsuario()).stream()
                .map(a -> a.getPedido().getIdPedido())
                .toList();
    }

    private void validarNota(Integer nota, String criterio) {
        if (nota == null || nota < 1 || nota > 5) {
            throw new RegraNegocioException("A nota de " + criterio + " deve ser entre 1 e 5 estrelas.");
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, Map<String, Object>> obterMediasPorMercadinho() {
        List<Object[]> resultados = avaliacaoRepository.calcularMediasPorMercadinho();
        Map<Long, Map<String, Object>> mapa = new HashMap<>();

        for (Object[] row : resultados) {
            Long idMercadinho = ((Number) row[0]).longValue();
            double media = row[1] != null ? arredondar(((Number) row[1]).doubleValue()) : 0.0;
            long total = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            mapa.put(idMercadinho, Map.of(
                    "mediaGeral", media,
                    "totalAvaliacoes", total
            ));
        }
        return mapa;
    }

    private double arredondar(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }
}