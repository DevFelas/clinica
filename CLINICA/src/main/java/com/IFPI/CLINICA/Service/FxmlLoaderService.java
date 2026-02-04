package com.IFPI.CLINICA.Service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Serviço responsável por carregar arquivos FXML integrando-os ao sistema Spring.
 * Esta classe resolve o problema de injeção de dependências em Controllers do JavaFX,
 * permitindo o uso de anotações como @Autowired dentro de classes controladoras de interface.
 */
@Component
public class FxmlLoaderService {

    @Autowired
    private ApplicationContext springContext; // O "cérebro" do Spring

    /**
     * Carrega um arquivo FXML e retorna o nó raiz da interface (Parent).
     * * @param fxmlPath O caminho relativo do recurso .fxml (ex: "/view/TelaPrincipal.fxml").
     * @return O componente Parent carregado e pronto para ser exibido em uma Scene.
     * @throws IOException Caso o caminho do arquivo seja inválido ou ocorra erro de leitura.
     */
    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

        /**
         * Esta linha faz o Spring criar o Controller.
         * Ao definir a ControllerFactory, o JavaFX deixa de instanciar o Controller via reflexão simples
         * e passa a solicitar a instância ao ApplicationContext do Spring.
         * Isso permite que o Controller seja um @Component e receba @Autowired.
         */
        // Esta linha é a mágica: ela faz o Spring criar o Controller
        loader.setControllerFactory(springContext::getBean);

        return loader.load();
    }
}