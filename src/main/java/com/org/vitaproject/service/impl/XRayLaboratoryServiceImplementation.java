package com.org.vitaproject.service.impl;


import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.PrescriptionViewDTO;
import com.org.vitaproject.model.dto.XRayInPrescriptionDTO;
import com.org.vitaproject.model.entity.*;
import com.org.vitaproject.repository.*;
import com.org.vitaproject.service.XRayLaboratoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class XRayLaboratoryServiceImplementation implements XRayLaboratoryService {
    private final OrganizationPatientRepo organizationPatientRepo;
    private final XRayInPrescriptionRepo xRayInPrescriptionRepo;
    private final XRayRepo xRayRepo;
    private final PrescriptionRepo prescriptionRepo;
    private final WorksInRepo worksInRepo;
    private final OrganizationRepo organizationRepo;
    private final BlobStorageService blobStorageService;


    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }


    @Override
    public int canAccessThisLab(String xRayLaboratoryName) {
        if (!organizationRepo.existsById(xRayLaboratoryName)) {
            throw new MessageError("No XRay-Lab With This Name");
        }
        Optional<WorksInEntity> works = worksInRepo.findByUsernameAndOrganizationName(getUsername(), xRayLaboratoryName);
        if (works.isPresent()) {
            if (works.get().getAdmin()) {
                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }


    @Override
    public PrescriptionViewDTO getPrescription(Long id, String xRayLaboratoryName) {
        if (canAccessThisLab(xRayLaboratoryName) == 0) {
            throw new MessageError("User Can't Access This Lab");
        }
        List<XRayInPrescriptionDTO> xRayInPrescriptionDTOList = new ArrayList<>();
        Optional<PrescriptionEntity> prescription = prescriptionRepo.findById(id);
        if (prescription.isEmpty()) {
            throw new MessageError("No prescription with this id");
        }
        Optional<OrganizationPatientEntity> xrayLab = organizationPatientRepo.findByOrganizationNameAndPatientName(
                xRayLaboratoryName, prescription.get().getPatientName());
        if (xrayLab.isEmpty() || !xrayLab.get().getAccess()) {
            throw new MessageError("Lab didn't have access to this patient");
        }
        PrescriptionViewDTO prescriptionViewDTO = new PrescriptionViewDTO();
        prescriptionViewDTO.setDoctorName(prescription.get().getDoctorName());
        prescriptionViewDTO.setPatientName(prescription.get().getPatientName());
        prescriptionViewDTO.setMedicines(new ArrayList<>());
        prescriptionViewDTO.setTests(new ArrayList<>());
        prescriptionViewDTO.setCreatedAt(prescription.get().getCreatedAt());
        prescriptionViewDTO.setId(prescription.get().getId());
        List<XRayInPrescriptionEntity> xRays = xRayInPrescriptionRepo.findAllByPrescriptionId(id);
        for (XRayInPrescriptionEntity xRay : xRays) {
            xRayInPrescriptionDTOList.add(new XRayInPrescriptionDTO(xRay.getXRay(), xRay.getNote()));
        }
        prescriptionViewDTO.setXrayes(xRayInPrescriptionDTOList);
        return prescriptionViewDTO;
    }

    @Override
    public String addXRayResult(String xRayLaboratoryName, String patientName, String category, MultipartFile file) throws IOException {
        if (canAccessThisLab(xRayLaboratoryName) == 0) {
            throw new MessageError("User Can't Access This Lab");
        }
        Optional<OrganizationPatientEntity> xRayLaboratoryPatient = organizationPatientRepo.
                findByOrganizationNameAndPatientName(xRayLaboratoryName, patientName);
        if (xRayLaboratoryPatient.isEmpty() || !xRayLaboratoryPatient.get().getAccess()) {
            throw new MessageError("No Access!");
        } else {
            XRayEntity xRay = new XRayEntity();
            xRay.setPatientName(patientName);
            xRay.setCategory(category);
            xRay = xRayRepo.save(xRay);
            blobStorageService.uploadFile("xraypictures", xRay.getXRayId() + "",
                    file.getInputStream(), file.getSize());
            return "Add Successfully!";
        }
    }

}
