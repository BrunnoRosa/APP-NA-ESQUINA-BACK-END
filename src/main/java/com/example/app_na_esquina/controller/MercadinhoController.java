package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.mercadinho.MercadinhoRequestDTO;
import com.example.app_na_esquina.dto.mercadinho.MercadinhoResponseDTO;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.service.MercadinhoService;
import com.example.app_na_esquina.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import com.example.app_na_esquina.util.Validacoes;

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

        // Validações (util/Validacoes): cada uma devolve o valor limpo ou lança erro 400
        String nomeFantasia = Validacoes.texto(dto.getNomeFantasia(), "o nome do mercadinho", 2, 100);

        // Endereço: se vier em campos separados (rua, número, bairro...), valida cada um e monta o texto;
        // se não, aceita o texto único antigo em "endereco".
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
            cepDigitos = Validacoes.cep(dto.getCep()); // guarda só os 8 números
        }
        Validacoes.horarios(dto.getHorarioAbertura(), dto.getHorarioFechamento());
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
        model.setHorarioAbertura(dto.getHorarioAbertura());
        model.setHorarioFechamento(dto.getHorarioFechamento());

        // Vincula o dono ao mercadinho
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

        // Só o DONO do mercadinho pode alterá-lo (o e-mail vem do token JWT)
        if (authentication == null
                || mercadinho.getComerciante() == null
                || !mercadinho.getComerciante().getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new AccessDeniedException("Você não tem permissão para alterar este mercadinho.");
        }

        if (dto.getNomeFantasia() != null)
            mercadinho.setNomeFantasia(Validacoes.texto(dto.getNomeFantasia(), "o nome do mercadinho", 2, 100));
        if (Validacoes.temEndereco(dto.getRua(), dto.getNumero(), dto.getBairro(), dto.getCidade(), dto.getUf())) {
            // Endereço em campos separados: valida tudo, monta o texto e guarda cada parte
            Validacoes.Endereco local = Validacoes.endereco(dto.getCep(), dto.getRua(), dto.getNumero(),
                    dto.getBairro(), dto.getComplemento(), dto.getCidade(), dto.getUf());
            mercadinho.setEndereco(local.texto());
            mercadinho.setCep(local.cep());
            aplicarEndereco(mercadinho, local);
        } else {
            // Formato antigo (texto único), mantido para não quebrar quem ainda envia assim
            if (dto.getEndereco() != null)
                mercadinho.setEndereco(Validacoes.texto(dto.getEndereco(), "o endereço", 5, 200));
            if (dto.getCep() != null && !dto.getCep().trim().isEmpty())
                mercadinho.setCep(Validacoes.cep(dto.getCep()));
        }

        Validacoes.coordenadas(dto.getLatitude(), dto.getLongitude());
        if (dto.getLatitude() != null) mercadinho.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) mercadinho.setLongitude(dto.getLongitude());

        if (dto.getHorarioAbertura() != null) mercadinho.setHorarioAbertura(dto.getHorarioAbertura());
        if (dto.getHorarioFechamento() != null) mercadinho.setHorarioFechamento(dto.getHorarioFechamento());
        // Confere a combinação final (mesmo que só um dos dois horários tenha sido enviado)
        if (mercadinho.getHorarioAbertura() != null && mercadinho.getHorarioFechamento() != null) {
            Validacoes.horarios(mercadinho.getHorarioAbertura(), mercadinho.getHorarioFechamento());
        }

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

    // Copia as partes do endereço (já validadas) para o mercadinho. Se "e" é nulo, não faz nada.
    private void aplicarEndereco(MercadinhoModel model, Validacoes.Endereco e) {
        if (e == null) return;
        model.setRua(e.rua());
        model.setNumero(e.numero());
        model.setBairro(e.bairro());
        model.setComplemento(e.complemento());
        model.setCidade(e.cidade());
        model.setUf(e.uf());
    }
    // Método auxiliar para conversão de DTO
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
        if (model.getComerciante() != null) {
            dto.setTelefone(model.getComerciante().getTelefone());
        }
        return dto;
    }
}