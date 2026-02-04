package com.IFPI.CLINICA.Repository;

import com.IFPI.CLINICA.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface de persistência para a entidade Usuario.
 * Estende as funcionalidades do JpaRepository para fornecer suporte a operações
 * de autenticação e gerenciamento de usuários no sistema.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Define uma consulta personalizada para validação de credenciais de acesso.
     * Este método utiliza a derivação de consulta do Spring Data para localizar
     * um registro que coincida simultaneamente com o login e a senha fornecidos.
     * @param login Identificador de acesso do usuário.
     * @param senha Chave de autenticação do usuário.
     * @return Um Optional contendo o Usuario caso as credenciais sejam válidas.
     */

    /*
     * ATENÇÃO:
     * Este método é usado diretamente no LoginController
     * Caso futuramente você altere o formato da senha (ex: hash),
     * este método precisará ser ajustado.
     */
    Optional<Usuario> findByLoginAndSenha(String login, String senha);
}
