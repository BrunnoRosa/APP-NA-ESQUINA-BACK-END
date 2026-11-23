package com.example.app_na_esquina.service;

import com.example.app_na_esquina.exceptions.RegraNegocioException;
import com.example.app_na_esquina.model.TokenRecuperacaoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.repository.TokenRecuperacaoRepository;
import com.example.app_na_esquina.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

// Recuperação de senha por código de 6 dígitos enviado ao e-mail.
// Vale para qualquer perfil (cliente, comerciante...): a conta é achada pelo e-mail.
@Service
public class RecuperacaoSenhaService {

    private static final long INTERVALO_ENTRE_ENVIOS_SEGUNDOS = 60;
    private static final int MAX_TENTATIVAS = 5;
    private static final String MSG_CODIGO_INVALIDO =
            "Código inválido ou expirado. Peça um novo código e tente novamente.";

    private final SecureRandom aleatorio = new SecureRandom();

    @Autowired
    private TokenRecuperacaoRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.recuperacao.minutos:15}")
    private long minutosValidade;

    @Value("${app.jwt.secret}")
    private String segredo;

    /**
     * Gera e envia o código. Não informa nada a quem chamou (a rota responde sempre igual),
     * para não revelar quais e-mails têm conta.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void solicitar(String email) {
        if (email == null || email.isBlank()) return;

        Optional<UsuarioModel> achado = usuarioRepository.findByEmail(email.trim().toLowerCase());
        if (achado.isEmpty()) return;
        UsuarioModel usuario = achado.get();

        Optional<TokenRecuperacaoModel> anterior = tokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        if (anterior.isPresent()
                && anterior.get().getCriadoEm().plusSeconds(INTERVALO_ENTRE_ENVIOS_SEGUNDOS).isAfter(LocalDateTime.now())) {
            return; // pediu há menos de 60 s
        }
        tokenRepository.deleteByUsuarioIdUsuario(usuario.getIdUsuario());
        tokenRepository.flush();

        String codigo = String.format("%06d", aleatorio.nextInt(1_000_000));
        tokenRepository.save(new TokenRecuperacaoModel(
                usuario, hash(usuario.getIdUsuario(), codigo), LocalDateTime.now().plusMinutes(minutosValidade)));

        emailService.enviarCodigoRecuperacao(usuario.getEmail(), usuario.getNome(), codigo, minutosValidade);
    }

    /**
     * Confere o código e troca a senha. Qualquer falha (e-mail inexistente, código errado,
     * expirado ou com tentativas esgotadas) responde com a MESMA mensagem.
     * noRollbackFor: o contador de tentativas precisa ser salvo mesmo quando lançamos o erro.
     */
    @Transactional(noRollbackFor = RegraNegocioException.class)
    public void redefinir(String email, String codigo, String novaSenha, String confirmarSenha) {
        if (confirmarSenha == null || !novaSenha.equals(confirmarSenha)) {
            throw new RegraNegocioException("As senhas informadas não coincidem.");
        }

        UsuarioModel usuario = usuarioRepository.findByEmail(email == null ? "" : email.trim().toLowerCase())
                .orElseThrow(() -> new RegraNegocioException(MSG_CODIGO_INVALIDO));

        TokenRecuperacaoModel token = tokenRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new RegraNegocioException(MSG_CODIGO_INVALIDO));

        if (token.getExpiraEm().isBefore(LocalDateTime.now()) || token.getTentativas() >= MAX_TENTATIVAS) {
            tokenRepository.delete(token);
            throw new RegraNegocioException(MSG_CODIGO_INVALIDO);
        }

        boolean confere = MessageDigest.isEqual(
                hash(usuario.getIdUsuario(), codigo == null ? "" : codigo.trim()).getBytes(StandardCharsets.UTF_8),
                token.getCodigoHash().getBytes(StandardCharsets.UTF_8));
        if (!confere) {
            token.setTentativas(token.getTentativas() + 1);
            tokenRepository.save(token);
            throw new RegraNegocioException(MSG_CODIGO_INVALIDO);
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        // Receber o código no e-mail prova que a pessoa controla a caixa: libera a conta se estava pendente
        usuario.setEmailVerificado(true);
        usuarioRepository.save(usuario);
        tokenRepository.delete(token);
    }

    // HMAC com o segredo do servidor: se o banco vazar, 1 milhão de códigos não dá para testar offline.
    private String hash(Long idUsuario, String codigo) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal((idUsuario + ":" + codigo).getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("HmacSHA256 indisponível", e);
        }
    }
}
