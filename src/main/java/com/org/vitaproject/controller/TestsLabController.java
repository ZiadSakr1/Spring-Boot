package com.org.vitaproject.controller;


import com.org.vitaproject.model.dto.PrescriptionViewDTO;
import com.org.vitaproject.model.dto.TestsResultDTO;
import com.org.vitaproject.service.TestsLabService;
import com.org.vitaproject.service.impl.BlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Test-Lab")
public class TestsLabController {
    private final TestsLabService testsLabService;
    private final BlobStorageService blobStorageService;

    @GetMapping("/get-prescription")
    public PrescriptionViewDTO getPrescription(@RequestParam Long ID,
                                               @RequestParam String laboratoryName) {
        return testsLabService.getPrescription(ID, laboratoryName);
    }

    @PostMapping("/add-test-result")
    public String addTestResult(@RequestParam String laboratoryName, @RequestBody TestsResultDTO testsResultDTO) {
        return testsLabService.addTestResult(testsResultDTO, laboratoryName);
    }

    @GetMapping("/get-test-template")
    public ResponseEntity<?> getTestTemplate() throws IOException {
        InputStream test = blobStorageService.downloadFile("testtemplate", "test.csv");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(test, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputStream.write(line.getBytes(StandardCharsets.UTF_8));
                outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
            InputStreamResource resource = new InputStreamResource
                    (new java.io.ByteArrayInputStream(outputStream.toByteArray()));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "test.csv");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(outputStream.size())
                    .contentType(MediaType.parseMediaType("application/csv"))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
