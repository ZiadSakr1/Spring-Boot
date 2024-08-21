package com.org.vitaproject.controller;

import com.org.vitaproject.model.dto.StaticDataDTO;
import com.org.vitaproject.model.entity.InsightsEntity;
import com.org.vitaproject.model.entity.UserEntity;
import com.org.vitaproject.repository.InsightsRepo;
import com.org.vitaproject.repository.TestsRepo;
import com.org.vitaproject.repository.UserRepo;
import com.org.vitaproject.service.impl.BlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final InsightsRepo insightsRepo;
    private final TestsRepo testsRepo;
    private final BlobStorageService blobStorageService;

    @GetMapping("/get")
    public String get() {
        testsRepo.deleteAll();
        return "success";
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

    @PostMapping("/predict")
    public ResponseEntity<String> predict(@RequestParam String category, @RequestParam("file") MultipartFile file)
            throws IOException {
        String tempFileName = UUID.randomUUID().toString() + ".png";
        Path tempFilePath = Paths.get(System.getProperty("java.io.tmpdir"), tempFileName);
        file.transferTo(tempFilePath);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(tempFilePath.toFile()));

        String mlApiUrl = "https://fractured-bones-detection.azurewebsites.net/predict";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                mlApiUrl, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String predictedClass = responseEntity.getBody();
            return ResponseEntity.ok().body(predictedClass);
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).body("Error occurred during prediction.");
        }
    }
}

