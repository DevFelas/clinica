package com.IFPI.CLINICA.Util;

import com.IFPI.CLINICA.Service.FxmlLoaderService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Serviço responsável por trocar a tela atual (root da {@link javafx.scene.Scene})
 * carregando um novo layout a partir de um arquivo FXML.
 *
 * <p>O carregamento do FXML é delegado ao {@code fxmlLoaderService} (injeção via Spring),
 * o que permite centralizar a criação de controllers e integrar com o contexto do Spring.</p>
 */
@Component
public class Navigator {

    /**
     * Serviço utilitário para carregar arquivos FXML (e seus controllers) integrados ao Spring.
     */
    @Autowired
    private FxmlLoaderService fxmlLoaderService;

    /**
     * Troca a página atual da aplicação pelo FXML informado.
     *
     * <p>Este método:
     * <ul>
     *   <li>Carrega o {@link javafx.scene.Parent} raiz a partir de {@code fxmlPath}.</li>
     *   <li>Obtém o {@link javafx.stage.Stage} a partir do {@code Node} de origem.</li>
     *   <li>Se ainda não existir {@link javafx.scene.Scene} no stage, cria uma nova e maximiza a janela.</li>
     *   <li>Se já existir uma scene, apenas troca o {@code root} para o novo layout.</li>
     * </ul>
     * </p>
     *
     * <p>Em caso de erro (ex.: FXML não encontrado, falha ao carregar controller),
     * a exceção é capturada e impressa no console.</p>
     *
     * @param origem nó de origem (ex.: botão clicado) usado para recuperar {@link javafx.stage.Stage} atual
     * @param fxmlPath caminho do arquivo FXML a ser carregado (ex.: {@code "/view/pages/Login.fxml"})
     */
    public void trocarPagina(Node origem, String fxmlPath) {
        try {
            Parent root = fxmlLoaderService.load(fxmlPath);

            Stage stage = (Stage) origem.getScene().getWindow();

            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
                stage.setMaximized(true);
            }
            else {
                scene.setRoot(root);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

