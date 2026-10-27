package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.FavoritoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<FavoritoModel, Long> {

    List<FavoritoModel> findByUsuarioIdUsuario(Long idUsuario);

    Optional<FavoritoModel> findByUsuarioIdUsuarioAndMercadinhoIdMercadinho(Long idUsuario, Long idMercadinho);

    boolean existsByUsuarioIdUsuarioAndMercadinhoIdMercadinho(Long idUsuario, Long idMercadinho);

    void deleteByUsuarioIdUsuarioAndMercadinhoIdMercadinho(Long idUsuario, Long idMercadinho);
}