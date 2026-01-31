package com.IFPI.CLINICA.Javafx;

import com.IFPI.CLINICA.ClinicaOdontoApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MainApp extends Application {

    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        try {
            springContext = new SpringApplicationBuilder(ClinicaOdontoApplication.class).run();
        } catch (Exception e) {
            System.err.println("ERRO ao inicializar Spring Context: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/pages/Login.fxml")
            );

            if (springContext == null) {
                throw new RuntimeException("Spring Context não inicializado!");
            }

            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("SGO - Sistema de Agendamento");
            stage.setMaximized(true); // Abre a janela ocupando todo o espaço disponível

            // Carregar ícone
            try {
                Image icone = new Image(getClass().getResourceAsStream("/view/static_files/sgo.png"));
                stage.getIcons().add(icone);
            } catch (Exception e) {
                System.err.println("Não foi possível carregar o ícone: " + e.getMessage());
            }

            stage.show();

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao iniciar aplicação: " + e.getMessage());
            e.printStackTrace();

            // Mostrar alerta de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Inicialização");
            alert.setHeaderText("Não foi possível iniciar o sistema");
            alert.setContentText("Erro: " + e.getMessage() + "\n\nContate o suporte técnico.");
            alert.showAndWait();

            Platform.exit();
        }
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }
}