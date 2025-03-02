package org.practice.seeyaa;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeeYaaApplication {

    public static void main(String[] args) {
        Application.launch(SeeYaaApplicationFX.class, args);
    }

}
