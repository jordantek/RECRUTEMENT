package com.tpc.tpcgestpaie.localapp.controller.numerisation;

import com.tpc.tpcgestpaie.localapp.service.numerisation.PythonIntegrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

/*
    private final PythonIntegrationService pythonService;

    @Value("${app.python.script-path}")
    private String pythonScriptPath;

    @Value("${app.storage.processed-dir}")
    private String processedDir;

    public DebugController(PythonIntegrationService pythonService) {
        this.pythonService = pythonService;
    }

    @GetMapping("/python-config")
    public Map<String, Object> getPythonConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("pythonScriptPath", pythonScriptPath);
        config.put("processedDir", processedDir);
        config.put("pythonAvailable", pythonService.testPythonConnection());
        config.put("scriptExists", Files.exists(Paths.get(pythonScriptPath)));

        try {
            config.put("processedDirExists", Files.exists(Paths.get(processedDir)));
            config.put("processedDirPath", Paths.get(processedDir).toAbsolutePath().toString());
        } catch (Exception e) {
            config.put("processedDirError", e.getMessage());
        }

        return config;
    }*/
}