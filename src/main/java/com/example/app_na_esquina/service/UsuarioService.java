package com.example.app_na_esquina.service;

import com.example.app_na_esquina.dto.usuario.UsuarioRequestDTO;
import com.example.app_na_esquina.exceptions.ConflitoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.model.enums.PerfilUsuario;
import com.example.app_na_esquina.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Cadastro público (tela de Cadastro). Aqui ficam as regras que o formato do DTO não cobre.
    @Transactional
    public UsuarioModel cadastrar(UsuarioRequestDTO dto) {
        // Ninguém pode se auto-cadastrar como ADMIN pela API pública.
        if (dto.getPerfil() == PerfilUsuario.ADMIN) {
            throw new RegraNegocioException("Não é permitido criar uma conta com o perfil informado.");
        }

        // O e-mail já chega minúsculo e sem espaços (normalizado no DTO).
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflitoException("Já existe uma conta cadastrada com este e-mail.");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setTelefone(dto.getTelefone());
        usuario.setPerfil(dto.getPerfil());

        return salvar(usuario);
    }

    public UsuarioModel salvar(UsuarioModel usuario) {
        // Encripta a senha com BCrypt antes de enviar para o banco
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Optional<UsuarioModel> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<UsuarioModel> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
}
