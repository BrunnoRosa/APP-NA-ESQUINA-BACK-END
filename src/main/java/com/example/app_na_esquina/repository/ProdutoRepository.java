package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.ProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long> {

    List<ProdutoModel> findByMercadinhoIdMercadinho(Long idMercadinho);

    List<ProdutoModel> findByNomeContainingIgnoreCase(String nome);

    List<ProdutoModel> findByMercadinhoIdMercadinhoAndAtivoTrue(Long idMercadinho);
}
