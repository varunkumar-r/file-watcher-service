package com.util.filewatcher.controller;

import com.util.filewatcher.model.WatchRequest;
import com.util.filewatcher.service.FileWatcherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FileWatcherControllerTest {

    @Mock
    private FileWatcherService fileWatcherService;

    @InjectMocks
    private FileWatcherController fileWatcherController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartWatching() {
        WatchRequest request = new WatchRequest();
        request.setSourceDir("/source");
        request.setTargetDir("/target");
        request.setLogOnly(false);

        ResponseEntity<String> response = fileWatcherController.startWatching(request);
        assertEquals("File watch service started for /source", response.getBody());
        verify(fileWatcherService, times(1)).addWatchRequest(request);
    }

    @Test
    void testListWatchedDirectories() {
        WatchRequest request1 = new WatchRequest();
        request1.setSourceDir("/source1");
        request1.setTargetDir("/target1");
        request1.setLogOnly(false);

        WatchRequest request2 = new WatchRequest();
        request2.setSourceDir("/source2");
        request2.setTargetDir("/target2");
        request2.setLogOnly(true);

        when(fileWatcherService.listWatchedDirectories()).thenReturn(Arrays.asList(request1, request2));

        ResponseEntity<List<WatchRequest>> response = fileWatcherController.listWatchedDirectories();
        List<WatchRequest> watchedDirectories = response.getBody();

        assertEquals(2, watchedDirectories.size());
        verify(fileWatcherService, times(1)).listWatchedDirectories();
    }

    @Test
    void testStopWatching() {
        when(fileWatcherService.removeWatchRequest(anyString())).thenReturn(true);
        ResponseEntity<String> response = fileWatcherController.stopWatching("/source");
        assertEquals("Stopped watching directory: /source", response.getBody());
        verify(fileWatcherService, times(1)).removeWatchRequest("/source");
    }

    @Test
    void testStopWatchingDirectoryNotFound() {
        when(fileWatcherService.removeWatchRequest(anyString())).thenReturn(false);
        ResponseEntity<String> response = fileWatcherController.stopWatching("/source");
        assertEquals("Directory not found: /source", response.getBody());
        verify(fileWatcherService, times(1)).removeWatchRequest("/source");
    }
}