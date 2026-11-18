package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.mercadinho.MercadinhoRequestDTO;
import com.example.app_na_esquina.dto.mercadinho.MercadinhoResponseDTO;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.service.MercadinhoService;
import com.example.app_na_esquina.service.UsuarioService;
import com.example.app_na_esquina.util.Validacoes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mercadinhos")
public class MercadinhoController {

    @Autowired
    private MercadinhoService mercadinhoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<MercadinhoResponseDTO>> listarTodos() {
        List<MercadinhoModel> mercadinhos = mercadinhoService.listarTodos();

        List<MercadinhoResponseDTO> response = mercadinhos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MercadinhoResponseDTO> cadastrar(@RequestBody MercadinhoRequestDTO dto) {
        String cnpjDigitos = dto.getCnpj() == null ? "" : dto.getCnpj().replaceAll("\\D", "");
        if (cnpjDigitos.length() != 14) {
            throw new RegraNegocioException("O CNPJ deve ter 14 números.");
        }

        String nomeFantasia = Validacoes.texto(dto.getNomeFantasia(), "o nome do mercadinho", 2, 100);

        Validacoes.Endereco local = null;
        String endereco;
        String cepDigitos;
        if (Validacoes.temEndereco(dto.getRua(), dto.getNumero(), dto.getBairro(), dto.getCidade(), dto.getUf())) {
            local = Validacoes.endereco(dto.getCep(), dto.getRua(), dto.getNumero(), dto.getBairro(),
                    dto.getComplemento(), dto.getCidade(), dto.getUf());
            endereco = local.texto();
            cepDigitos = local.cep();
        } else {
            endereco = Validacoes.texto(dto.getEndereco(), "o endereço", 5, 200);
            cepDigitos = Validacoes.cep(dto.getCep());
        }

        Validacoes.coordenadas(dto.getLatitude(), dto.getLongitude());

        if (dto.getIdComerciante() == null) {
            throw new RegraNegocioException("Informe o comerciante dono do mercadinho.");
        }

        MercadinhoModel model = new MercadinhoModel();
        model.setNomeFantasia(nomeFantasia);
        model.setCnpj(cnpjDigitos);
        model.setEndereco(endereco);
        model.setCep(cepDigitos);
        aplicarEndereco(model, local);
        model.setLatitude(dto.getLatitude());
        model.setLongitude(dto.getLongitude());

        aplicarHorarios(model, dto, true);

        UsuarioModel dono = usuarioService.buscarPorId(dto.getIdComerciante())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Comerciante não encontrado."));
        model.setComerciante(dono);

        MercadinhoModel salvo = mercadinhoService.salvar(model);

        return ResponseEntity.status(201).body(converterParaDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MercadinhoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody MercadinhoRequestDTO dto,
            Authentication authentication) {
        MercadinhoModel mercadinho = mercadinhoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mercadinho não encontrado."));

        if (authentication == null
                || mercadinho.getComerciante() == null
                || !mercadinho.getComerciante().getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new AccessDeniedException("Você não tem permissão para alterar este mercadinho.");
        }

        if (dto.getNomeFantasia() != null)
            mercadinho.setNomeFantasia(Validacoes.texto(dto.getNomeFantasia(), "o nome do mercadinho", 2, 100));

        if (Validacoes.temEndereco(dto.getRua(), dto.getNumero(), dto.getBairro(), dto.getCidade(), dto.getUf())) {
            Validacoes.Endereco local = Validacoes.endereco(dto.getCep(), dto.getRua(), dto.getNumero(),
                    dto.getBairro(), dto.getComplemento(), dto.getCidade(), dto.getUf());
            mercadinho.setEndereco(local.texto());
            mercadinho.setCep(local.cep());
            aplicarEndereco(mercadinho, local);
        } else {
            if (dto.getEndereco() != null)
                mercadinho.setEndereco(Validacoes.texto(dto.getEndereco(), "o endereço", 5, 200));
            if (dto.getCep() != null && !dto.getCep().trim().isEmpty())
                mercadinho.setCep(Validacoes.cep(dto.getCep()));
        }

        Validacoes.coordenadas(dto.getLatitude(), dto.getLongitude());
        if (dto.getLatitude() != null) mercadinho.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) mercadinho.setLongitude(dto.getLongitude());

        aplicarHorarios(mercadinho, dto, false);

        if (dto.getCnpj() != null && !dto.getCnpj().trim().isEmpty()) {
            String cnpjDigitos = dto.getCnpj().replaceAll("\\D", "");
            if (cnpjDigitos.length() != 14) {
                throw new RegraNegocioException("O CNPJ deve ter 14 números.");
            }
            mercadinho.setCnpj(cnpjDigitos);
        }

        MercadinhoModel atualizado = mercadinhoService.salvar(mercadinho);
        return ResponseEntity.ok(converterParaDTO(atualizado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MercadinhoResponseDTO> buscarPorId(@PathVariable Long id) {
        MercadinhoModel mercadinho = mercadinhoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mercadinho não encontrado."));

        return ResponseEntity.ok(converterParaDTO(mercadinho));
    }

    @GetMapping("/proximos")
    public ResponseEntity<List<MercadinhoResponseDTO>> listarProximos(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false, defaultValue = "5.0") Double raioKm) {

        List<MercadinhoModel> mercadinhos = mercadinhoService.listarProximos(lat, lng, raioKm);

        List<MercadinhoResponseDTO> response = mercadinhos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private void aplicarHorarios(MercadinhoModel model, MercadinhoRequestDTO dto, boolean obrigatorio) {
        boolean temDetalhado = dto.getAbreSegSex() != null
                || dto.getAbreSabado() != null
                || dto.getAbreDomingo() != null
                || dto.getHorarioAberturaSegSex() != null
                || dto.getHorarioAberturaSabado() != null
                || dto.getHorarioAberturaDomingo() != null;

        if (temDetalhado) {
            boolean segSex = Boolean.TRUE.equals(dto.getAbreSegSex());
            boolean sabado = Boolean.TRUE.equals(dto.getAbreSabado());
            boolean domingo = Boolean.TRUE.equals(dto.getAbreDomingo());

            if (!segSex && !sabado && !domingo) {
                throw new RegraNegocioException("O mercadinho deve funcionar em pelo menos um período da semana.");
            }

            if (segSex) {
                Validacoes.horarios(dto.getHorarioAberturaSegSex(), dto.getHorarioFechamentoSegSex());
                model.setAbreSegSex(true);
                model.setHorarioAberturaSegSex(dto.getHorarioAberturaSegSex());
                model.setHorarioFechamentoSegSex(dto.getHorarioFechamentoSegSex());
            } else {
                model.setAbreSegSex(false);
                model.setHorarioAberturaSegSex(null);
                model.setHorarioFechamentoSegSex(null);
            }

            if (sabado) {
                Validacoes.horarios(dto.getHorarioAberturaSabado(), dto.getHorarioFechamentoSabado());
                model.setAbreSabado(true);
                model.setHorarioAberturaSabado(dto.getHorarioAberturaSabado());
                model.setHorarioFechamentoSabado(dto.getHorarioFechamentoSabado());
            } else {
                model.setAbreSabado(false);
                model.setHorarioAberturaSabado(null);
                model.setHorarioFechamentoSabado(null);
            }

            if (domingo) {
                Validacoes.horarios(dto.getHorarioAberturaDomingo(), dto.getHorarioFechamentoDomingo());
                model.setAbreDomingo(true);
                model.setHorarioAberturaDomingo(dto.getHorarioAberturaDomingo());
                model.setHorarioFechamentoDomingo(dto.getHorarioFechamentoDomingo());
            } else {
                model.setAbreDomingo(false);
                model.setHorarioAberturaDomingo(null);
                model.setHorarioFechamentoDomingo(null);
            }

            // Sincroniza os campos legados com o primeiro período ativo
            if (segSex) {
                model.setHorarioAbertura(dto.getHorarioAberturaSegSex());
                model.setHorarioFechamento(dto.getHorarioFechamentoSegSex());
            } else if (sabado) {
                model.setHorarioAbertura(dto.getHorarioAberturaSabado());
                model.setHorarioFechamento(dto.getHorarioFechamentoSabado());
            } else {
                model.setHorarioAbertura(dto.getHorarioAberturaDomingo());
                model.setHorarioFechamento(dto.getHorarioFechamentoDomingo());
            }
            return;
        }

        // Compatibilidade com requisições antigas (apenas horarioAbertura e horarioFechamento)
        if (obrigatorio) {
            Validacoes.horarios(dto.getHorarioAbertura(), dto.getHorarioFechamento());
        }
        if (dto.getHorarioAbertura() != null) model.setHorarioAbertura(dto.getHorarioAbertura());
        if (dto.getHorarioFechamento() != null) model.setHorarioFechamento(dto.getHorarioFechamento());

        if (model.getHorarioAbertura() != null && model.getHorarioFechamento() != null) {
            Validacoes.horarios(model.getHorarioAbertura(), model.getHorarioFechamento());
            if (obrigatorio) {
                model.setAbreSegSex(true);
                model.setHorarioAberturaSegSex(model.getHorarioAbertura());
                model.setHorarioFechamentoSegSex(model.getHorarioFechamento());

                model.setAbreSabado(true);
                model.setHorarioAberturaSabado(model.getHorarioAbertura());
                model.setHorarioFechamentoSabado(model.getHorarioFechamento());

                model.setAbreDomingo(true);
                model.setHorarioAberturaDomingo(model.getHorarioAbertura());
                model.setHorarioFechamentoDomingo(model.getHorarioFechamento());
            }
        }
    }

    private void aplicarEndereco(MercadinhoModel model, Validacoes.Endereco e) {
        if (e == null) return;
        model.setRua(e.rua());
        model.setNumero(e.numero());
        model.setBairro(e.bairro());
        model.setComplemento(e.complemento());
        model.setCidade(e.cidade());
        model.setUf(e.uf());
    }

    private MercadinhoResponseDTO converterParaDTO(MercadinhoModel model) {
        MercadinhoResponseDTO dto = new MercadinhoResponseDTO();
        dto.setIdMercadinho(model.getIdMercadinho());
        dto.setNomeFantasia(model.getNomeFantasia());
        dto.setEndereco(model.getEndereco());
        dto.setCep(model.getCep());
        dto.setRua(model.getRua());
        dto.setNumero(model.getNumero());
        dto.setBairro(model.getBairro());
        dto.setComplemento(model.getComplemento());
        dto.setCidade(model.getCidade());
        dto.setUf(model.getUf());
        dto.setLatitude(model.getLatitude());
        dto.setLongitude(model.getLongitude());
        dto.setHorarioAbertura(model.getHorarioAbertura());
        dto.setHorarioFechamento(model.getHorarioFechamento());

        boolean temDetalhado = model.getAbreSegSex() != null
                || model.getAbreSabado() != null
                || model.getAbreDomingo() != null;

        if (temDetalhado) {
            dto.setAbreSegSex(model.getAbreSegSex());
            dto.setHorarioAberturaSegSex(model.getHorarioAberturaSegSex());
            dto.setHorarioFechamentoSegSex(model.getHorarioFechamentoSegSex());

            dto.setAbreSabado(model.getAbreSabado());
            dto.setHorarioAberturaSabado(model.getHorarioAberturaSabado());
            dto.setHorarioFechamentoSabado(model.getHorarioFechamentoSabado());

            dto.setAbreDomingo(model.getAbreDomingo());
            dto.setHorarioAberturaDomingo(model.getHorarioAberturaDomingo());
            dto.setHorarioFechamentoDomingo(model.getHorarioFechamentoDomingo());
        } else {
            // Fallback automático para mercadinhos antigos já salvos no banco
            dto.setAbreSegSex(true);
            dto.setHorarioAberturaSegSex(model.getHorarioAbertura());
            dto.setHorarioFechamentoSegSex(model.getHorarioFechamento());

            dto.setAbreSabado(true);
            dto.setHorarioAberturaSabado(model.getHorarioAbertura());
            dto.setHorarioFechamentoSabado(model.getHorarioFechamento());

            dto.setAbreDomingo(true);
            dto.setHorarioAberturaDomingo(model.getHorarioAbertura());
            dto.setHorarioFechamentoDomingo(model.getHorarioFechamento());
        }

        if (model.getComerciante() != null) {
            dto.setTelefone(model.getComerciante().getTelefone());
        }
        return dto;
    }
}