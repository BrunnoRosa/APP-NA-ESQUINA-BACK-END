package com.example.app_na_esquina.service;

import com.example.app_na_esquina.model.TokenVerificacaoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.repository.TokenVerificacaoRepository;
import com.example.app_na_esquina.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

// Gera, envia e confirma o link de verificação de e-mail.
@Service
public class VerificacaoEmailService {

    public enum Resultado { CONFIRMADO, EXPIRADO, INVALIDO }

    // Espera mínima entre dois envios para o mesmo usuário (evita spam na caixa de entrada)
    private static final long INTERVALO_REENVIO_SEGUNDOS = 60;

    private final SecureRandom aleatorio = new SecureRandom();

    @Autowired
    private TokenVerificacaoRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.verificacao.horas:24}")
    private long horasValidade;

    @Value("${app.public-url:http://localhost:8080}")
    private String urlPublicaDoBack;

    /** Cria um token novo (apaga o anterior) e dispara o e-mail com o link. */
    @Transactional
    public void enviarLink(UsuarioModel usuario) {
        tokenRepository.deleteByUsuarioIdUsuario(usuario.getIdUsuario());

        byte[] bytes = new byte[32];
        aleatorio.nextBytes(bytes);
        String tokenBruto = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        tokenRepository.save(new TokenVerificacaoModel(
                hash(tokenBruto), usuario, LocalDateTime.now().plusHours(horasValidade)));

        String base = urlPublicaDoBack.replaceAll("/+$", "");
        emailService.enviarConfirmacaoCadastro(
                usuario.getEmail(), usuario.getNome(), base + "/api/auth/verificar-email?token=" + tokenBruto);
    }

    /** Valida o token do link e marca o e-mail como confirmado. */
    @Transactional
    public Resultado confirmar(String tokenBruto) {
        if (tokenBruto == null || tokenBruto.isBlank()) return Resultado.INVALIDO;

        Optional<TokenVerificacaoModel> achado = tokenRepository.findByTokenHash(hash(tokenBruto.trim()));
        if (achado.isEmpty()) return Resultado.INVALIDO;

        TokenVerificacaoModel token = achado.get();
        UsuarioModel usuario = token.getUsuario();

        // Clicou duas vezes no mesmo link: a conta já está confirmada, então é sucesso
        if (usuario.isEmailVerificado()) return Resultado.CONFIRMADO;

        if (token.getExpiraEm().isBefore(LocalDateTime.now())) return Resultado.EXPIRADO;

        usuario.setEmailVerificado(true);
        usuarioRepository.save(usuario);
        token.setUsadoEm(LocalDateTime.now());
        tokenRepository.save(token);
        return Resultado.CONFIRMADO;
    }

    /**
     * Reenvia o link se a conta existir e ainda não estiver confirmada.
     * Não informa nada a quem chamou (a rota responde sempre igual), para não revelar quais e-mails existem.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reenviar(String email) {
        if (email == null || email.isBlank()) return;

        Optional<UsuarioModel> achado = usuarioRepository.findByEmail(email.trim().toLowerCase());
        if (achado.isEmpty() || achado.get().isEmailVerificado()) return;

        UsuarioModel usuario = achado.get();
        Optional<TokenVerificacaoModel> ultimo = tokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        if (ultimo.isPresent()
                && ultimo.get().getCriadoEm().plusSeconds(INTERVALO_REENVIO_SEGUNDOS).isAfter(LocalDateTime.now())) {
            return;
        }
        enviarLink(usuario);
    }

    private String hash(String tokenBruto) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(tokenBruto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
