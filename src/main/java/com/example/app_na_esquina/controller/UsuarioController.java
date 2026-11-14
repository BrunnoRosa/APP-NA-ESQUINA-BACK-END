package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.usuario.UsuarioRequestDTO;
import com.example.app_na_esquina.dto.usuario.UsuarioResponseDTO;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.repository.MercadinhoRepository;
import com.example.app_na_esquina.repository.UsuarioRepository;
import com.example.app_na_esquina.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.app_na_esquina.util.Validacoes;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MercadinhoRepository mercadinhoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioModel salvo = usuarioService.cadastrar(dto);
        return ResponseEntity.status(201).body(converterParaDTO(salvo));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioAtual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        UsuarioModel usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        UsuarioResponseDTO response = converterParaDTO(usuario);

        Optional<MercadinhoModel> mercadinho = mercadinhoRepository.findByUsuarioIdUsuarioNative(usuario.getIdUsuario());
        mercadinho.ifPresent(m -> response.setIdMercadinho(m.getIdMercadinho()));

        return ResponseEntity.ok(response);
    }

    // ROTA ADICIONADA: Atualiza nome e telefone do usuário logado
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuarioAtual(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        UsuarioModel usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        // Valida e limpa os dados com as mesmas regras do cadastro (util/Validacoes)
        if (body.get("nome") != null) {
            usuario.setNome(Validacoes.nome(body.get("nome")));
        }

        if (body.get("telefone") != null) {
            usuario.setTelefone(Validacoes.telefone(body.get("telefone")));
        }

        // Endereço (CEP, rua, número, bairro, complemento, cidade, UF): se algum campo veio, valida todos
        if (Validacoes.temEndereco(body.get("cep"), body.get("rua"), body.get("numero"), body.get("bairro"),
                body.get("cidade"), body.get("uf"))) {
            usuarioService.aplicarEndereco(usuario, Validacoes.endereco(body.get("cep"), body.get("rua"),
                    body.get("numero"), body.get("bairro"), body.get("complemento"), body.get("cidade"), body.get("uf")));
        }

        UsuarioModel salvo = usuarioRepository.save(usuario);
        UsuarioResponseDTO response = converterParaDTO(salvo);

        Optional<MercadinhoModel> mercadinho = mercadinhoRepository.findByUsuarioIdUsuarioNative(salvo.getIdUsuario());
        mercadinho.ifPresent(m -> response.setIdMercadinho(m.getIdMercadinho()));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/senha")
    public ResponseEntity<?> alterarSenha(@RequestBody Map<String, String> body, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        UsuarioModel usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        String senhaAtual = body.get("senhaAtual");
        String novaSenha = body.get("senha") != null ? body.get("senha") : body.get("novaSenha");

        // Confere a senha atual (assim, quem pegar o celular desbloqueado não troca a senha).
        // Erro é 400 e não 401: o 401 faria o app deslogar a pessoa.
        if (senhaAtual == null || senhaAtual.isEmpty()) {
            throw new RegraNegocioException("Informe a senha atual.");
        }
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new RegraNegocioException("A senha atual está incorreta.");
        }

        Validacoes.senha(novaSenha, "A nova senha");
        if (novaSenha.equals(senhaAtual)) {
            throw new RegraNegocioException("A nova senha deve ser diferente da atual.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso!"));
    }

    private UsuarioResponseDTO converterParaDTO(UsuarioModel model) {
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setIdUsuario(model.getIdUsuario());
        response.setNome(model.getNome());
        response.setEmail(model.getEmail());
        response.setTelefone(model.getTelefone());
        response.setPerfil(model.getPerfil());
        response.setCep(model.getCep());
        response.setRua(model.getRua());
        response.setNumero(model.getNumero());
        response.setBairro(model.getBairro());
        response.setComplemento(model.getComplemento());
        response.setCidade(model.getCidade());
        response.setUf(model.getUf());
        return response;
    }
    @PutMapping("/me/push-token")
    public ResponseEntity<?> salvarPushToken(@RequestBody Map<String, String> body, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        UsuarioModel usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        String token = body.get("token");
        if (token != null && !token.trim().isEmpty()) {
            usuario.setPushToken(token);
            usuarioRepository.save(usuario);
        }

        return ResponseEntity.ok(Map.of("mensagem", "Push Token registrado com sucesso."));
    }
}