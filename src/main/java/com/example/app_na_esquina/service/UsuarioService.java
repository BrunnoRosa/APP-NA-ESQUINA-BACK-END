package com.example.app_na_esquina.service;

import com.example.app_na_esquina.dto.usuario.UsuarioRequestDTO;
import com.example.app_na_esquina.exceptions.ConflitoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.model.enums.PerfilUsuario;
import com.example.app_na_esquina.repository.UsuarioRepository;
import com.example.app_na_esquina.util.Validacoes;
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

    @Transactional
    public UsuarioModel cadastrar(UsuarioRequestDTO dto) {
        if (dto.getPerfil() == PerfilUsuario.ADMIN) {
            throw new RegraNegocioException("Não é permitido criar uma conta com o perfil informado.");
        }

        if (dto.getConfirmarSenha() == null || !dto.getSenha().equals(dto.getConfirmarSenha())) {
            throw new RegraNegocioException("As senhas informadas não coincidem.");
        }

        // Pega erros de digitação em provedores conhecidos (ex.: gmail.co -> sugere gmail.com)
        Validacoes.dominioDoEmail(dto.getEmail());

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflitoException("Já existe uma conta cadastrada com este e-mail.");
        }

        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setTelefone(dto.getTelefone());
        usuario.setPerfil(dto.getPerfil());

        // Endereço: opcional para o back (o app exige para clientes). Se vier algo, valida tudo.
        if (Validacoes.temEndereco(dto.getCep(), dto.getRua(), dto.getNumero(), dto.getBairro(), dto.getCidade(), dto.getUf())) {
            aplicarEndereco(usuario, Validacoes.endereco(dto.getCep(), dto.getRua(), dto.getNumero(),
                    dto.getBairro(), dto.getComplemento(), dto.getCidade(), dto.getUf()));
        }

        return salvar(usuario);
    }

    // Copia o endereço já validado para o usuário (usado no cadastro e no PUT /me)
    public void aplicarEndereco(UsuarioModel usuario, Validacoes.Endereco e) {
        usuario.setCep(e.cep());
        usuario.setRua(e.rua());
        usuario.setNumero(e.numero());
        usuario.setBairro(e.bairro());
        usuario.setComplemento(e.complemento());
        usuario.setCidade(e.cidade());
        usuario.setUf(e.uf());
    }

    public UsuarioModel salvar(UsuarioModel usuario) {
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