package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.pedido.CriarPedidoDTO;
import com.example.app_na_esquina.model.PedidoModel;
import com.example.app_na_esquina.model.enums.StatusPedido;
import com.example.app_na_esquina.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoModel> criarPedido(@RequestBody CriarPedidoDTO dto) {
        PedidoModel novoPedido = pedidoService.criarPedido(dto);
        return ResponseEntity.ok(novoPedido);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoModel>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(pedidoService.buscarPorCliente(idCliente));
    }

    @GetMapping("/mercadinho/{idMercadinho}")
    public ResponseEntity<List<PedidoModel>> listarPorMercadinho(@PathVariable Long idMercadinho) {
        return ResponseEntity.ok(pedidoService.buscarPorMercadinho(idMercadinho));
    }

    @PutMapping("/{idPedido}/status")
    public ResponseEntity<PedidoModel> atualizarStatus(
            @PathVariable Long idPedido,
            @RequestParam StatusPedido status,
            @RequestParam(required = false) String motivo) { // <--- Adicionado parâmetro opcional
        PedidoModel pedidoAtualizado = pedidoService.atualizarStatus(idPedido, status, motivo);
        return ResponseEntity.ok(pedidoAtualizado);
    }
}