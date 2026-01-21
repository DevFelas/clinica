package com.IFPI.CLINICA.Service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FxmlLoaderService {

    @Autowired
    private ApplicationContext springContext; // O "cérebro" do Spring

    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

        // Esta linha é a mágica: ela faz o Spring criar o Controller
        loader.setControllerFactory(springContext::getBean);

        return loader.load();
    }
}