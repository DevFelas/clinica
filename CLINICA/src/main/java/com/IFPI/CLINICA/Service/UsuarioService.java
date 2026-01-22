package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.Model.Usuario;
import com.IFPI.CLINICA.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService (UsuarioRepository repository) {
        this.repository = repository;
    }

    public void cadastrarUsuario (Usuario usuario) {
        repository.save(usuario);
    }

}
