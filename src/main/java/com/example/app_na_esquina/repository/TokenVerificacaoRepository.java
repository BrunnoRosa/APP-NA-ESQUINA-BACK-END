package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.TokenVerificacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenVerificacaoRepository extends JpaRepository<TokenVerificacaoModel, Long> {

    Optional<TokenVerificacaoModel> findByTokenHash(String tokenHash);

    Optional<TokenVerificacaoModel> findByUsuarioIdUsuario(Long idUsuario);

    void deleteByUsuarioIdUsuario(Long idUsuario);
}
