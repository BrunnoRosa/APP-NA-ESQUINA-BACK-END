package com.example.app_na_esquina.repository;

import com.example.app_na_esquina.model.VitrineModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitrineRepository extends JpaRepository<VitrineModel, Long> {

    // Lista todos os produtos da vitrine de um mercadinho específico
    List<VitrineModel> findByMercadinho_IdMercadinho(Long idMercadinho);

    // Lista apenas os produtos que estão disponíveis ("em stock") num mercadinho
    List<VitrineModel> findByMercadinho_IdMercadinhoAndDisponivelTrue(Long idMercadinho);
}
