package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.ProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long> {

    // Lista todos os produtos de um determinado mercadinho (Ativos e Inativos)
    List<ProdutoModel> findByMercadinhoIdMercadinho(Long idMercadinho);

    // Lista apenas produtos ativos do mercadinho (para exibição no app do cliente)
    List<ProdutoModel> findByMercadinhoIdMercadinhoAndAtivoTrue(Long idMercadinho);

    // Verifica se o mercadinho já tem determinado produto do catálogo global ativo no seu estoque
    Optional<ProdutoModel> findByMercadinhoIdMercadinhoAndProdutoBaseIdProdutoBase(Long idMercadinho, Long idProdutoBase);

    // Busca produtos ativos pelo nome no Catálogo Base OU pelo nome personalizado
    List<ProdutoModel> findByProdutoBaseNomeContainingIgnoreCaseOrNomePersonalizadoContainingIgnoreCase(String nomeBase, String nomePersonalizado);

    // Busca por categoria através do relacionamento com o ProdutoBase
    List<ProdutoModel> findByProdutoBaseCategoriaIdCategoriaAndAtivoTrue(Long idCategoria);
}