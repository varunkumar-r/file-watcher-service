package com.util.filewatcher.service;

import com.util.filewatcher.model.WatchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileWatcherServiceTest {

    @InjectMocks
    private FileWatcherService fileWatcherService;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        fileWatcherService.init();
    }

    @Test
    void testAddWatchRequest() throws IOException {
        WatchRequest request = new WatchRequest();
        request.setSourceDir("test/source");
        request.setTargetDir("test/target");
        request.setLogOnly(false);

        Files.createDirectories(Paths.get("test/source"));
        Files.createDirectories(Paths.get("test/target"));

        fileWatcherService.addWatchRequest(request);

        assertTrue(fileWatcherService.listWatchedDirectories().contains(request));

        FileSystemUtils.deleteRecursively(Paths.get("test/source"));
        FileSystemUtils.deleteRecursively(Paths.get("test/target"));
    }

    @Test
    void testRemoveWatchRequest() throws IOException {
        WatchRequest request = new WatchRequest();
        request.setSourceDir("test/source");
        request.setTargetDir("test/target");
        request.setLogOnly(false);

        Files.createDirectories(Paths.get("test/source"));
        Files.createDirectories(Paths.get("test/target"));

        fileWatcherService.addWatchRequest(request);
        boolean removed = fileWatcherService.removeWatchRequest("test/source");

        assertTrue(removed);
        assertFalse(fileWatcherService.listWatchedDirectories().contains(request));

        FileSystemUtils.deleteRecursively(Paths.get("test/source"));
        FileSystemUtils.deleteRecursively(Paths.get("test/target"));
    }

    @Test
    void testListWatchedDirectories() throws IOException {
        WatchRequest request1 = new WatchRequest();
        request1.setSourceDir("test/source1");
        request1.setTargetDir("test/target1");
        request1.setLogOnly(false);

        WatchRequest request2 = new WatchRequest();
        request2.setSourceDir("test/source2");
        request2.setTargetDir("test/target2");
        request2.setLogOnly(true);

        Files.createDirectories(Paths.get("test/source1"));
        Files.createDirectories(Paths.get("test/target1"));
        Files.createDirectories(Paths.get("test/source2"));
        Files.createDirectories(Paths.get("test/target2"));

        fileWatcherService.addWatchRequest(request1);
        fileWatcherService.addWatchRequest(request2);

        assertTrue(fileWatcherService.listWatchedDirectories().contains(request1));
        assertTrue(fileWatcherService.listWatchedDirectories().contains(request2));

        FileSystemUtils.deleteRecursively(Paths.get("test/source1"));
        FileSystemUtils.deleteRecursively(Paths.get("test/target1"));
        FileSystemUtils.deleteRecursively(Paths.get("test/source2"));
        FileSystemUtils.deleteRecursively(Paths.get("test/target2"));
    }
}
