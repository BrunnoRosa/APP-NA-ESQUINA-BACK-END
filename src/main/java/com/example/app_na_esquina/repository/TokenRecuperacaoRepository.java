package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.TokenRecuperacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacaoModel, Long> {

    Optional<TokenRecuperacaoModel> findByUsuarioIdUsuario(Long idUsuario);

    void deleteByUsuarioIdUsuario(Long idUsuario);
}
