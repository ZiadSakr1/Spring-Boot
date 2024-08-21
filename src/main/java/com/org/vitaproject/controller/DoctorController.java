package com.org.vitaproject.controller;

import com.org.vitaproject.model.dto.*;
import com.org.vitaproject.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/get-list-of-connections")
    public List<ConnectionsListDTO> getListOfConnections() {
        return doctorService.getListOfConnections();
    }

    @GetMapping("have-access")
    public Boolean haveAccess(@RequestParam String patientName) {
        return doctorService.haveAccess(patientName);
    }

    @GetMapping("/remove_access")
    public String removeAccess(@RequestParam String patient) throws Exception {
        return doctorService.removeAccess(patient);
    }

    @GetMapping("/get-my-patient")
    public PatientViewDTO getPatient(@RequestParam String patientName) throws Exception {
        return doctorService.getPatient(patientName);
    }

    @GetMapping("/accept_access")
    public String acceptAccess(@RequestParam String patient) throws Exception {
        return doctorService.acceptAccess(patient);
    }

    @GetMapping("get_access")
    public String getAccess(@RequestParam String patientName) throws Exception {
        return doctorService.getAccess(patientName);
    }

    @GetMapping("/get_all_patients")
    public List<PatientViewAsListDTO> getAllPatients() {
        return doctorService.getAllPatients();
    }

    @PostMapping("/add-prescription-to-my-patient")
    public PrescriptionViewDTO addPrescriptionToMyPatient(@RequestBody PrescriptionAddFromDoctorDTO
                                                                  prescriptionEntity) throws Exception {
        return doctorService.addPrescriptionToMyPatient(prescriptionEntity);
    }

    @GetMapping("/get-prescription")
    public PrescriptionViewDTO getPrescriptionTOMyPatient(@RequestParam Long ID, @RequestParam
    String patientName) throws Exception {
        return doctorService.getPrescriptionTOMyPatient(ID, patientName);
    }

    @GetMapping("/get-all-prescriptions-sorted-by-Date")
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByID(String patientName) throws Exception {
        return doctorService.getAllPrescriptionSortedByID(patientName);
    }

    @GetMapping("/get-all-prescriptions-sorted-by-DoctorName")
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByDoctorName(String patientName) throws Exception {
        return doctorService.getAllPrescriptionSortedByDoctorName(patientName);
    }

    public String getDoctorUserName() {
        return doctorService.getUsername();
    }

    @GetMapping("/get-list-of-XRays")
    public List<ResponseAsListDTO> getListOfXRay(@RequestParam String patientName) {
        return doctorService.getListOfXRay(patientName);
    }

    @GetMapping("/get-XRay-picture")
    public ResponseEntity<?> getXRayPicture(@RequestParam Long ID, @RequestParam String patientName) {
        return doctorService.getXRayPicture(ID, patientName);
    }

    @GetMapping("/get-category-list")
    public List<String> getCategoryList(@RequestParam String patientName) {
        return doctorService.getCategoryList(patientName);
    }

    @GetMapping("/get-list-of-tests-by-description")
    public List<TestsValueDTO> getListOfTests(@RequestParam String description, @RequestParam String patientName) {
        return doctorService.getListOfTests(description, patientName);
    }

    @GetMapping("/get-list-of-tests-details-by-category")
    public List<TestsResultDTO> getTestDetails(@RequestParam String category, @RequestParam String patientName) {
        return doctorService.getTestDetails(category, patientName);
    }

    @PostMapping("/change-test-abnormal")
    public String changeTestAbnormal(@RequestParam Long ID) {
        return doctorService.changeTestAbnormal(ID);
    }
}
