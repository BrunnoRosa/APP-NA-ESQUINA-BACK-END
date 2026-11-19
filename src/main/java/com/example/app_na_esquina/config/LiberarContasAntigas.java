package com.example.app_na_esquina.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// Roda na subida do back. Ao criar a coluna email_verificado, todas as contas que JÁ existiam ficariam
// "não confirmadas" e perderiam o acesso. Contas antigas não têm data_criacao (a coluna é nova),
// enquanto toda conta nova nasce com a data preenchida. Então: sem data_criacao = conta antiga = liberada.
// É seguro rodar várias vezes (não altera contas novas).
@Component
public class LiberarContasAntigas implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(LiberarContasAntigas.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            int n = jdbcTemplate.update(
                    "UPDATE tb_usuarios SET email_verificado = 1 WHERE data_criacao IS NULL AND email_verificado = 0");
            if (n > 0) log.info("{} conta(s) antiga(s) liberada(s) da confirmação de e-mail.", n);
        } catch (Exception e) {
            log.warn("Não foi possível liberar contas antigas: {}", e.getMessage());
        }
    }
}
