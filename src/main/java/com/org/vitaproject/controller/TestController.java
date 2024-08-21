package com.org.vitaproject.controller;

import com.org.vitaproject.model.entity.OrganizationEntity;
import com.org.vitaproject.model.entity.TestsEntity;
import com.org.vitaproject.model.entity.UserEntity;
import com.org.vitaproject.repository.OrganizationRepo;
import com.org.vitaproject.repository.TestsRepo;
import com.org.vitaproject.repository.UserRepo;
import com.org.vitaproject.service.impl.BlobStorageService;
import com.org.vitaproject.service.impl.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final BlobStorageService blobStorageService;
    private final TestsRepo testsRepo;
    private final UserRepo userRepo;
    private final OrganizationRepo organizationRepo;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        blobStorageService.uploadFile("profilepictures", file.getOriginalFilename(),
                file.getInputStream(), file.getSize());
        return "File uploaded successfully!";
    }

    @GetMapping("/get-data")
    public List<UserEntity> get() {
        return userRepo.findAll();
    }

    @GetMapping("/html")
    @ResponseBody
    public List<String> getEmailVerifiedAlert() {
        return null; //testsRepo.findAllByDescription();
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String blobName) {
        try (InputStream fileStream = blobStorageService.downloadFile("profilepictures", blobName)) {
            byte[] content = IOUtils.toByteArray(fileStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", blobName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(content.length)
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
