package org.practice.seeyaa.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AIController {
    @FXML private TextArea responseAi;
    @Setter private String prompt;

    @Value("${vertex.project_id}")
    private String PROJECT_ID;
    @Value("${vertex.location}")
    private String LOCATION;
    @Value("${vertex.model_id}")
    private String MODEL_ID;

    @FXML
    public void initialize() {
        if (prompt != null) analyzeText();
    }

    private void analyzeText() {
        try (final VertexAI vertexAi = new VertexAI(PROJECT_ID, LOCATION);) {
            GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setMaxOutputTokens(8192)
                            .setTemperature(1F)
                            .setTopP(0.95F)
                            .build();
            List<SafetySetting> safetySettings = Arrays.asList(
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build());
            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName(MODEL_ID)
                            .setVertexAi(vertexAi)
                            .setGenerationConfig(generationConfig)
                            .setSafetySettings(safetySettings)
                            .build();


            var content = ContentMaker.fromMultiModalData(prompt);
            ResponseStream<GenerateContentResponse> responseStream =
                    model.generateContentStream(content);

            StringBuilder fullResponse = new StringBuilder();


            responseStream.forEach(response -> {
                String chunk = response.getCandidates(0)
                        .getContent()
                        .getParts(0)
                        .getText();
                fullResponse.append(chunk);

                Platform.runLater(() -> {
                    responseAi.setText(fullResponse.toString());
                });
            });
        } catch (IOException e) {
            log.info("Error occurred while generating the model: {}", e.getMessage());
        }

    }

}
