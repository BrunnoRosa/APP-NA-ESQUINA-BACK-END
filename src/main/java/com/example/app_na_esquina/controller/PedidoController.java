package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.pedido.CriarPedidoDTO;
import com.example.app_na_esquina.model.enums.StatusPedido;
import com.example.app_na_esquina.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.app_na_esquina.dto.pedido.PedidoResponseDTO;   // 🆕


import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody CriarPedidoDTO dto) {
        return ResponseEntity.ok(pedidoService.criarPedido(dto));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(pedidoService.buscarPorCliente(idCliente));
    }

    @GetMapping("/mercadinho/{idMercadinho}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorMercadinho(@PathVariable Long idMercadinho) {
        return ResponseEntity.ok(pedidoService.buscarPorMercadinho(idMercadinho));
    }

    @PutMapping("/{idPedido}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Long idPedido,
            @RequestParam StatusPedido status,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(idPedido, status, motivo));
    }
}