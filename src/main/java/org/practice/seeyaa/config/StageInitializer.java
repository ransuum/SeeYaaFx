package org.practice.seeyaa.config;

import javafx.stage.Stage;
import org.practice.seeyaa.SeeYaaApplicationFX;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<SeeYaaApplicationFX.StageReadyEvent> {
    @Override
    public void onApplicationEvent(SeeYaaApplicationFX.StageReadyEvent event) {
        Stage stage = event.getStage();
    }
}
