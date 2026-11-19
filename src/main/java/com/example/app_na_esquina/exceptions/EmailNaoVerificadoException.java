package com.example.app_na_esquina.exceptions;

// Lançada no login quando a senha está certa, mas o e-mail ainda não foi confirmado.
// O GlobalExceptionHandler transforma em HTTP 403 ("E-mail não confirmado").
public class EmailNaoVerificadoException extends RuntimeException {

    public EmailNaoVerificadoException(String mensagem) {
        super(mensagem);
    }
}
