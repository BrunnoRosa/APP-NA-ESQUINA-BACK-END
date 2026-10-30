package com.example.app_na_esquina.exceptions;

// Lançada quando o dado já existe (ex.: e-mail ou CNPJ já cadastrado).
// O GlobalExceptionHandler transforma em HTTP 409.
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
