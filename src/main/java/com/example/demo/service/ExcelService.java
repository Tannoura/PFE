package com.example.demo.service;

import com.example.demo.entité.Membre;
import com.example.demo.entité.Presence;
import com.example.demo.repository.MembreRepository;
import com.example.demo.repository.PresenceRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelService {
    private final MembreRepository membreRepository;
    private final PresenceRepository presenceRepository;

    // Injection des repositories via le constructeur
    public ExcelService(MembreRepository membreRepository, PresenceRepository presenceRepository) {
        this.membreRepository = membreRepository;
        this.presenceRepository = presenceRepository;
    }

    public ResponseEntity<ByteArrayResource> exportPresenceToExcel(Long sessionId) throws IOException {
        // Récupérer les membres associés à la session
        List<Membre> membres = membreRepository.findBySessionId(sessionId);
        if (membres.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Récupérer les présences pour les membres récupérés
        List<Presence> presences = presenceRepository.findByMembreIn(membres);
        if (presences.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Création du workbook et de la feuille
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Présences");

        // Création de l'en-tête
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Membre ID");
        headerRow.createCell(2).setCellValue("Présent");
        headerRow.createCell(3).setCellValue("Jour");

        // Remplissage des données
        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Presence presence : presences) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(presence.getId());
            row.createCell(1).setCellValue(presence.getMembre() != null ? presence.getMembre().getUser().getUsername() : null);
            row.createCell(2).setCellValue(presence.isPresent());
            row.createCell(3).setCellValue(presence.getJour() != null ? presence.getJour().format(formatter) : null);
        }

        // Écriture du fichier dans un flux de sortie
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

        // Création de la réponse HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=presences.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }

}
