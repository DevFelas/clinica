package com.IFPI.CLINICA.Util;

import com.IFPI.CLINICA.Service.FxmlLoaderService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Navigator {

    @Autowired
    private FxmlLoaderService fxmlLoaderService;

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

