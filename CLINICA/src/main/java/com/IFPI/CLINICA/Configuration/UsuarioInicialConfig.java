package com.IFPI.CLINICA.Configuration;

import com.IFPI.CLINICA.Model.Perfil;
import com.IFPI.CLINICA.Model.Usuario;
import com.IFPI.CLINICA.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuarioInicialConfig {

    @Bean
    CommandLineRunner criarUsuarioAdmin(UsuarioRepository usuarioRepository) {
        return args -> {

            // Só cria se NÃO existir nenhum usuário
            if (usuarioRepository.count() == 0) {

                Usuario admin = new Usuario();
                admin.setLogin("admin");
                admin.setSenha("admin");
                admin.setPerfil(Perfil.ADMIN);

                usuarioRepository.save(admin);
            }
        };
    }
}
