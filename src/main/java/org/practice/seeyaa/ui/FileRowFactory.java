package org.practice.seeyaa.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.practice.seeyaa.action.FileAction;
import org.practice.seeyaa.models.dto.FileMetadataDto;
import org.practice.seeyaa.util.triofunction.Trio;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class FileRowFactory {
    private final FileAction fileAction;

    public HBox createFileRow(
            FileMetadataDto fileMetadata,
            Consumer<FileMetadataDto> onDownloadClick,
            Supplier<Stage> stageSupplier
    ) {
        final var fileRow = new HBox();
        fileRow.getStyleClass().add("file-row");
        fileRow.setSpacing(10);
        fileRow.setAlignment(Pos.CENTER_LEFT);
        fileRow.setMinHeight(40);

        final var fileIcon = FileUiUtils.createFileTypeIcon(fileMetadata.type());

        final var fileInfo = new VBox(5);
        HBox.setHgrow(fileInfo, Priority.ALWAYS);

        final var nameLabel = new Label(fileMetadata.name());
        nameLabel.getStyleClass().add("file-name-label");
        nameLabel.setWrapText(true);

        final var sizeLabel = new Label(FileUiUtils.formatFileSize(fileMetadata.size()));
        sizeLabel.getStyleClass().add("file-size-label");

        fileInfo.getChildren().addAll(nameLabel, sizeLabel);

        final Trio<HBox, Button, ProgressIndicator> object = fileAction.createObjects();
        final var downloadButton = object.second();
        final var progressIndicator = object.third();
        final var buttonBox = object.first();

        downloadButton.setOnAction(e -> {
            progressIndicator.setVisible(true);
            downloadButton.setVisible(false);
            onDownloadClick.accept(fileMetadata);
        });

        stageSupplier.get().setOnCloseRequest(e -> {/*for file open while on app*/});

        fileRow.getChildren().addAll(fileIcon, fileInfo, buttonBox);
        return fileRow;
    }
}
