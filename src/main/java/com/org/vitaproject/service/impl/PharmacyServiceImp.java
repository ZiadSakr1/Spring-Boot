package com.org.vitaproject.service.impl;

import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.MedicineDTO;
import com.org.vitaproject.model.dto.PrescriptionViewDTO;
import com.org.vitaproject.model.entity.MedicineInPrescriptionEntity;
import com.org.vitaproject.model.entity.OrganizationPatientEntity;
import com.org.vitaproject.model.entity.PrescriptionEntity;
import com.org.vitaproject.model.entity.WorksInEntity;
import com.org.vitaproject.repository.*;
import com.org.vitaproject.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PharmacyServiceImp implements PharmacyService {
    private final WorksInRepo worksInRepo;
    private final PrescriptionRepo prescriptionRepo;
    private final MedicineInPrescriptionRepo medicineInPrescriptionRepo;
    private final OrganizationPatientRepo organizationPatientRepo;
    private final OrganizationRepo organizationRepo;

    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public int canAccessThisLab(String organizationName) {
        if (!organizationRepo.existsById(organizationName)) {
            throw new MessageError("No Pharmacy With This Name");
        }
        Optional<WorksInEntity> works = worksInRepo.
                findByUsernameAndOrganizationName(getUsername(), organizationName);
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
    public PrescriptionViewDTO getPrescription(Long id, String pharmacistName) {
        if (canAccessThisLab(pharmacistName) == 0) {
            throw new MessageError("User Can't Access This Pharmacy");
        }
        List<MedicineDTO> medicineDTOList = new ArrayList<>();
        Optional<PrescriptionEntity> prescription = prescriptionRepo.findById(id);
        if (prescription.isEmpty()) {
            throw new MessageError("No prescription with this id");
        }
        Optional<OrganizationPatientEntity> pharmacistPatient = organizationPatientRepo.
                findByOrganizationNameAndPatientName(pharmacistName, prescription.get().getPatientName());
        if (pharmacistPatient.isEmpty() || !pharmacistPatient.get().getAccess()) {
            throw new MessageError("Pharmacy didn't have access to this patient");
        }
        PrescriptionViewDTO prescriptionViewDTO = new PrescriptionViewDTO();
        prescriptionViewDTO.setDoctorName(prescription.get().getDoctorName());
        prescriptionViewDTO.setPatientName(prescription.get().getPatientName());
        prescriptionViewDTO.setTests(new ArrayList<>());
        prescriptionViewDTO.setXrayes(new ArrayList<>());
        prescriptionViewDTO.setCreatedAt(prescription.get().getCreatedAt());
        prescriptionViewDTO.setId(prescription.get().getId());
        List<MedicineInPrescriptionEntity> medicine = medicineInPrescriptionRepo.
                findAllByPrescriptionId(id);
        for (MedicineInPrescriptionEntity medicine1 : medicine) {
            medicineDTOList.add(new MedicineDTO(
                    medicine1.getMedicine(), medicine1.getNote()));
        }
        prescriptionViewDTO.setMedicines(medicineDTOList);
        return prescriptionViewDTO;
    }

}
