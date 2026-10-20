package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    // Método para buscar o usuário no banco de dados através do email (usado no login)
    Optional<UsuarioModel> findByEmail(String email);
}
