package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.AvaliacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<AvaliacaoModel, Long> {

    @Query("SELECT a.mercadinho.idMercadinho, AVG(a.notaGeral), COUNT(a) FROM AvaliacaoModel a GROUP BY a.mercadinho.idMercadinho")
    List<Object[]> calcularMediasPorMercadinho();

    boolean existsByPedidoIdPedido(Long idPedido);

    Optional<AvaliacaoModel> findByPedidoIdPedido(Long idPedido);

    List<AvaliacaoModel> findByMercadinhoIdMercadinhoOrderByDataAvaliacaoDesc(Long idMercadinho);

    List<AvaliacaoModel> findByClienteIdUsuario(Long idCliente);

    void deleteByMercadinhoIdMercadinho(Long idMercadinho);

    void deleteByClienteIdUsuario(Long idCliente);
}