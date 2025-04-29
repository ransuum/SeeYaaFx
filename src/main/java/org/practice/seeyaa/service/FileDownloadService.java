package org.practice.seeyaa.service;

import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.practice.seeyaa.models.entity.Files;

import java.util.function.Consumer;

public interface FileDownloadService {
    Task<Files> createFileLoadTask(int fileId);
    Task<Void> createDownloadTask(Files completeFile, Stage stage,
                                  Runnable onDone, Consumer<Exception> onError);
}
