package com.example.app_na_esquina.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Apaga UMA conta não confirmada e tudo que depende dela, numa transação própria:
// se uma conta der erro (ex.: já tem pedidos), as outras continuam sendo limpas.
@Service
public class LimpezaContasService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void excluirContaNaoConfirmada(Long idUsuario) {
        // Segurança extra: só apaga se continuar NÃO confirmada
        Object valor = em.createNativeQuery(
                "SELECT email_verificado FROM tb_usuarios WHERE id_usuario = :id")
                .setParameter("id", idUsuario).getSingleResult();
        boolean confirmada = valor instanceof Boolean b ? b : ((Number) valor).intValue() != 0;
        if (confirmada) return;

        em.createNativeQuery("DELETE FROM tb_tokens_verificacao WHERE id_usuario = :id")
                .setParameter("id", idUsuario).executeUpdate();
        em.createNativeQuery("DELETE FROM tb_tokens_recuperacao WHERE id_usuario = :id")
                .setParameter("id", idUsuario).executeUpdate();
        // Mercadinho cadastrado junto (comerciante) e os favoritos que apontam para ele
        em.createNativeQuery("DELETE FROM tb_favorito WHERE mercadinho_id IN "
                + "(SELECT id_mercadinho FROM tb_mercadinhos WHERE comerciante_id = :id)")
                .setParameter("id", idUsuario).executeUpdate();
        em.createNativeQuery("DELETE FROM tb_mercadinhos WHERE comerciante_id = :id")
                .setParameter("id", idUsuario).executeUpdate();
        em.createNativeQuery("DELETE FROM tb_favorito WHERE usuario_id = :id")
                .setParameter("id", idUsuario).executeUpdate();
        em.createNativeQuery("DELETE FROM tb_usuarios WHERE id_usuario = :id")
                .setParameter("id", idUsuario).executeUpdate();
    }
}
