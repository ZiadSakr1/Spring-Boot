package com.org.vitaproject.controller;

import com.org.vitaproject.model.dto.*;
import com.org.vitaproject.model.entity.PostersEntity;
import com.org.vitaproject.model.entity.UserEntity;
import com.org.vitaproject.service.ImageService;
import com.org.vitaproject.service.UserService;
import com.org.vitaproject.service.impl.BlobStorageService;
import com.org.vitaproject.util.JsonConverter;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/auth")
public class UserController {
    private final UserService userService;
    private final BlobStorageService blobStorageService;
    private final JsonConverter jsonConverter = new JsonConverter();

    @GetMapping("/get")
    public List<UserEntity> get() {
        return userService.get();
    }

    @GetMapping("/get-list-of-profiles")
    public UserDTO getListOfProfiles() {
        return userService.getListOfProfiles();
    }

    @GetMapping("/have-patient-profile")
    public boolean havePatientProfile() {
        return userService.havePatientProfile();
    }

    @GetMapping("/have-doctor-profile")
    public boolean haveDoctorProfile() {
        return userService.haveDoctorProfile();
    }

    @PostMapping("/add-patient-profile")
    public ResponseEntity<String> addPatientProfile() {
        return ResponseEntity.ok(userService.addPatientProfile());
    }

    @PostMapping("/add-doctor-profile")
    public String addDoctorProfile(@RequestBody DoctorDto dto) {
        return userService.addDoctorProfile(dto);
    }

    @PostMapping("/add-xray-laboratory-profile")
    public String addXRayLaboratoryProfile(@RequestBody OrganizationProfileDTO organizationProfileDTO) {
        return userService.addXRayLaboratoryProfile(organizationProfileDTO);
    }

    @PostMapping("/add-pharmacy-profile")
    public String addPharmacyProfile(@RequestBody OrganizationProfileDTO organizationProfileDTO) {
        return userService.addPharmacyProfile(organizationProfileDTO);
    }

    @PostMapping("/add-test-lab-profile")
    public String addTestLabProfile(@RequestBody OrganizationProfileDTO organizationProfileDTO) {
        return userService.addTestLabProfile(organizationProfileDTO);
    }

    @PostMapping("/add-profile-image")
    public String addProfileImage(@RequestParam("image") MultipartFile file) throws IOException {
        blobStorageService.uploadFile("profilepictures",
                userService.getUsername(),
                file.getInputStream(), file.getSize());
        return "Picture uploaded successfully!";
    }

    @GetMapping("/get-profile-image")
    public ResponseEntity<byte[]> downloadFile() {
        return getImage(userService.getUsername());
    }

    @PostMapping("/modify-data")
    public ResponseEntity<?> modifyUserData(@RequestBody UserModifyDTO userModifyDTO) {
        return userService.modifyUserData(userModifyDTO);
    }

    @GetMapping("/get-image")
    public ResponseEntity<byte[]> getImage(@RequestParam String username) {
        try (InputStream fileStream = blobStorageService.downloadFile("profilepictures",
                username)) {
            byte[] content = IOUtils.toByteArray(fileStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", username);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(content.length)
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/get-poster-image")
    public ResponseEntity<byte[]> getPosterImage(@RequestParam Long ID) {
        try (InputStream fileStream = blobStorageService.downloadFile("posters",
                ID + "")) {
            byte[] content = IOUtils.toByteArray(fileStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", ID + "");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(content.length)
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/add-poster")
    public String addPoster(@RequestParam int days, @RequestParam("image") MultipartFile image) throws IOException {
        return userService.addPoster(days, image);
    }

    @PostMapping("/like-button")
    public void likeButton(@RequestParam Long ID) {
        userService.likeButton(ID);
    }

    @GetMapping("get-posters")
    public List<PosterDTO> getActivePosters(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "2") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("likes", "ID").descending());
        return userService.getActivePosters(pageable);
    }

    @GetMapping("/get-general-info-of-user")
    public GeneralInfoOfUserDTO getGeneralInfoOfUser() {
        return userService.getGeneralInfoOfUser();
    }

    @PostMapping("/add-bigdata-profile")
    public String addBigDataProfile(@RequestBody BigDataProfileDTO bigDataProfileDTO) {
        return userService.addBigDataProfile(bigDataProfileDTO);
    }

    @GetMapping("/predict")
    public ResponseEntity<?> predict(@RequestParam String category, @RequestParam Integer ID)
            throws IOException {
        InputStream inputStream = blobStorageService.downloadFile("xraypictures", ID + "");
        String tempFileName = UUID.randomUUID().toString() + ".png";
        File tempFile = File.createTempFile("tempfile-", "-" + tempFileName);
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            FileCopyUtils.copy(inputStream, outputStream);
        }
        FileSystemResource fileSystemResource = new FileSystemResource(tempFile);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);
        String mlApiUrl;
        if (category.equals("bones")) {
            mlApiUrl = "https://fractured-bones-detection.azurewebsites.net/predict";
        } else if (category.equals("chest-lungs")) {
            mlApiUrl = "https://pneumonia-detection.azurewebsites.net/predict";
        } else {
            return ResponseEntity.status(400).body("Error occurred during prediction.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                mlApiUrl, HttpMethod.POST, requestEntity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok().body(responseEntity.getBody());
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).body("Error occurred during prediction.");
        }
    }
}
