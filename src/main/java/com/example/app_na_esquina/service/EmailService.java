package com.example.app_na_esquina.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

// Envia os e-mails do sistema pela conta configurada em spring.mail.* (appnaesquina@gmail.com).
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String remetente;

    @Value("${app.verificacao.horas:24}")
    private long horasValidade;

    // Mostra na subida se o e-mail foi configurado (nunca imprime a senha)
    @PostConstruct
    void avisarConfiguracao() {
        if (remetente == null || remetente.isBlank()) {
            log.warn("E-mail NÃO configurado: MAIL_USERNAME vazio. Os links de confirmação aparecerão só no log.");
        } else {
            log.info("E-mail configurado. Remetente: {}", remetente);
        }
    }

    // @Async: o cadastro responde na hora e o envio roda em outra thread.
    // Se o envio falhar, só registra no log: a conta já foi criada e o usuário pode pedir reenvio.
    @Async
    public void enviarConfirmacaoCadastro(String para, String nome, String link) {
        // Sem MAIL_USERNAME configurado (ex.: rodando local sem senha de app), mostra o link no console.
        if (remetente == null || remetente.isBlank()) {
            log.warn("E-mail NÃO configurado (MAIL_USERNAME vazio). Link de confirmação de {}: {}", para, link);
            return;
        }
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setFrom(remetente, "Na Esquina");
            helper.setTo(para);
            helper.setSubject("Confirme seu e-mail - Na Esquina");
            helper.setText(montarHtml(nome, link), true);
            mailSender.send(msg);
            log.info("E-mail de confirmação enviado para {}", para);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de confirmação para {}: {}", para, e.getMessage());
        }
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
}
