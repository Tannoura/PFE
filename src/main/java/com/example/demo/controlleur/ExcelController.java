package com.example.demo.controlleur;

import com.example.demo.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    @Autowired
    private ExcelService exportService;

    @GetMapping("/{sessionId}/export-presences")
    public ResponseEntity<ByteArrayResource> exportPresences(@PathVariable Long sessionId) throws IOException {
        return exportService.exportPresenceToExcel(sessionId);
    }
}
