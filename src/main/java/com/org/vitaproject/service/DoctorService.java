package com.org.vitaproject.service;

import com.org.vitaproject.model.dto.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface DoctorService {


    String getAccess(String patientName) throws Exception;

    List<ConnectionsListDTO> getListOfConnections();

    PatientViewDTO findPatient(String username) throws IOException;

    public PrescriptionViewDTO getPrescriptionTOMyPatient(Long ID, String patientName) throws Exception;

    List<PrescriptionDTOToViewAsList> getAllPrescriptionTOMyPatient(String patientName) throws Exception;

    List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByDoctorName
            (String patientName) throws Exception;

    public List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByID
            (String patientName) throws Exception;

    PrescriptionViewDTO addPrescriptionToMyPatient(PrescriptionAddFromDoctorDTO prescriptionEntity
    ) throws Exception;

    public String getUsername();

    List<PatientViewAsListDTO> getAllPatients();


    PatientViewDTO getPatient(String patientName) throws Exception;

    Boolean haveAccess(String patientName);

    String acceptAccess(String patientName) throws Exception;

    String removeAccess(String patientName) throws Exception;


    List<ResponseAsListDTO> getListOfXRay(String patientName);

    ResponseEntity<byte[]> getXRayPicture(Long id, String patientName);

    List<String> getCategoryList(String patientName);

    List<TestsResultDTO> getTestDetails(String category, String patientName);

    List<TestsValueDTO> getListOfTests(String description, String patientName);

    String changeTestAbnormal(Long ID);
}
