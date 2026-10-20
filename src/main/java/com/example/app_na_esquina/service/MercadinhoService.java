package com.example.app_na_esquina.service;

import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.repository.MercadinhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MercadinhoService {

    @Autowired
    private MercadinhoRepository mercadinhoRepository;

    public List<MercadinhoModel> listarTodos() {
        return mercadinhoRepository.findAll();
    }

    public MercadinhoModel salvar(MercadinhoModel mercadinho) {
        return mercadinhoRepository.save(mercadinho);
    }

    public Optional<MercadinhoModel> buscarPorId(Long id) {
        return mercadinhoRepository.findById(id);
    }

    public List<MercadinhoModel> listarProximos(Double lat, Double lng, Double raioKm) {
        Double raioBusca = (raioKm != null) ? raioKm : 5.0;
        return mercadinhoRepository.buscarPorProximidade(lat, lng, raioBusca);
    }
}