package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.repository.UsuarioRepository;
import com.example.app_na_esquina.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioModel getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UsuarioModel) {
            return (UsuarioModel) principal;
        }

        String email = null;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        }

        if (email != null && !email.equalsIgnoreCase("anonymousUser")) {
            final String emailFinal = email; // torna a variável efetivamente final para o lambda
            return usuarioRepository.findByEmail(emailFinal)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o e-mail: " + emailFinal));
        }

        throw new RuntimeException("Não foi possível identificar o usuário autenticado");
    }

    @GetMapping
    public ResponseEntity<List<MercadinhoModel>> listar() {
        UsuarioModel usuario = getUsuarioLogado();
        List<MercadinhoModel> favoritos = favoritoService.listarFavoritos(usuario.getIdUsuario());
        return ResponseEntity.ok(favoritos);
    }

    @PostMapping
    public ResponseEntity<?> adicionar(@RequestBody Map<String, Long> body) {
        UsuarioModel usuario = getUsuarioLogado();
        Long idMercadinho = body.get("idMercadinho");

        if (idMercadinho == null) {
            return ResponseEntity.badRequest().body("idMercadinho é obrigatório");
        }

        favoritoService.adicionarFavorito(usuario.getIdUsuario(), idMercadinho);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{idMercadinho}")
    public ResponseEntity<Void> remover(@PathVariable Long idMercadinho) {
        UsuarioModel usuario = getUsuarioLogado();
        favoritoService.removerFavorito(usuario.getIdUsuario(), idMercadinho);
        return ResponseEntity.noContent().build();
    }
}