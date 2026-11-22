package com.example.app_na_esquina.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

// Envia os e-mails do sistema via API HTTPS do Brevo (porta 443, liberada no Railway).
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final URI BREVO_ENDPOINT = URI.create("https://api.brevo.com/v3/smtp/email");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${brevo.api-key:}")
    private String apiKey;

    @Value("${brevo.sender-email:}")
    private String remetente;

    @Value("${brevo.sender-name:Na Esquina}")
    private String nomeRemetente;

    @Value("${app.verificacao.horas:24}")
    private long horasValidade;

    @PostConstruct
    void avisarConfiguracao() {
        if (apiKey == null || apiKey.isBlank() || remetente == null || remetente.isBlank()) {
            log.warn("E-mail NÃO configurado: BREVO_API_KEY ou remetente vazio. Os links de confirmação aparecerão só no log.");
        } else {
            log.info("E-mail configurado via API Brevo. Remetente: {}", remetente);
        }
    }

    @Async
    public void enviarConfirmacaoCadastro(String para, String nome, String link) {
        if (!configurado()) {
            log.warn("E-mail NÃO configurado (BREVO_API_KEY vazia). Link de confirmação de {}: {}", para, link);
            return;
        }
        enviarPeloBrevo(para, nome, "Confirme seu e-mail - Na Esquina", montarHtml(nome, link), "confirmação");
    }

    // Código de 6 dígitos da recuperação de senha (cliente e comerciante usam o mesmo e-mail).
    @Async
    public void enviarCodigoRecuperacao(String para, String nome, String codigo, long minutosValidade) {
        if (!configurado()) {
            log.warn("E-mail NÃO configurado (BREVO_API_KEY vazia). Código de recuperação de {}: {}", para, codigo);
            return;
        }
        enviarPeloBrevo(para, nome, "Código para redefinir sua senha - Na Esquina",
                montarHtmlRecuperacao(nome, codigo, minutosValidade), "recuperação de senha");
    }

    private boolean configurado() {
        return apiKey != null && !apiKey.isBlank() && remetente != null && !remetente.isBlank();
    }

    // Envio comum a todos os e-mails: uma chamada HTTPS à API do Brevo.
    // Se falhar, só registra no log (quem pediu já recebeu a resposta da rota).
    private void enviarPeloBrevo(String para, String nome, String assunto, String html, String tipo) {
        try {
            String nomeDestino = (nome == null || nome.isBlank()) ? para : nome.trim();

            String jsonBody = "{"
                    + "\"sender\":{\"name\":\"" + escaparJson(nomeRemetente) + "\",\"email\":\"" + escaparJson(remetente) + "\"},"
                    + "\"to\":[{\"email\":\"" + escaparJson(para) + "\",\"name\":\"" + escaparJson(nomeDestino) + "\"}],"
                    + "\"subject\":\"" + escaparJson(assunto) + "\","
                    + "\"htmlContent\":\"" + escaparJson(html) + "\""
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(BREVO_ENDPOINT)
                    .timeout(Duration.ofSeconds(15))
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("api-key", apiKey.trim())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("E-mail de {} enviado para {}", tipo, para);
            } else {
                log.error("Falha ao enviar e-mail de {} para {} (HTTP {}): {}",
                        tipo, para, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de {} para {}: {}", tipo, para, e.getMessage());
        }
    }

    private String montarHtmlRecuperacao(String nome, String codigo, long minutosValidade) {
        String primeiroNome = nome == null ? "" : HtmlUtils.htmlEscape(nome.trim().split("\\s+")[0]);
        return "<div style=\"font-family:Arial,sans-serif;max-width:480px;margin:auto;color:#333\">"
                + "<h2 style=\"color:#FF3131\">Na Esquina</h2>"
                + "<p>Olá, " + primeiroNome + "!</p>"
                + "<p>Use o código abaixo no app para criar uma nova senha:</p>"
                + "<p style=\"text-align:center;margin:28px 0;font-size:34px;font-weight:bold;"
                + "letter-spacing:8px;color:#FF3131\">" + codigo + "</p>"
                + "<p style=\"font-size:13px;color:#666\">O código vale por " + minutosValidade + " minutos "
                + "e só pode ser usado uma vez.</p>"
                + "<p style=\"font-size:13px;color:#666\">Se você não pediu a troca de senha, ignore este e-mail: "
                + "sua senha continua a mesma.</p>"
                + "</div>";
    }

    private String montarHtml(String nome, String link) {
        String primeiroNome = nome == null ? "" : HtmlUtils.htmlEscape(nome.trim().split("\\s+")[0]);
        return "<div style=\"font-family:Arial,sans-serif;max-width:480px;margin:auto;color:#333\">"
                + "<h2 style=\"color:#FF3131\">Na Esquina</h2>"
                + "<p>Olá, " + primeiroNome + "!</p>"
                + "<p>Para ativar sua conta, confirme seu e-mail clicando no botão abaixo:</p>"
                + "<p style=\"text-align:center;margin:28px 0\">"
                + "<a href=\"" + link + "\" style=\"background:#FF3131;color:#fff;padding:12px 24px;"
                + "border-radius:8px;text-decoration:none;font-weight:bold\">Confirmar meu e-mail</a></p>"
                + "<p style=\"font-size:13px;color:#666\">Este link vale por " + horasValidade + " horas. "
                + "Se o botão não funcionar, copie e cole no navegador:<br>" + link + "</p>"
                + "<p style=\"font-size:13px;color:#666\">Se você não criou uma conta no Na Esquina, ignore este e-mail.</p>"
                + "</div>";
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        StringBuilder sb = new StringBuilder(texto.length() + 16);
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}