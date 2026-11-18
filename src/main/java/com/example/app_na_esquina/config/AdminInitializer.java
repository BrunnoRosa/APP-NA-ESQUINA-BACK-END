package com.example.app_na_esquina.config;

import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.model.enums.PerfilUsuario;
import com.example.app_na_esquina.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@naesquina.com}")
    private String adminEmail;

    @Value("${app.admin.senha:Admin@123}")
    private String adminSenha;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            UsuarioModel admin = new UsuarioModel();
            admin.setNome("Administrador");
            admin.setEmail(adminEmail);
            admin.setSenha(passwordEncoder.encode(adminSenha));
            admin.setTelefone("71999990000");
            admin.setPerfil(PerfilUsuario.ADMIN);
            admin.setEmailVerificado(true); // admin não passa pela confirmação por e-mail
            usuarioRepository.save(admin);
        }
    }
}
