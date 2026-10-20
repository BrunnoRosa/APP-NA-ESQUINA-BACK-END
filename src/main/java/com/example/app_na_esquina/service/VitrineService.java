package com.example.app_na_esquina.service;

import com.example.app_na_esquina.model.VitrineModel;
import com.example.app_na_esquina.repository.VitrineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VitrineService {

    @Autowired
    private VitrineRepository vitrineRepository;

    public VitrineModel adicionarProduto(VitrineModel vitrine) {
        return vitrineRepository.save(vitrine);
    }

    // Lista TODOS os produtos de um mercadinho (útil para o painel do dono)
    public List<VitrineModel> listarVitrineDoMercadinho(Long idMercadinho) {
        return vitrineRepository.findByMercadinho_IdMercadinho(idMercadinho);
    }

    // Lista APENAS os produtos disponíveis (útil para o ecrã do cliente)
    public List<VitrineModel> listarDisponiveisDoMercadinho(Long idMercadinho) {
        return vitrineRepository.findByMercadinho_IdMercadinhoAndDisponivelTrue(idMercadinho);
    }

    public void atualizarDisponibilidade(Long idVitrine, boolean disponivel) {
        Optional<VitrineModel> itemOptional = vitrineRepository.findById(idVitrine);
        if (itemOptional.isPresent()) {
            VitrineModel item = itemOptional.get();
            item.setDisponivel(disponivel);
            vitrineRepository.save(item);
        }
    }
}