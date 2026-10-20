package com.example.app_na_esquina.dto.vitrine;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class VitrineResponseDTO {
    private Long idVitrine;
    private Long idMercadinho;
    private Long idProduto;
    private String nomeProduto; // Facilita a exibição no App
    private String imagemProduto; // Facilita a exibição no App
    private BigDecimal preco;
    private Boolean disponivel;
}

// (Nota: Neste DTO de resposta, é muito útil enviar o nome e a imagem do produto junto,
// para o aplicativo front-end não precisar fazer duas requisições separadas).