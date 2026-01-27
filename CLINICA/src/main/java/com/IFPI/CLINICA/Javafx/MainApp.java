package com.IFPI.CLINICA.Javafx;

import com.IFPI.CLINICA.ClinicaOdontoApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class MainApp extends Application {

    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(ClinicaOdontoApplication.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/pages/Login.fxml")
        );
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setTitle("SGO - Sistema de Agendamento");
        stage.setMaximized(true);
        stage.show();

        // Inicializar dados de exemplo após a aplicação iniciar
        inicializarDadosExemplo();
    }

    private void inicializarDadosExemplo() {
        try {
            // Aguardar um pouco para o Spring terminar de inicializar todos os beans
            Platform.runLater(() -> {
                try {
                    // Aqui você pode inicializar dados de exemplo se necessário
                    // Por exemplo, criar alguns procedimentos padrão ou transações
                    System.out.println("Aplicação iniciada com sucesso!");
                    System.out.println("Financeiro está pronto para uso.");
                } catch (Exception e) {
                    System.out.println("Erro ao inicializar dados: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}