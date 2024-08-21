package com.org.vitaproject.service;

import com.org.vitaproject.model.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PatientService {

    List<PrescriptionDTOToViewAsList> getAllPrescriptionsV2(String username);

    PrescriptionViewDTO getPrescriptionsById(Long id) throws Exception;

    boolean removePrescriptionById(Long id);

    Boolean haveAccess(String patientName, String doctorName);


    String acceptDoctorAccess(String doctorName) throws Exception;


    String giveOrganizationAccess(String organizationName) throws Exception;

    String removeDoctorAccess(String doctorName) throws Exception;

    String giveAccessToDoctor(String doctorName) throws Exception;

    List<ConnectionsListDTO> getListOfConnections();

    String acceptOrganizationAccess(String organizationName) throws Exception;

    String removeOrganizationAccess(String organizationName) throws Exception;

    List<ResponseAsListDTO> getListOfXRay();


    List<String> getCategoryList();

    List<TestsResultDTO> getTestDetails(String category);


    List<TestsValueDTO> getListOfTests(String description);

    ResponseEntity<byte[]> getXRayPicture(Long id);
}
