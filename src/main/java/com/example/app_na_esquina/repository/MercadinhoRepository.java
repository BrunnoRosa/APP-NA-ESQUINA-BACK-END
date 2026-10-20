package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.MercadinhoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MercadinhoRepository extends JpaRepository<MercadinhoModel, Long> {

    // Busca o mercadinho pela coluna exata do MySQL 'comerciante_id'
    @Query(value = "SELECT * FROM tb_mercadinhos WHERE comerciante_id = :idUsuario LIMIT 1", nativeQuery = true)
    Optional<MercadinhoModel> findByUsuarioIdUsuarioNative(@Param("idUsuario") Long idUsuario);

    // Consulta de proximidade geográfica
    @Query(value = "SELECT *, " +
            "( 6371 * acos( cos( radians(:lat) ) * cos( radians( latitude ) ) * " +
            "cos( radians( longitude ) - radians(:lng) ) + sin( radians(:lat) ) * " +
            "sin( radians( latitude ) ) ) ) AS distancia " +
            "FROM tb_mercadinhos HAVING distancia <= :raioKm ORDER BY distancia ASC",
            nativeQuery = true)
    List<MercadinhoModel> buscarPorProximidade(@Param("lat") Double lat,
                                               @Param("lng") Double lng,
                                               @Param("raioKm") Double raioKm);
}