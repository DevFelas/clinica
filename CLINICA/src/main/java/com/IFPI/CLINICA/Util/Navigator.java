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
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

