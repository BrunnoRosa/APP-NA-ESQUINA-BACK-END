package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.PedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {
    List<PedidoModel> findByMercadinhoIdMercadinhoOrderByDataCriacaoDesc(Long idMercadinho);
    List<PedidoModel> findByClienteIdUsuarioOrderByDataCriacaoDesc(Long idCliente);
}
