package com.org.vitaproject.service.impl;


import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.ConnectionsListDTO;
import com.org.vitaproject.model.dto.OrganizationProfileDTO;
import com.org.vitaproject.model.dto.PatientViewDTO;
import com.org.vitaproject.model.dto.PrescriptionDTOToViewAsList;
import com.org.vitaproject.model.entity.*;
import com.org.vitaproject.model.mapper.PrescriptionMapperIm;
import com.org.vitaproject.model.mapper.UserMapper;
import com.org.vitaproject.repository.*;
import com.org.vitaproject.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrganizationServiceImp implements OrganizationService {
    private final OrganizationRepo organizationRepo;
    private final WorksInRepo worksInRepo;
    private final OrganizationPatientRepo organizationPatientRepo;
    private final PatientRepo patientRepo;
    private final UserRepo userRepo;
    private final PrescriptionMapperIm prescriptionMapper;
    private final PrescriptionRepo prescriptionRepo;
    private final UserMapper userMapper;

    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public String acceptAccess(String organizationName, String patientName) throws Exception {

        if (canAccessThisLab(organizationName) == 0) {
            throw new MessageError("User Can't Access This Organization");
        }
        Optional<OrganizationPatientEntity> organizationPatient = organizationPatientRepo.findByOrganizationNameAndPatientName(organizationName, patientName);
        if (organizationPatient.isPresent()) {
            if (!organizationPatient.get().getAccess() && organizationPatient.get().getWhichRequestAccess()) {
                organizationPatient.get().setAccess(true);
                organizationPatientRepo.save(organizationPatient.get());
                return "Accept Access Done";
            } else {
                throw new MessageError("Already have access!");
            }
        }
        throw new MessageError("No Request to be accept");
    }

    @Override
    public String removeAccess(String organizationName, String patientName) throws Exception {
        if (canAccessThisLab(organizationName) == 0) {
            throw new MessageError("User Can't Access This Lab");
        }
        Optional<OrganizationPatientEntity> organizationPatient = organizationPatientRepo.findByOrganizationNameAndPatientName(organizationName, patientName);
        if (organizationPatient.isPresent()) {
            organizationPatientRepo.deleteById(organizationPatient.get().getID());
            return "Access Removed";
        }
        throw new MessageError("Not Found!");
    }

    @Override
    public List<ConnectionsListDTO> getListOfConnections(String organizationName) {
        if (canAccessThisLab(organizationName) == 0) {
            throw new MessageError("User Can't Access This Lab");
        }
        List<OrganizationPatientEntity> organizationPatientEntities = organizationPatientRepo.findAllByOrganizationName(organizationName);
        List<ConnectionsListDTO> connectionsListDTO = new ArrayList<>();
        for (OrganizationPatientEntity organizationPatient : organizationPatientEntities) {
            if (organizationPatient.getAccess()) {
                connectionsListDTO.add(new ConnectionsListDTO(organizationPatient.getPatientName(), userRepo.findByUsername(organizationPatient.getPatientName()).get().getFullName(), true, true));
            } else if (organizationPatient.getWhichRequestAccess()) {
                connectionsListDTO.add(new ConnectionsListDTO(organizationPatient.getPatientName(), userRepo.findByUsername(organizationPatient.getPatientName()).get().getFullName(), false, true));
            }
        }
        return connectionsListDTO;
    }

    @Override
    public String getAccess(String organizationName, String patientName) throws Exception {
        if (canAccessThisLab(organizationName) == 0) {
            throw new MessageError("User Can't Access This Lab");
        }
        if (patientRepo.existsById(patientName)) {
            Optional<OrganizationPatientEntity> organizationPatient =
                    organizationPatientRepo.findByOrganizationNameAndPatientName(organizationName, patientName);
            if (organizationPatient.isEmpty()) {
                OrganizationPatientEntity organizationPatientEntity = new OrganizationPatientEntity();
                organizationPatientEntity.setPatientName(patientName);
                organizationPatientEntity.setOrganizationName(organizationName);
                organizationPatientEntity.setType(organizationRepo.findById(organizationName).get().getType());
                organizationPatientEntity.setAccess(false);
                organizationPatientEntity.setWhichRequestAccess(false);
                organizationPatientRepo.save(organizationPatientEntity);
                return "Request Access Done";
            }
            if (organizationPatient.get().getAccess()) {
                throw new MessageError("Already have access!");
            }
            throw new MessageError("Already request access");
        }
        throw new MessageError("No patient with this UserName");
    }

    @Override
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionTOMyPatient(String organizationName, String patientName) throws Exception {
        if (canAccessThisLab(organizationName) == 0) {
            throw new MessageError("User Can't Access This Org");
        }
        Optional<OrganizationPatientEntity> organizationPatient = organizationPatientRepo.findByOrganizationNameAndPatientName(organizationName, patientName);
        if (organizationPatient.isEmpty() || !organizationPatient.get().getAccess()) {
            throw new MessageError("Organization don't have access to this patient");
        }
        List<PrescriptionEntity> prescriptionEntityList = prescriptionRepo.findAllByPatientName(patientName);
        List<PrescriptionDTOToViewAsList> prescriptionViewDTOList = new ArrayList<>();
        for (PrescriptionEntity prescription : prescriptionEntityList) {
            prescriptionViewDTOList.add(prescriptionMapper.toPrescriptionDTOToViewAsList(prescription));
        }
        prescriptionViewDTOList.sort((o1, o2) -> o1.getPrescription_id().compareTo(o2.getPrescription_id()) * -1);
        return prescriptionViewDTOList;
    }

    @Override
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByID(String organizationName, String patientName) throws Exception {
        return getAllPrescriptionTOMyPatient(organizationName, patientName);
    }

    @Override
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByDoctorName(String organizationName, String patientName) throws Exception {
        List<PrescriptionDTOToViewAsList> result = getAllPrescriptionTOMyPatient(organizationName, patientName);
        result.sort((o1, o2) -> o1.getDoctorName().compareTo(o2.getDoctorName()) * -1);
        return result;
    }

    @Override
    public int canAccessThisLab(String organizationName) {
        Optional<WorksInEntity> works = worksInRepo.findByUsernameAndOrganizationName(getUsername(),
                organizationName);
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
    public String addEmployee(String username, String organizationName) {
        if (canAccessThisLab(organizationName) < 2) {
            throw new MessageError("this Request Require Admin!");
        }
        if (worksInRepo.findByUsernameAndOrganizationName(username, organizationName).isPresent()) {
            throw new MessageError("User Already Work Here!");
        }
        OrganizationEntity organization = organizationRepo.findById(organizationName).get();
        WorksInEntity works = new WorksInEntity();
        works.setUsername(username);
        works.setOrganizationName(organizationName);
        works.setType(organization.getType());
        works.setAdmin(false);
        worksInRepo.save(works);
        return "Add Successfully!";
    }

    @Override
    public String removeEmployee(String username, String organizationName) {
        if (canAccessThisLab(organizationName) < 2) {
            throw new MessageError("this Request Require Admin!");
        }
        if (worksInRepo.findByUsernameAndOrganizationName(username, organizationName).isEmpty()) {
            throw new MessageError("User Already Not Work Here!");
        }
        worksInRepo.deleteByUsernameAndOrganizationName(username, organizationName);
        return "Add Successfully!";
    }

    @Override
    public OrganizationProfileDTO getOrganizationData(String organizationName) {
        if (canAccessThisLab(organizationName) == 0) {
            throw new MessageError("User Can't Access This Organization!");
        }
        return organizationRepo.findByOrganizationName(organizationName);
    }

    @Override
    public String editOrganizationData(OrganizationProfileDTO profileDTO) {
        int role = canAccessThisLab(profileDTO.getOrganizationName());
        if (role == 0) {
            throw new MessageError("User Can't Access This Organization OR No Organization With This Name");
        }
        if (role == 1) {
            throw new MessageError("this Request Require Admin!");
        }
        Optional<OrganizationEntity> organization = organizationRepo.findById(profileDTO.getOrganizationName());
        if (organization.isEmpty()) {
            throw new MessageError("No Organization With This Name!");
        }
        OrganizationEntity organizationEntity = organization.get();
        organizationEntity.setOrganizationName(profileDTO.getOrganizationName());
        if (profileDTO.getEmail() != null) {
            organizationEntity.setEmail(profileDTO.getEmail());
        }
        if (profileDTO.getPhone() != null) {
            organizationEntity.setPhone(profileDTO.getPhone());
        }
        if (profileDTO.getLocation() != null) {
            organizationEntity.setLocation(profileDTO.getLocation());
        }
        organizationRepo.save(organizationEntity);
        return "Successfully Updated";
    }

    @Override
    public PatientViewDTO getPatientData(String patientName, String organizationName) throws IOException {
        if (canAccessThisLab(organizationName) == 0) {
            throw new MessageError("User Can't Access This Organization OR No Organization With This Name");
        }
        Optional<PatientEntity> patientEntity = patientRepo.findById(patientName);
        if (patientEntity.isEmpty()) {
            throw new NoSuchElementException("Patient Not Found!");
        }
        if (organizationPatientRepo.findByOrganizationNameAndPatientName(organizationName, patientName).isEmpty()) {
            throw new MessageError("Organization don't have access to this patient");
        }
        PatientViewDTO patient = userMapper.toPatientViewDTO(userRepo.findById(patientName).get());
        patient.setAge(Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears());
        return patient;
    }
}