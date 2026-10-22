package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.auth.LoginRequestDTO;
import com.example.app_na_esquina.dto.auth.LoginResponseDTO;
import com.example.app_na_esquina.dto.usuario.UsuarioRequestDTO;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.model.enums.PerfilUsuario;
import com.example.app_na_esquina.repository.MercadinhoRepository;
import com.example.app_na_esquina.security.CustomUserDetailsService;
import com.example.app_na_esquina.security.JwtService;
import com.example.app_na_esquina.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        UsuarioModel usuario = usuarioService.buscarPorEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.gerarToken(userDetails);

        // Busca o idMercadinho se o perfil for COMERCIANTE
        Long idMercadinho = null;
        if (usuario.getPerfil() == PerfilUsuario.COMERCIANTE) {
            idMercadinho = mercadinhoRepository.findByUsuarioIdUsuarioNative(usuario.getIdUsuario())
                    .map(MercadinhoModel::getIdMercadinho)
                    .orElse(1L);
        }

        // Passa os 5 parâmetros exigidos pelo LoginResponseDTO
        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getPerfil(),
                idMercadinho
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        try {
            UsuarioModel usuario = new UsuarioModel();
            BeanUtils.copyProperties(requestDTO, usuario);
            UsuarioModel novoUsuario = usuarioService.salvar(usuario);

            // Cria um objeto JSON contendo a mensagem e o ID gerado
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("mensagem", "Conta criada com sucesso!");
            response.put("id", novoUsuario.getIdUsuario());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}