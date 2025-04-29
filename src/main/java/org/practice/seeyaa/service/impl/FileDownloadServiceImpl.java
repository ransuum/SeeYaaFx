package org.practice.seeyaa.service.impl;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.practice.seeyaa.models.entity.Files;
import org.practice.seeyaa.service.FileDownloadService;
import org.practice.seeyaa.service.StorageService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FileDownloadServiceImpl implements FileDownloadService {
    private final StorageService storageService;

    @Override
    public Task<Files> createFileLoadTask(int fileId) {
        return new Task<>() {
            @Override
            protected Files call() {
                return storageService.getFileById(fileId);
            }
        };
    }

    @Override
    public Task<Void> createDownloadTask(Files completeFile, Stage stage, Runnable onDone, Consumer<Exception> onError) {
        return new Task<>() {
            @Override
            protected Void call() {
                File saveFile = getSaveFileFromUser(stage, completeFile.getName());
                if (saveFile != null) {
                    try {
                        copyToFile(completeFile.getData(), saveFile);
                        if (onDone != null) Platform.runLater(onDone);
                    } catch (Exception ex) {
                        if (onError != null) Platform.runLater(() -> onError.accept(ex));
                    }
                }
                return null;
            }
        };
    }

    private File getSaveFileFromUser(Stage stage, String fileName) {
        final File[] selectedFile = new File[1];
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialFileName(fileName);
            selectedFile[0] = fileChooser.showSaveDialog(stage);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return selectedFile[0];
    }

    private void copyToFile(byte[] data, File outFile) throws IOException {
        try (InputStream is = new ByteArrayInputStream(data);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            final byte[] buffer = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1)
                fos.write(buffer, 0, bytesRead);
        }
    }
}
