package com.example.app_na_esquina.config;

import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.repository.UsuarioRepository;
import com.example.app_na_esquina.service.LimpezaContasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// Tarefa agendada: de hora em hora apaga as contas que não confirmaram o e-mail em 48 h.
@Component
public class LimpezaContasJob {

    private static final Logger log = LoggerFactory.getLogger(LimpezaContasJob.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LimpezaContasService limpezaService;

    @Value("${app.verificacao.limpeza-horas:48}")
    private long horasParaApagar;

    @Scheduled(cron = "0 0 * * * *") // minuto 0 de cada hora
    public void apagarContasNaoConfirmadas() {
        LocalDateTime limite = LocalDateTime.now().minusHours(horasParaApagar);
        // Contas antigas (sem data_criacao) nunca entram aqui: a data só existe para contas novas
        List<UsuarioModel> vencidas = usuarioRepository.findByEmailVerificadoFalseAndDataCriacaoBefore(limite);
        for (UsuarioModel u : vencidas) {
            try {
                limpezaService.excluirContaNaoConfirmada(u.getIdUsuario());
                log.info("Conta não confirmada apagada: id {}", u.getIdUsuario());
            } catch (Exception e) {
                log.warn("Não foi possível apagar a conta {} (provavelmente tem vínculos): {}",
                        u.getIdUsuario(), e.getMessage());
            }
        }
    }
}
