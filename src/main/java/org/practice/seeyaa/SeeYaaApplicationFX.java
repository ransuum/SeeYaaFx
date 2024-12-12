package org.practice.seeyaa;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.practice.seeyaa.util.fileProperties.networkConnection.DatabaseConnectionChecker;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

public class SeeYaaApplicationFX extends javafx.application.Application {

    private ConfigurableApplicationContext springContext;
    private DatabaseConnectionChecker connectionChecker;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(SeeYaaApplication.class).run();
        connectionChecker = springContext.getBean(DatabaseConnectionChecker.class);
    }

    @Override
    public void start(Stage stage) throws Exception {

        new Thread(this::checkDatabaseConnection).start();

        springContext.publishEvent(new StageReadyEvent(stage));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("controller/login.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        String css = Objects.requireNonNull(this.getClass().getResource("controller/static/login.css")).toExternalForm();
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.setTitle("SeeYaa");
        stage.show();
    }

    private void checkDatabaseConnection() {
        boolean isConnected = connectionChecker.checkConnection();

        Platform.runLater(() -> {
            if (!isConnected) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Network problem");
                alert.showAndWait();
            }
        });
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }


}
