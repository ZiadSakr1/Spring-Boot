package com.org.vitaproject.service.impl;

import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.PrescriptionViewDTO;
import com.org.vitaproject.model.dto.TestsInPrescriptionDTO;
import com.org.vitaproject.model.dto.TestsResultDTO;
import com.org.vitaproject.model.entity.*;
import com.org.vitaproject.model.mapper.TestsMapper;
import com.org.vitaproject.repository.*;
import com.org.vitaproject.service.TestsLabService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestsLabServiceImp implements TestsLabService {
    private final OrganizationRepo testsLabRepo;
    private final WorksInRepo worksInRepo;
    private final PrescriptionRepo prescriptionRepo;
    private final TestsInPrescriptionRepo testsInPrescriptionRepo;
    private final TestsMapper testsMapper;
    private final TestsRepo testsRepo;
    private final OrganizationPatientRepo organizationPatientRepo;

    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public int canAccessThisLab(String organizationName) {
        if (!testsLabRepo.existsById(organizationName)) {
            throw new MessageError("No Test Lab With This Name");
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
    public PrescriptionViewDTO getPrescription(Long id, String laboratoryName) {
        if (canAccessThisLab(laboratoryName) == 0) {
            throw new MessageError("User Can't Access This laboratoryName");
        }
        List<TestsInPrescriptionDTO> testsInPrescriptionDTO = new ArrayList<>();
        Optional<PrescriptionEntity> prescription = prescriptionRepo.findById(id);
        if (prescription.isEmpty()) {
            throw new MessageError("No prescription with this ID");
        }
        Optional<OrganizationPatientEntity> testsLabPatient = organizationPatientRepo.
                findByOrganizationNameAndPatientName(laboratoryName, prescription.get().getPatientName());
        if (testsLabPatient.isEmpty() || !testsLabPatient.get().getAccess()) {
            throw new MessageError("laboratoryName didn't have access to this patient");
        }
        PrescriptionViewDTO prescriptionViewDTO = new PrescriptionViewDTO();
        prescriptionViewDTO.setDoctorName(prescription.get().getDoctorName());
        prescriptionViewDTO.setPatientName(prescription.get().getPatientName());
        prescriptionViewDTO.setMedicines(new ArrayList<>());
        prescriptionViewDTO.setXrayes(new ArrayList<>());
        prescriptionViewDTO.setCreatedAt(prescription.get().getCreatedAt());
        prescriptionViewDTO.setId(prescription.get().getId());
        List<TestsPrescriptionEntity> tests = testsInPrescriptionRepo.
                findAllByPrescriptionId(id);
        for (TestsPrescriptionEntity testsPrescription : tests) {
            testsInPrescriptionDTO.add(new TestsInPrescriptionDTO(testsPrescription.getTest()
                    , testsPrescription.getNote()));
        }
        prescriptionViewDTO.setTests(testsInPrescriptionDTO);
        return prescriptionViewDTO;
    }

    @Override
    public String addTestResult(TestsResultDTO testsResultDTO, String laboratoryName) {
        if (organizationPatientRepo.findByOrganizationNameAndPatientName(
                laboratoryName, testsResultDTO.getPatientName()).isPresent()) {
            TestsEntity tests = testsMapper.toTestsEntity(testsResultDTO);
            tests.setIs_abnormal(false);
            testsRepo.save(tests);
            return "Add Test Result Successfully";
        }
        throw new MessageError("Laboratory Can't Access This Patient!");
    }
}
