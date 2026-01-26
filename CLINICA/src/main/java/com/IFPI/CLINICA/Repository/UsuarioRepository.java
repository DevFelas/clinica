package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /*
     * ATENÇÃO:
     * Este método é usado diretamente no LoginController
     * Caso futuramente você altere o formato da senha (ex: hash),
     * este método precisará ser ajustado.
     */
    Optional<Usuario> findByLoginAndSenha(String login, String senha);
}
