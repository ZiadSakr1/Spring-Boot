package com.org.vitaproject.controller;

import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.ConnectionsListDTO;
import com.org.vitaproject.model.dto.OrganizationProfileDTO;
import com.org.vitaproject.model.dto.PatientViewDTO;
import com.org.vitaproject.model.dto.PrescriptionDTOToViewAsList;
import com.org.vitaproject.service.OrganizationService;
import com.org.vitaproject.service.impl.BlobStorageService;
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

@RequestMapping("/Organization")
@RestController
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;
    private final BlobStorageService blobStorageService;

    @GetMapping("/accept-access")
    public String acceptAccess(@RequestParam String organizationName, @RequestParam String patientName) throws Exception {
        return organizationService.acceptAccess(organizationName, patientName);
    }

    @GetMapping("/remove-access")
    public String removeAccess(@RequestParam String organizationName, @RequestParam String patientName) throws Exception {
        return organizationService.removeAccess(organizationName, patientName);
    }

    @GetMapping("/request-access")
    public String getAccess(@RequestParam String organizationName, @RequestParam String patientName) throws Exception {
        return organizationService.getAccess(organizationName, patientName);
    }

    @GetMapping("/get-list-of-connections")
    public List<ConnectionsListDTO> getConnectionsList(@RequestParam String organizationName) {
        return organizationService.getListOfConnections(organizationName);
    }

    @GetMapping("/get-all-prescriptions-sorted-by-Date")
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByID(@RequestParam String organizationName, @RequestParam String patientName) throws Exception {
        return organizationService.getAllPrescriptionSortedByID(organizationName, patientName);
    }

    @GetMapping("/get-all-prescriptions-sorted-by-DoctorName")
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByDoctorName(@RequestParam String organizationName, @RequestParam String patientName) throws Exception {
        return organizationService.getAllPrescriptionSortedByDoctorName(organizationName, patientName);
    }

    @PostMapping("/add-employee")
    public String addEmployee(@RequestParam String username, @RequestParam String organizationName) {
        return organizationService.addEmployee(username, organizationName);
    }

    @PostMapping("/remove-employee")
    public String removeEmployee(@RequestParam String username, @RequestParam String organizationName) {
        return organizationService.removeEmployee(username, organizationName);
    }

    @PostMapping("/add-profile-picture")
    public String addProfilePicture(@RequestParam String organizationName, @RequestParam("image") MultipartFile file) throws IOException {
        if (organizationService.canAccessThisLab(organizationName) < 2) {
            throw new MessageError("User Can't Make This Request");
        }
        blobStorageService.uploadFile("profilepictures", organizationName, file.getInputStream(), file.getSize());
        return "Picture uploaded successfully!";
    }

    @GetMapping("/get-profile-picture")
    public ResponseEntity<?> getProfilePicture(@RequestParam String organizationName) throws IOException {
        try (InputStream fileStream = blobStorageService.downloadFile("profilepictures", organizationName)) {
            byte[] content = IOUtils.toByteArray(fileStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", organizationName);
            return ResponseEntity.ok().headers(headers).contentLength(content.length).body(content);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/get-organization-data")
    public OrganizationProfileDTO getOrganizationData(@RequestParam String organizationName) {
        return organizationService.getOrganizationData(organizationName);
    }
    @PostMapping("/edit-profile-data")
    public String editOrganizationData(@RequestBody OrganizationProfileDTO profileDTO) {
        return organizationService.editOrganizationData(profileDTO);
    }
    @GetMapping("/get-patient-data")
    public PatientViewDTO getPatientData(@RequestParam String patientName, @RequestParam String organizationName) throws IOException {
        return organizationService.getPatientData(patientName, organizationName);
    }
}
