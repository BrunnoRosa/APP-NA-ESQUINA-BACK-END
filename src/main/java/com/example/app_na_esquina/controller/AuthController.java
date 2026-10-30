package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.auth.LoginRequestDTO;
import com.example.app_na_esquina.dto.auth.LoginResponseDTO;
import com.example.app_na_esquina.dto.usuario.UsuarioRequestDTO;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.model.enums.PerfilUsuario;
import com.example.app_na_esquina.repository.MercadinhoRepository;
import com.example.app_na_esquina.security.CustomUserDetailsService;
import com.example.app_na_esquina.security.JwtService;
import com.example.app_na_esquina.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = "*")
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
}
