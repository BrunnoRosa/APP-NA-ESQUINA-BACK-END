package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.pedido.CriarPedidoDTO;
import com.example.app_na_esquina.dto.pedido.EditarItensPedidoDTO;
import com.example.app_na_esquina.dto.pedido.PedidoResponseDTO;
import com.example.app_na_esquina.model.enums.StatusPedido;
import com.example.app_na_esquina.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    // Atualiza status geral (separação, pronto para retirada, concluído ou cancelado pela loja)
    @PutMapping("/{idPedido}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @PathVariable Long idPedido,
            @RequestParam StatusPedido status,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(idPedido, status, motivo));
    }

    // 1. Cliente cancela o pedido com justificativa
    @PutMapping("/{idPedido}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelarPeloCliente(
            @PathVariable Long idPedido,
            @RequestParam(required = false) String motivo,
            Authentication authentication) {
        return ResponseEntity.ok(
                pedidoService.cancelarPeloCliente(idPedido, authentication.getName(), motivo)
        );
    }

    // 2. Comerciante remove/ajusta itens indisponíveis e devolve para aprovação do cliente
    @PutMapping("/{idPedido}/editar-itens")
    public ResponseEntity<PedidoResponseDTO> editarItensPedido(
            @PathVariable Long idPedido,
            @RequestBody EditarItensPedidoDTO dto) {
        return ResponseEntity.ok(pedidoService.editarItensPedido(idPedido, dto));
    }

    // 3. Cliente aprova ou recusa o pedido ajustado pelo comerciante
    @PutMapping("/{idPedido}/responder-alteracao")
    public ResponseEntity<PedidoResponseDTO> responderAlteracaoCliente(
            @PathVariable Long idPedido,
            @RequestParam boolean aprovado,
            @RequestParam(required = false) String motivo,
            Authentication authentication) {
        return ResponseEntity.ok(
                pedidoService.responderAlteracaoCliente(idPedido, authentication.getName(), aprovado, motivo)
        );
    }
}