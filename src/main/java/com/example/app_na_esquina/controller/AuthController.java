package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.auth.LoginRequestDTO;
import com.example.app_na_esquina.dto.auth.LoginResponseDTO;
import com.example.app_na_esquina.dto.auth.RecuperarSenhaRequestDTO;
import com.example.app_na_esquina.dto.auth.RedefinirSenhaRequestDTO;
import com.example.app_na_esquina.dto.usuario.UsuarioRequestDTO;
import com.example.app_na_esquina.exceptions.EmailNaoVerificadoException;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.model.enums.PerfilUsuario;
import com.example.app_na_esquina.repository.MercadinhoRepository;
import com.example.app_na_esquina.security.CustomUserDetailsService;
import com.example.app_na_esquina.security.JwtService;
import com.example.app_na_esquina.service.RecuperacaoSenhaService;
import com.example.app_na_esquina.service.UsuarioService;
import com.example.app_na_esquina.service.VerificacaoEmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MercadinhoRepository mercadinhoRepository;

    @Autowired
    private VerificacaoEmailService verificacaoEmailService;

    @Autowired
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @Value("${app.front-url:http://localhost:8081}")
    private String urlDoFront;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        // Se e-mail/senha estiverem errados, authenticate() lança BadCredentialsException
        // e o GlobalExceptionHandler responde 401 "E-mail ou senha incorretos."
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        UsuarioModel usuario = usuarioService.buscarPorEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        // Senha correta, mas o e-mail ainda não foi confirmado pelo link: não entrega o token
        if (!usuario.isEmailVerificado()) {
            throw new EmailNaoVerificadoException("Confirme seu e-mail pelo link que enviamos antes de entrar.");
        }

        String token = jwtService.gerarToken(userDetails);

        // Busca o idMercadinho se o perfil for COMERCIANTE.
        // Antes, se não achasse, devolvia 1 (o mercadinho de OUTRA pessoa!). Agora devolve null
        // e o app mostra "Mercadinho não identificado" em vez de mexer na loja errada.
        Long idMercadinho = null;
        if (usuario.getPerfil() == PerfilUsuario.COMERCIANTE) {
            idMercadinho = mercadinhoRepository.findByUsuarioIdUsuarioNative(usuario.getIdUsuario())
                    .map(MercadinhoModel::getIdMercadinho)
                    .orElse(null);
        }

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getPerfil(),
                idMercadinho
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> cadastrar(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        // Sem try/catch: se algo der errado, a exceção sobe e o GlobalExceptionHandler
        // devolve o status certo (400 regra, 409 e-mail repetido, etc.).
        UsuarioModel novoUsuario = usuarioService.cadastrar(requestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Conta criada com sucesso!");
        response.put("id", novoUsuario.getIdUsuario());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Link recebido por e-mail. Abre no navegador, confirma e mostra uma página simples.
    @GetMapping(value = "/verificar-email", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public ResponseEntity<String> verificarEmail(@RequestParam(required = false) String token) {
        VerificacaoEmailService.Resultado r = verificacaoEmailService.confirmar(token);
        return switch (r) {
            case CONFIRMADO -> ResponseEntity.ok(paginaResposta("E-mail confirmado!",
                    "Sua conta está ativa. Volte ao Na Esquina e faça login.", true));
            case EXPIRADO -> ResponseEntity.status(HttpStatus.GONE).body(paginaResposta("Link expirado",
                    "Este link passou da validade. Abra o app, tente entrar e peça o reenvio do e-mail de confirmação.", false));
            case INVALIDO -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(paginaResposta("Link inválido",
                    "Não encontramos este link. Ele pode já ter sido substituído por um mais novo ou a conta foi removida.", false));
        };
    }

    // Pede um novo link. Responde sempre 200 igual, exista ou não o e-mail (não revela quem tem conta).
    @PostMapping("/reenviar-verificacao")
    public ResponseEntity<Map<String, String>> reenviarVerificacao(@RequestBody Map<String, String> corpo) {
        verificacaoEmailService.reenviar(corpo == null ? null : corpo.get("email"));
        return ResponseEntity.ok(Map.of("mensagem",
                "Se existir uma conta aguardando confirmação para este e-mail, enviamos um novo link."));
    }

    // Esqueci a senha: envia um código de 6 dígitos por e-mail. Responde sempre 200 igual,
    // exista ou não a conta (não revela quem tem cadastro).
    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String, String>> recuperarSenha(@Valid @RequestBody RecuperarSenhaRequestDTO dto) {
        recuperacaoSenhaService.solicitar(dto.getEmail());
        return ResponseEntity.ok(Map.of("mensagem",
                "Se existir uma conta com este e-mail, enviamos um código para redefinir a senha."));
    }

    // Confere o código recebido e grava a nova senha. Serve para cliente e comerciante.
    @PostMapping("/redefinir-senha")
    public ResponseEntity<Map<String, String>> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequestDTO dto) {
        recuperacaoSenhaService.redefinir(dto.getEmail(), dto.getCodigo(), dto.getNovaSenha(), dto.getConfirmarSenha());
        return ResponseEntity.ok(Map.of("mensagem", "Senha redefinida com sucesso. Faça login com a nova senha."));
    }

    private String paginaResposta(String titulo, String mensagem, boolean sucesso) {
        String cor = sucesso ? "#1AC500" : "#D93025";
        String destino = urlDoFront.replaceAll("/+$", "");
        return "<!doctype html><html lang=\"pt-BR\"><head><meta charset=\"utf-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
                + "<title>Na Esquina</title></head>"
                + "<body style=\"font-family:Arial,sans-serif;background:#FFFCF7;margin:0;display:flex;"
                + "align-items:center;justify-content:center;min-height:100vh\">"
                + "<div style=\"background:#fff;border-radius:16px;padding:32px;max-width:420px;text-align:center;"
                + "box-shadow:0 2px 12px rgba(0,0,0,.08)\">"
                + "<h1 style=\"color:#FF3131;margin:0 0 4px\">Na Esquina</h1>"
                + "<h2 style=\"color:" + cor + "\">" + titulo + "</h2>"
                + "<p style=\"color:#555\">" + mensagem + "</p>"
                + "<a href=\"" + destino + "\" style=\"display:inline-block;margin-top:12px;background:#FF3131;"
                + "color:#fff;padding:12px 24px;border-radius:8px;text-decoration:none;font-weight:bold\">"
                + "Ir para o Na Esquina</a></div></body></html>";
    }
}
