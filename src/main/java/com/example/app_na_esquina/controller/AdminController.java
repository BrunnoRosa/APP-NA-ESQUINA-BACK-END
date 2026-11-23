package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.admin.ResetSenhaAdminRequestDTO;
import com.example.app_na_esquina.dto.admin.UsuarioAdminResponseDTO;
import com.example.app_na_esquina.exceptions.RecursoNaoEncontradoException;
import com.example.app_na_esquina.exceptions.RegraNegocioException;
import com.example.app_na_esquina.model.MercadinhoModel;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.model.enums.PerfilUsuario;
import com.example.app_na_esquina.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MercadinhoRepository mercadinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private VitrineRepository vitrineRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenVerificacaoRepository tokenVerificacaoRepository;

    @Autowired
    private TokenRecuperacaoRepository tokenRecuperacaoRepository;

    @GetMapping("/resumo")
    public ResponseEntity<?> obterResumo() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();

        long totalClientes = usuarios.stream()
                .filter(u -> u.getPerfil() == PerfilUsuario.CLIENTE)
                .count();
        long totalComerciantes = usuarios.stream()
                .filter(u -> u.getPerfil() == PerfilUsuario.COMERCIANTE)
                .count();

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalUsuarios", usuarios.size());
        resumo.put("totalClientes", totalClientes);
        resumo.put("totalComerciantes", totalComerciantes);
        resumo.put("totalMercadinhos", mercadinhoRepository.count());
        resumo.put("totalProdutos", produtoRepository.count());
        resumo.put("totalPedidos", pedidoRepository.count());

        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioAdminResponseDTO>> listarUsuarios() {
        List<UsuarioAdminResponseDTO> lista = usuarioRepository.findAll().stream()
                .sorted(Comparator.comparing(UsuarioModel::getIdUsuario).reversed())
                .map(this::converterUsuario)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @PutMapping("/usuarios/{id}/senha")
    public ResponseEntity<?> resetarSenha(
            @PathVariable Long id,
            @RequestBody ResetSenhaAdminRequestDTO dto) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        String novaSenha = dto != null ? dto.getNovaSenha() : null;
        if (novaSenha == null || novaSenha.length() < 8 || novaSenha.length() > 72
                || !novaSenha.matches(".*[A-Za-z].*") || !novaSenha.matches(".*\\d.*")) {
            throw new RegraNegocioException("A nova senha deve ter entre 8 e 72 caracteres, com letras e números.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Senha redefinida com sucesso.");
        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/usuarios/{id}")
    @Transactional
    public ResponseEntity<?> excluirUsuario(
            @PathVariable Long id,
            Authentication authentication) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (authentication != null && usuario.getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new RegraNegocioException("Você não pode excluir sua própria conta de administrador.");
        }

        // 0. Remove tokens de confirmação de e-mail (FK para o usuário)
        tokenVerificacaoRepository.deleteByUsuarioIdUsuario(id);
        tokenRecuperacaoRepository.deleteByUsuarioIdUsuario(id);

        // 1. Limpa favoritos e pedidos feitos como cliente
        favoritoRepository.deleteAll(favoritoRepository.findByUsuarioIdUsuario(id));
        pedidoRepository.deleteAll(pedidoRepository.findByClienteIdUsuarioOrderByDataCriacaoDesc(id));
        avaliacaoRepository.deleteByClienteIdUsuario(id);

        // 2. Se for comerciante, limpa todos os vínculos da loja antes de remover
        Optional<MercadinhoModel> lojaOpt = mercadinhoRepository.findByUsuarioIdUsuarioNative(id);
        if (lojaOpt.isPresent()) {
            MercadinhoModel loja = lojaOpt.get();
            Long idM = loja.getIdMercadinho();

            pedidoRepository.deleteAll(pedidoRepository.findByMercadinhoIdMercadinhoOrderByDataCriacaoDesc(idM));
            avaliacaoRepository.deleteByMercadinhoIdMercadinho(idM);
            favoritoRepository.deleteByMercadinhoIdMercadinho(idM);
            vitrineRepository.deleteAll(vitrineRepository.findByMercadinho_IdMercadinho(idM));
            produtoRepository.deleteAll(produtoRepository.findByMercadinhoIdMercadinho(idM));
            mercadinhoRepository.delete(loja);
        }

        // 3. Remove o usuário
        usuarioRepository.delete(usuario);

        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Cadastro excluído com sucesso.");
        return ResponseEntity.ok(resposta);
    }

    private UsuarioAdminResponseDTO converterUsuario(UsuarioModel u) {
        UsuarioAdminResponseDTO dto = new UsuarioAdminResponseDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setNome(u.getNome());
        dto.setEmail(u.getEmail());
        dto.setTelefone(u.getTelefone());
        dto.setPerfil(u.getPerfil());
        dto.setCidade(u.getCidade());
        dto.setUf(u.getUf());

        if (u.getPerfil() == PerfilUsuario.COMERCIANTE) {
            mercadinhoRepository.findByUsuarioIdUsuarioNative(u.getIdUsuario()).ifPresent(m -> {
                dto.setIdMercadinho(m.getIdMercadinho());
                dto.setNomeFantasia(m.getNomeFantasia());
                dto.setCnpj(m.getCnpj());
                dto.setEnderecoMercadinho(m.getEndereco());
                dto.setTotalProdutos(produtoRepository.findByMercadinhoIdMercadinho(m.getIdMercadinho()).size());
                dto.setTotalPedidos(pedidoRepository.findByMercadinhoIdMercadinhoOrderByDataCriacaoDesc(m.getIdMercadinho()).size());
            });
        } else if (u.getPerfil() == PerfilUsuario.CLIENTE) {
            dto.setTotalPedidos(pedidoRepository.findByClienteIdUsuarioOrderByDataCriacaoDesc(u.getIdUsuario()).size());
        }

        return dto;
    }
}