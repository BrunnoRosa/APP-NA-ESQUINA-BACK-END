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

        MercadinhoModel model = new MercadinhoModel();
        model.setNomeFantasia(dto.getNomeFantasia());
        model.setCnpj(cnpjDigitos);
        model.setEndereco(dto.getEndereco());
        model.setCep(dto.getCep());
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
    public ResponseEntity<MercadinhoResponseDTO> atualizar(@PathVariable Long id, @RequestBody MercadinhoRequestDTO dto) {
        MercadinhoModel mercadinho = mercadinhoService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mercadinho não encontrado."));

        if (dto.getNomeFantasia() != null) mercadinho.setNomeFantasia(dto.getNomeFantasia());
        if (dto.getEndereco() != null) mercadinho.setEndereco(dto.getEndereco());
        if (dto.getCep() != null) mercadinho.setCep(dto.getCep());
        if (dto.getLatitude() != null) mercadinho.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) mercadinho.setLongitude(dto.getLongitude());
        if (dto.getHorarioAbertura() != null) mercadinho.setHorarioAbertura(dto.getHorarioAbertura());
        if (dto.getHorarioFechamento() != null) mercadinho.setHorarioFechamento(dto.getHorarioFechamento());

        if (dto.getCnpj() != null && !dto.getCnpj().trim().isEmpty()) {
            String cnpjDigitos = dto.getCnpj().replaceAll("\\D", "");
            if (cnpjDigitos.length() == 14) {
                mercadinho.setCnpj(cnpjDigitos);
            }
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

    // Método auxiliar para conversão de DTO
    private MercadinhoResponseDTO converterParaDTO(MercadinhoModel model) {
        MercadinhoResponseDTO dto = new MercadinhoResponseDTO();
        dto.setIdMercadinho(model.getIdMercadinho());
        dto.setNomeFantasia(model.getNomeFantasia());
        dto.setEndereco(model.getEndereco());
        dto.setCep(model.getCep());
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