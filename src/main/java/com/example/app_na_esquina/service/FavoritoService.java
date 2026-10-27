package com.example.app_na_esquina.service;

import com.example.app_na_esquina.model.FavoritoModel;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.repository.FavoritoRepository;
import com.example.app_na_esquina.repository.MercadinhoRepository;
import com.example.app_na_esquina.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MercadinhoRepository mercadinhoRepository;

    public List<MercadinhoModel> listarFavoritos(Long usuarioId) {
        return favoritoRepository.findByUsuarioIdUsuario(usuarioId)
                .stream()
                .map(FavoritoModel::getMercadinho)
                .collect(Collectors.toList());
    }

    @Transactional
    public void adicionarFavorito(Long usuarioId, Long mercadinhoId) {
        if (!favoritoRepository.existsByUsuarioIdUsuarioAndMercadinhoIdMercadinho(usuarioId, mercadinhoId)) {
            UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            MercadinhoModel mercadinho = mercadinhoRepository.findById(mercadinhoId)
                    .orElseThrow(() -> new RuntimeException("Mercadinho não encontrado"));

            FavoritoModel favorito = new FavoritoModel(usuario, mercadinho);
            favoritoRepository.save(favorito);
        }
    }

    @Transactional
    public void removerFavorito(Long usuarioId, Long mercadinhoId) {
        favoritoRepository.deleteByUsuarioIdUsuarioAndMercadinhoIdMercadinho(usuarioId, mercadinhoId);
    }
}