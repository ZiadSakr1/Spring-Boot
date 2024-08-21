package com.org.vitaproject.model.mapper;

import com.org.vitaproject.model.dto.*;
import com.org.vitaproject.model.entity.MedicineInPrescriptionEntity;
import com.org.vitaproject.model.entity.PrescriptionEntity;
import com.org.vitaproject.model.entity.TestsPrescriptionEntity;
import com.org.vitaproject.model.entity.XRayInPrescriptionEntity;
import com.org.vitaproject.repository.MedicineInPrescriptionRepo;
import com.org.vitaproject.repository.TestsInPrescriptionRepo;
import com.org.vitaproject.repository.XRayInPrescriptionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PrescriptionMapperIm {
    private final MedicineInPrescriptionRepo medicineInPrescriptionRepo;
    private final TestsInPrescriptionRepo testsInPrescriptionRepo;
    private final XRayInPrescriptionRepo xRayInPrescriptionRepo;

    public PrescriptionViewDTO toPrescriptionViewDTO(PrescriptionEntity prescriptionEntity) {
        List<MedicineDTO> medicineDTOList = new ArrayList<>();
        List<MedicineInPrescriptionEntity> medicineInPrescriptionEntities =
                medicineInPrescriptionRepo.findAllByPrescriptionId(prescriptionEntity.getId());
        for (MedicineInPrescriptionEntity medicine : medicineInPrescriptionEntities) {
            medicineDTOList.add(new MedicineDTO(medicine.getMedicine(), medicine.getNote()));
        }
        List<TestsInPrescriptionDTO> testsInPrescriptionDTOList = new ArrayList<>();

        List<TestsPrescriptionEntity> tests =
                testsInPrescriptionRepo.findAllByPrescriptionId(prescriptionEntity.getId());
        for (TestsPrescriptionEntity testsInPrescriptionEntity : tests) {
            testsInPrescriptionDTOList.add(new TestsInPrescriptionDTO(testsInPrescriptionEntity.getTest(),
                    testsInPrescriptionEntity.getNote()));
        }
        List<XRayInPrescriptionDTO> xRayInPrescriptionDTOList = new ArrayList<>();
        List<XRayInPrescriptionEntity> xRays =
                xRayInPrescriptionRepo.findAllByPrescriptionId(prescriptionEntity.getId());
        for (XRayInPrescriptionEntity xRay : xRays) {
            xRayInPrescriptionDTOList.add(new XRayInPrescriptionDTO(xRay.getXRay(),xRay.getNote()));
        }
        return new PrescriptionViewDTO(prescriptionEntity.getId()
                , prescriptionEntity.getPatientName(),
                prescriptionEntity.getDoctorName(), medicineDTOList, xRayInPrescriptionDTOList
                , testsInPrescriptionDTOList, prescriptionEntity.getNote(),
                prescriptionEntity.getDiagnosis(), prescriptionEntity.getCreatedAt());
    }

    public PrescriptionDTOToViewAsList toPrescriptionDTOToViewAsList(PrescriptionEntity prescriptionEntity) {
        PrescriptionDTOToViewAsList prescriptionDTOToViewAsList = new PrescriptionDTOToViewAsList();
        prescriptionDTOToViewAsList.setPrescription_id(prescriptionEntity.getId());
        prescriptionDTOToViewAsList.setCreated_at(prescriptionEntity.getCreatedAt());
        prescriptionDTOToViewAsList.setDoctorName(prescriptionEntity.getDoctorName());
        return prescriptionDTOToViewAsList;
    }

    XRayInPrescriptionDTO toXRayInPrescriptionDto(XRayInPrescriptionEntity xRayInPrescription) {
        return null;
    }
}
