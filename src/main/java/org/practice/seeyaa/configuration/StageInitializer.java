package org.practice.seeyaa.configuration;

import javafx.stage.Stage;
import lombok.NonNull;
import org.practice.seeyaa.SeeYaaApplicationFX;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StageInitializer implements ApplicationListener<SeeYaaApplicationFX.StageReadyEvent> {
    @Override
    public void onApplicationEvent(@NonNull SeeYaaApplicationFX.StageReadyEvent event) {
        // TODO document why this method is empty
    }
}
