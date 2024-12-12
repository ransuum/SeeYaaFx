package org.practice.seeyaa;

import javafx.application.Application;
import javafx.scene.control.Alert;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;

@SpringBootApplication
public class SeeYaaApplication {

    public static void main(String[] args) {
            Application.launch(SeeYaaApplicationFX.class, args);
    }

}
