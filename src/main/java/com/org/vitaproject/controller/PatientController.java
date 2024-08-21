package com.org.vitaproject.controller;


import com.org.vitaproject.model.dto.*;
import com.org.vitaproject.service.PatientService;
import com.org.vitaproject.service.impl.BlobStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;
    private final BlobStorageService blobStorageService;

    public String getPatientUserName() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @GetMapping("/get-list-of-connections")
    public List<ConnectionsListDTO> getListOfConnections() {
        return patientService.getListOfConnections();
    }

    @GetMapping("have_access")
    public Boolean haveAccess(@RequestParam String doctorName) {
        String username = getPatientUserName();
        return patientService.haveAccess(username, doctorName);
    }

    @GetMapping("/remove_access")
    public String removeAccess(@RequestParam(name = "doctorName") String doctorName) throws Exception {
        return patientService.removeDoctorAccess(doctorName);
    }

    @GetMapping("/accept_access")
    public String acceptAccess(@RequestParam String doctorName) throws Exception {
        return patientService.acceptDoctorAccess(doctorName);
    }

    @GetMapping("/give_access")
    public String giveAccess(@RequestParam String doctorName) throws Exception {
        return patientService.giveAccessToDoctor(doctorName);
    }


    @GetMapping("/get-all-prescriptions")
    public List<PrescriptionDTOToViewAsList> getAllPrescriptions() {
        return patientService.getAllPrescriptionsV2(getPatientUserName());
    }

    @GetMapping("/get-prescription-details")
    public PrescriptionViewDTO getPrescription(@RequestParam Long ID) throws Exception {
        return patientService.getPrescriptionsById(ID);
    }

    @GetMapping("/accept-organization-access")
    public String acceptXRayLaboratoryAccess(String organizationName) throws Exception {
        return patientService.acceptOrganizationAccess(organizationName);
    }

    @GetMapping("/remove-organization-access")
    public String removeXRayLaboratoryAccess(String organizationName) throws Exception {
        return patientService.removeOrganizationAccess(organizationName);
    }

    @GetMapping("/give-organization-access")
    public String giveAccessTestsLaboratory(String organizationName) throws Exception {
        return patientService.giveOrganizationAccess(organizationName);
    }

    @GetMapping("/get-list-of-XRays")
    public List<ResponseAsListDTO> getListOfXRay() {
        return patientService.getListOfXRay();
    }

    @GetMapping("/get-XRay-picture")
    public ResponseEntity<byte[]> getXRayPicture(@RequestParam Long ID) {
        return patientService.getXRayPicture(ID);
    }

    @GetMapping("/get-category-list")
    public List<String> getCategoryList() {
        return patientService.getCategoryList();
    }

    @GetMapping("/get-list-of-tests-details-by-category")
    public List<TestsResultDTO> getTestDetails(String category) {
        return patientService.getTestDetails(category);
    }

    @GetMapping("/get-list-of-tests-by-description")
    public List<TestsValueDTO> getListOfTests(@RequestParam String description) {
        return patientService.getListOfTests(description);
    }
}
