package com.util.filewatcher.service;

import com.util.filewatcher.model.WatchRequest;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileWatcherService {

    private static final Logger logger = LoggerFactory.getLogger(FileWatcherService.class);
    private WatchService watchService;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Map<WatchKey, WatchRequest> watchKeys = new HashMap<>();
    private final Map<String, WatchKey> directoryToWatchKey = new HashMap<>();

    @PostConstruct
    public void init() throws IOException{

        watchService = FileSystems.getDefault().newWatchService();
        executorService.submit(  this::processEvents  ) ;

    }

    public void addWatchRequest(WatchRequest watchRequest) {

        Path srcDir = Path.of(watchRequest.getSourceDir());

        try {
            WatchKey key = srcDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
            watchKeys.put(key,watchRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//  Touch to process existing files
        try( DirectoryStream<Path> stream = Files.newDirectoryStream(srcDir) ) {
            FileTime now = FileTime.fromMillis(System.currentTimeMillis());
            for (Path entry : stream) {
                Files.setLastModifiedTime(entry, now);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void processEvents() {
        while (true) {

            WatchKey key;

            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                return;
            }

            //get the original request from HashMAP
            WatchRequest watchRequest = watchKeys.get(key);

            if(watchRequest == null)
                continue;

            for(WatchEvent<?> event : key.pollEvents() ) {
                WatchEvent.Kind<?> kind = event.kind();

                if(kind == StandardWatchEventKinds.OVERFLOW) {
                    logger.error("Overflow Error: " + kind);
                }

                WatchEvent<Path> ev  = (WatchEvent<Path>) event;
                // Get the file path from the event context
                Path fileName = ev.context();

                if(watchRequest.isLogOnly()) {
                    continue;
                }
                else {

                    // Create File objects (src & target) inorder to move this file to target (based on logonly flag)
                    File sourcefile = new File(watchRequest.getSourceDir(),fileName.toString());

                    Path targetPath = Path.of(watchRequest.getTargetDir());

                    File targetFile = new File(targetPath.toString(), fileName.toString());

                    // Move the file from Source to target

                    try {
                        Files.move(sourcefile.toPath(), targetFile.toPath());
                        logger.info("Move file : " + sourcefile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());

                    } catch (IOException e) {
                        logger.error("Error moving file " + sourcefile.getName(), e);
                    }
                }

            }

            // Exit if there are no more paths to watch

            boolean valid = key.reset();

            if(!valid) {
                watchKeys.remove(key);
                if (watchKeys.isEmpty() ) {
                    break;
                }
            }
        }
    }

    public List<WatchRequest> listWatchedDirectories() {
        return new ArrayList<>(watchKeys.values());
    }

    public boolean removeWatchRequest(String sourceDir) {
        WatchKey key = directoryToWatchKey.get(sourceDir);
        if (key != null) {
            key.cancel();
            watchKeys.remove(key);
            directoryToWatchKey.remove(sourceDir);
            logger.info("Stopped watching directory: " + sourceDir);
            return true;
        } else {
            logger.warn("Directory not found: " + sourceDir);
            return false;
        }
    }

}
