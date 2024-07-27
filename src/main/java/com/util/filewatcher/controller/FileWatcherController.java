package com.util.filewatcher.controller;

import com.util.filewatcher.model.WatchRequest;
import com.util.filewatcher.service.FileWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filewatcher")
public class FileWatcherController {

    @Autowired
    private FileWatcherService fileWatcherService;


    @PostMapping("/start")
    public ResponseEntity<String> startWatching(@RequestBody WatchRequest watchRequest) {

        fileWatcherService.addWatchRequest(watchRequest);

        return ResponseEntity.ok("FileWatch service started for : " + watchRequest.getSourceDir());
    }

    @DeleteMapping("/stop")
    public ResponseEntity<String> stopWatching(String srcDir) {
        return null;

    }

    @GetMapping("/list")
    public ResponseEntity<List<WatchRequest>> listWatchedDirectories() {
        return ResponseEntity.ok(fileWatcherService.listWatchedDirectories());
    }


}
