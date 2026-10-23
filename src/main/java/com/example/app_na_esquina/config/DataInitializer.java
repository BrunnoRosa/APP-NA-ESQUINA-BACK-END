package com.example.app_na_esquina.config;

import com.example.app_na_esquina.model.CategoriaModel;
import com.example.app_na_esquina.model.ProdutoBaseModel;
import com.example.app_na_esquina.repository.CategoriaRepository;
import com.example.app_na_esquina.repository.ProdutoBaseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoBaseRepository produtoBaseRepository;

    public DataInitializer(CategoriaRepository categoriaRepository, ProdutoBaseRepository produtoBaseRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoBaseRepository = produtoBaseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        popularCategoriasEProdutos();
    }

    private void popularCategoriasEProdutos() {
        if (categoriaRepository.count() == 0) {
            Map<String, CategoriaModel> categorias = new HashMap<>();

            // 1. Criar e Salvar Categorias
            categorias.put("Bebidas", categoriaRepository.save(new CategoriaModel(null, "Bebidas", null)));
            categorias.put("Alimentos", categoriaRepository.save(new CategoriaModel(null, "Alimentos", null)));
            categorias.put("Limpeza", categoriaRepository.save(new CategoriaModel(null, "Limpeza", null)));
            categorias.put("Higiene", categoriaRepository.save(new CategoriaModel(null, "Higiene", null)));
            categorias.put("Laticínios", categoriaRepository.save(new CategoriaModel(null, "Laticínios", null)));
            categorias.put("Padaria", categoriaRepository.save(new CategoriaModel(null, "Padaria", null)));
            categorias.put("Hortifruti", categoriaRepository.save(new CategoriaModel(null, "Hortifruti", null)));
            categorias.put("Outros", categoriaRepository.save(new CategoriaModel(null, "Outros", null)));

            System.out.println(">>> Categorias iniciais populadas com sucesso!");

            // 2. Popular Produtos do Catálogo Global (tb_produtos_base)
            if (produtoBaseRepository.count() == 0) {
                produtoBaseRepository.saveAll(Arrays.asList(
                        // --- BEBIDAS ---
                        new ProdutoBaseModel(null, "Refrigerante Coca-Cola 2L", "7894900011517", "Refrigerante de cola garrafa 2L", "https://images.unsplash.com/photo-1622483767028-3f66f32aef97", categorias.get("Bebidas")),
                        new ProdutoBaseModel(null, "Refrigerante Coca-Cola Lata 350ml", "7894900010015", "Refrigerante de cola lata 350ml", "https://images.unsplash.com/photo-1554866585-cd94860890b7", categorias.get("Bebidas")),
                        new ProdutoBaseModel(null, "Refrigerante Guaraná Antarctica 2L", "7891991000801", "Refrigerante de guaraná 2L", "https://images.unsplash.com/photo-1625772299848-391b6a87d7b3", categorias.get("Bebidas")),
                        new ProdutoBaseModel(null, "Água Mineral Crystal Sem Gás 500ml", "7894900530001", "Água mineral natural garrafa 500ml", "https://images.unsplash.com/photo-1548839140-29a749e1bc4e", categorias.get("Bebidas")),
                        new ProdutoBaseModel(null, "Cerveja Heineken Long Neck 330ml", "7891025111234", "Cerveja puro malte 330ml", "https://images.unsplash.com/photo-1608270586620-248524c67de9", categorias.get("Bebidas")),

                        // --- ALIMENTOS ---
                        new ProdutoBaseModel(null, "Arroz Branco Tio João Tipo 1 - 1kg", "7896006700019", "Arroz branco polido tipo 1 pacote 1kg", "https://images.unsplash.com/photo-1586201375761-83865001e31c", categorias.get("Alimentos")),
                        new ProdutoBaseModel(null, "Feijão Preto Camil 1kg", "7896006701016", "Feijão preto tipo 1 pacote 1kg", "https://images.unsplash.com/photo-1551462147-ff29053bfc14", categorias.get("Alimentos")),
                        new ProdutoBaseModel(null, "Açúcar Refinado União 1kg", "7896017000016", "Açúcar refinado amorfo 1kg", "https://images.unsplash.com/photo-1622484210800-4740263f6834", categorias.get("Alimentos")),
                        new ProdutoBaseModel(null, "Óleo de Soja Liza 900ml", "7896006702006", "Óleo comestível de soja 900ml", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5", categorias.get("Alimentos")),
                        new ProdutoBaseModel(null, "Macarrão Espaguete Adria 500g", "7891000001011", "Macarrão de sêmola espaguete n°8 500g", "https://images.unsplash.com/photo-1612927601601-6638404737ce", categorias.get("Alimentos")),

                        // --- LATICÍNIOS ---
                        new ProdutoBaseModel(null, "Leite Integral Piracanjuba 1L", "7898215150018", "Leite UHT integral caixinha 1L", "https://images.unsplash.com/photo-1563636619-e9143da7973b", categorias.get("Laticínios")),
                        new ProdutoBaseModel(null, "Manteiga com Sal Aviação 200g", "7896012300012", "Manteiga de primeira qualidade com sal pote 200g", "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d", categorias.get("Laticínios")),
                        new ProdutoBaseModel(null, "Requeijão Cremoso Poços de Caldas 200g", "7891000002025", "Requeijão cremoso copo 200g", "https://images.unsplash.com/photo-1528751014936-863e6e7a319c", categorias.get("Laticínios")),

                        // --- LIMPEZA ---
                        new ProdutoBaseModel(null, "Detergente Líquido Ypê Neutro 500ml", "7896098900014", "Detergente lava-louças frasco 500ml", "https://images.unsplash.com/photo-1585830812416-a6c86bb14576", categorias.get("Limpeza")),
                        new ProdutoBaseModel(null, "Sabão em Pó OMO Lavagem Perfeita 800g", "7891039000111", "Sabão em pó para roupas caixa 800g", "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae", categorias.get("Limpeza")),
                        new ProdutoBaseModel(null, "Amaciante Downy Brisa de Verão 500ml", "7891039000222", "Amaciante concentrado frasco 500ml", "https://images.unsplash.com/photo-1610557892470-55d9e80c0bce", categorias.get("Limpeza")),

                        // --- HIGIENE ---
                        new ProdutoBaseModel(null, "Creme Dental Colgate Total 12 90g", "7891024130012", "Creme dental anticárie tubo 90g", "https://images.unsplash.com/photo-1559598467-f8b76c8155d0", categorias.get("Higiene")),
                        new ProdutoBaseModel(null, "Sabonete Dove Original 90g", "7891039000333", "Sabonete em barra cremoso 90g", "https://images.unsplash.com/photo-1607006344380-b6775a0824a7", categorias.get("Higiene")),
                        new ProdutoBaseModel(null, "Papel Higiênico Neve Folha Dupla 4 rolos", "7891039000444", "Papel higiênico folha dupla pacote com 4", "https://images.unsplash.com/photo-1584556812952-905ffd0c611a", categorias.get("Higiene"))
                ));

                System.out.println(">>> Catálogo Global populado com sucesso!");
            }
        }
    }
}