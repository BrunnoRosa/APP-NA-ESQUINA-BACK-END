package com.example.app_na_esquina.exceptions;

// Lançada quando algo que o usuário pediu não existe (usuário, mercadinho, produto, pedido...).
// O GlobalExceptionHandler transforma em HTTP 404.
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
