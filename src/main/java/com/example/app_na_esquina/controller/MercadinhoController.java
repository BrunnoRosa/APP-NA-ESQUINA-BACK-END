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
        MercadinhoModel model = new MercadinhoModel();
        model.setNomeFantasia(dto.getNomeFantasia());
        model.setCnpj(dto.getCnpj());
        model.setEndereco(dto.getEndereco());
        model.setLatitude(dto.getLatitude());
        model.setLongitude(dto.getLongitude());
        model.setHorarioAbertura(dto.getHorarioAbertura());
        model.setHorarioFechamento(dto.getHorarioFechamento());

        // Vincula o dono ao mercadinho
        UsuarioModel dono = usuarioService.buscarPorId(dto.getIdComerciante())
                .orElseThrow(() -> new RuntimeException("Comerciante não encontrado"));
        model.setComerciante(dono);

        MercadinhoModel salvo = mercadinhoService.salvar(model);

        return ResponseEntity.status(201).body(converterParaDTO(salvo));
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

    // Método auxiliar para não repetir código
    private MercadinhoResponseDTO converterParaDTO(MercadinhoModel model) {
        MercadinhoResponseDTO dto = new MercadinhoResponseDTO();
        dto.setIdMercadinho(model.getIdMercadinho());
        dto.setNomeFantasia(model.getNomeFantasia());
        dto.setEndereco(model.getEndereco());
        dto.setLatitude(model.getLatitude());
        dto.setLongitude(model.getLongitude());
        dto.setHorarioAbertura(model.getHorarioAbertura());
        dto.setHorarioFechamento(model.getHorarioFechamento());
        return dto;
    }
}