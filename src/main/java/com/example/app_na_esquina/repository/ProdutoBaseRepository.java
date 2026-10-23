package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.ProdutoBaseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoBaseRepository extends JpaRepository<ProdutoBaseModel, Long> {

    // Busca insensível a maiúsculas/minúsculas (funciona igual ao LOWER + LIKE)
    List<ProdutoBaseModel> findByNomeContainingIgnoreCase(String nome);

    // Busca exata pelo código de barras EAN
    Optional<ProdutoBaseModel> findByCodigoBarra(String codigoBarra);

    // Filtra por categoria
    List<ProdutoBaseModel> findByCategoriaIdCategoria(Long idCategoria);
}