package com.example.app_na_esquina.exceptions;

// Lançada quando o pedido do usuário é válido no formato, mas quebra uma regra do sistema
// (ex.: preço zero, perfil não permitido, pedido sem itens).
// O GlobalExceptionHandler transforma em HTTP 400.
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
