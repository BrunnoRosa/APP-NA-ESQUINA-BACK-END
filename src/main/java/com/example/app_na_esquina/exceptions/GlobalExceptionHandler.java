package com.example.app_na_esquina.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Central de erros da API: qualquer exceção que "escapar" de um controller cai aqui
// e vira uma resposta JSON com o status HTTP certo e uma mensagem em português.
//
// Dois formatos de resposta:
//  - Erro de campo (@Valid):  { "email": "Formato de e-mail inválido", "senha": "..." }   -> HTTP 400
//  - Demais erros (StandardError): { timestamp, status, error, message, path }
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<StandardError> montar(HttpStatus status, String titulo, String mensagem,
                                                 HttpServletRequest request) {
        StandardError erro = new StandardError(
                LocalDateTime.now(),
                status.value(),
                titulo,
                mensagem,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(erro);
    }

    // ---------- 400: dados inválidos ----------

    // Falhou alguma anotação do DTO (@NotBlank, @Size, @Pattern...). Devolve { campo: mensagem }.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();

        ex.getBindingResult().getAllErrors().forEach(erro -> {
            String campo = (erro instanceof FieldError fe) ? fe.getField() : erro.getObjectName();
            String codigo = erro.getCode();
            boolean obrigatorio = "NotBlank".equals(codigo) || "NotNull".equals(codigo) || "NotEmpty".equals(codigo);

            // Se o campo falhou em várias regras, a mensagem "é obrigatório" tem prioridade
            // (campo vazio também quebra o tamanho mínimo, mas dizer "obrigatório" é mais claro).
            if (obrigatorio || !erros.containsKey(campo)) {
                erros.put(campo, erro.getDefaultMessage());
            }
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    // JSON quebrado, corpo vazio ou valor que não existe no enum (ex.: perfil = "DONO").
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleCorpoIlegivel(HttpMessageNotReadableException e,
                                                             HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST, "Requisição Inválida",
                "Corpo da requisição ausente, mal formatado ou com valor inválido.", request);
    }

    // Parâmetro de URL com tipo errado (ex.: /api/produtos/abc quando se esperava um número).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardError> handleTipoInvalido(MethodArgumentTypeMismatchException e,
                                                            HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST, "Parâmetro Inválido",
                "O parâmetro '" + e.getName() + "' tem um valor inválido.", request);
    }

    // Parâmetro obrigatório da URL que não foi enviado (ex.: /api/produtos/buscar sem ?nome=).
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<StandardError> handleParametroAusente(MissingServletRequestParameterException e,
                                                                HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST, "Parâmetro Ausente",
                "O parâmetro '" + e.getParameterName() + "' é obrigatório.", request);
    }

    // Regra do sistema violada (lançada por nós nos services).
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<StandardError> handleRegraNegocio(RegraNegocioException e, HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST, "Regra de Negócio", e.getMessage(), request);
    }

    // ---------- 401 / 403 ----------

    // Senha ou e-mail errados no login. A mensagem é a mesma nos dois casos de propósito:
    // assim ninguém descobre quais e-mails existem no sistema.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> handleCredenciais(BadCredentialsException e, HttpServletRequest request) {
        return montar(HttpStatus.UNAUTHORIZED, "Credenciais Inválidas", "E-mail ou senha incorretos.", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardError> handleAcessoNegado(AccessDeniedException e, HttpServletRequest request) {
        return montar(HttpStatus.FORBIDDEN, "Acesso Negado", "Você não tem permissão para esta ação.", request);
    }

    // ---------- 404 / 405 ----------

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<StandardError> handleNaoEncontrado(RecursoNaoEncontradoException e,
                                                             HttpServletRequest request) {
        return montar(HttpStatus.NOT_FOUND, "Não Encontrado", e.getMessage(), request);
    }

    // Rota que não existe (ex.: /api/xyz).
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleRotaInexistente(NoResourceFoundException e,
                                                               HttpServletRequest request) {
        return montar(HttpStatus.NOT_FOUND, "Não Encontrado", "Recurso não encontrado.", request);
    }

    // Rota existe mas o verbo está errado (ex.: GET em uma rota que só aceita POST).
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardError> handleMetodoNaoSuportado(HttpRequestMethodNotSupportedException e,
                                                                  HttpServletRequest request) {
        return montar(HttpStatus.METHOD_NOT_ALLOWED, "Método Não Permitido",
                "Este método HTTP não é aceito nesta rota.", request);
    }

    // ---------- 409: conflito ----------

    // Dado já existe (lançada por nós, ex.: e-mail repetido).
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<StandardError> handleConflito(ConflitoException e, HttpServletRequest request) {
        return montar(HttpStatus.CONFLICT, "Conflito", e.getMessage(), request);
    }

    // Rede de segurança do banco: se duas requisições passarem juntas pela checagem do service,
    // a constraint UNIQUE do MySQL ainda barra e cai aqui.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleIntegridade(DataIntegrityViolationException e,
                                                           HttpServletRequest request) {
        log.warn("Violação de integridade em {}: {}", request.getRequestURI(), e.getMostSpecificCause().getMessage());
        return montar(HttpStatus.CONFLICT, "Violação de Dados",
                "Este registro já existe ou há um conflito com dados existentes.", request);
    }

    // ---------- 500: o que não previmos ----------

    // O detalhe técnico vai só para o log do servidor; o usuário recebe uma mensagem genérica.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGenerico(Exception e, HttpServletRequest request) {
        log.error("Erro inesperado em {}", request.getRequestURI(), e);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno do Servidor",
                "Ocorreu um erro inesperado. Tente novamente em instantes.", request);
    }
}
