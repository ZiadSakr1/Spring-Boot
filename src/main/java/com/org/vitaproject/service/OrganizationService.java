package com.org.vitaproject.service;

import com.org.vitaproject.model.dto.ConnectionsListDTO;
import com.org.vitaproject.model.dto.OrganizationProfileDTO;
import com.org.vitaproject.model.dto.PatientViewDTO;
import com.org.vitaproject.model.dto.PrescriptionDTOToViewAsList;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OrganizationService {
    String acceptAccess(String xRayLaboratoryName, String patientName) throws Exception;

    String removeAccess(String xRayLaboratoryName, String patientName) throws Exception;

    List<ConnectionsListDTO> getListOfConnections(String organizationName);

    String getAccess(String xRayLaboratoryName, String patientName) throws Exception;

    List<PrescriptionDTOToViewAsList> getAllPrescriptionTOMyPatient(String organizationName
            , String patientName) throws Exception;

    List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByID(String xRayLaboratoryName
            , String patientName) throws Exception;

    List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByDoctorName(
            String xRayLaboratoryName,
            String patientName) throws Exception;

    int canAccessThisLab(String organizationName);

    String addEmployee(String username, String pharmacistName);

    String removeEmployee(String username, String pharmacistName);


    OrganizationProfileDTO getOrganizationData(String organizationName);

    String editOrganizationData(OrganizationProfileDTO profileDTO);

    PatientViewDTO getPatientData(String patientName, String organizationName) throws IOException;
}
