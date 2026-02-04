package com.IFPI.CLINICA.Service;

import com.IFPI.CLINICA.Model.Usuario;
import com.IFPI.CLINICA.Repository.UsuarioRepository;
import org.springframework.stereotype.Service;

/**
 * Classe integrante da camada de serviço responsável pela
 * gestão das regras de negócio relacionadas aos usuários do sistema.
 * Sua função é intermediar as requisições entre a interface e o acesso aos dados.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    /**
     * Construtor da classe que estabelece a injeção de dependência do repositório.
     * Este método garante que o serviço tenha acesso às operações de banco de dados
     * fornecidas pelo Spring Data JPA.
     * * @param repository Instância do repositório de usuários.
     */
    public UsuarioService (UsuarioRepository repository) {
        this.repository = repository;
    }

    /**
     * Efetua o registro de uma nova instância da entidade Usuario no armazenamento persistente.
     * O método recebe o objeto preenchido e utiliza o repositório para gravá-lo na base de dados.
     * * @param usuario Objeto contendo as informações do novo usuário.
     */
    public void cadastrarUsuario (Usuario usuario) {
        repository.save(usuario);
    }

}
