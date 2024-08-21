package com.org.vitaproject.service.impl;

import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.*;
import com.org.vitaproject.model.entity.*;
import com.org.vitaproject.model.mapper.PrescriptionMapperIm;
import com.org.vitaproject.model.mapper.TestsMapper;
import com.org.vitaproject.model.mapper.UserMapper;
import com.org.vitaproject.model.mapper.XRayMapperIm;
import com.org.vitaproject.repository.*;
import com.org.vitaproject.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PatientServiceImplementation implements PatientService {
    private final DoctorPatientRepo doctorPatientRepo;
    private final DoctorRepo doctorRepo;
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PrescriptionRepo prescriptionRepo;
    private final PrescriptionMapperIm prescriptionMapper;
    private final OrganizationPatientRepo organizationPatientRepo;
    private final XRayRepo xRayRepo;
    private final XRayMapperIm xRayMapper;
    private final TestsRepo testsRepo;
    private final TestsMapper testsMapper;
    private final OrganizationRepo organizationRepo;
    private final BlobStorageService blobStorageService;


    public String getUserName() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionsV2(String username) {
        List<PrescriptionDTOToViewAsList> result = new ArrayList<>();
        List<PrescriptionEntity> prescriptionEntities = prescriptionRepo.findAllByPatientName(username);
        for (PrescriptionEntity prescription : prescriptionEntities) {
            result.add(prescriptionMapper.toPrescriptionDTOToViewAsList(prescription));
        }
        return result;
    }

    @Override
    public PrescriptionViewDTO getPrescriptionsById(Long id) throws Exception {
        Optional<PrescriptionEntity> prescriptionEntities = prescriptionRepo.findById(id);
        if (prescriptionEntities.isPresent()) {
            if (!prescriptionEntities.get().getPatientName().equals(getUserName())) {
                throw new MessageError(("User can't access this prescription!"));
            }
            return prescriptionMapper.toPrescriptionViewDTO(prescriptionEntities.get());
        } else {
            throw new MessageError(("Prescription Not Found!"));
        }
    }

    @Override
    public boolean removePrescriptionById(Long id) {
        if (prescriptionRepo.existsByPatientNameAndId(getUserName(), id)) {
            prescriptionRepo.deleteByPatientNameAndId(getUserName(), id);
            return true;
        }
        return false;
    }

    @Override
    public Boolean haveAccess(String patientName, String doctorName) {
        Optional<DoctorPatientEntity> entityDP = doctorPatientRepo.findByDoctorNameAndPatientName(doctorName, patientName);
        if (entityDP.isPresent() && entityDP.get().getAccess() != null) {
            return entityDP.get().getAccess();
        } else {
            return false;
        }

    }

    @Override
    public String acceptDoctorAccess(String doctorName) throws Exception {
        Optional<DoctorPatientEntity> doctorPatient = doctorPatientRepo.findByDoctorNameAndPatientName(doctorName, getUserName());
        if (doctorPatient.isPresent()) {
            if (!doctorPatient.get().getAccess()) {
                doctorPatient.get().setAccess(true);
                doctorPatientRepo.save(doctorPatient.get());
                return "Done Accept Access";
            } else {
                throw new MessageError("Already have access!");
            }
        }
        throw new MessageError("No Request to be accept");
    }

    @Override
    public String removeDoctorAccess(String doctorName) throws Exception {
        Optional<DoctorPatientEntity> doctorPatient = doctorPatientRepo.findByDoctorNameAndPatientName(doctorName, getUserName());
        if (doctorPatient.isPresent()) {
            doctorPatientRepo.deleteById(doctorPatient.get().getDoctorPatientId());
            return "Access Removed";
        }
        throw new MessageError("Not Found!");
    }

    @Override
    public String giveAccessToDoctor(String doctorName) throws Exception {
        if (doctorRepo.existsByDoctorName(doctorName)) {
            Optional<DoctorPatientEntity> doctorPatient = doctorPatientRepo.findByDoctorNameAndPatientName(doctorName, getUserName());
            if (doctorPatient.isEmpty()) {
                DoctorPatientEntity doctorPatientEntity = new DoctorPatientEntity();
                doctorPatientEntity.setPatientName(getUserName());
                doctorPatientEntity.setDoctorName(doctorName);
                doctorPatientEntity.setAccess(false);
                doctorPatientEntity.setWhichRequestAccess(1);
                doctorPatientRepo.save(doctorPatientEntity);
                return "Request Access Done";
            }
            if (doctorPatient.get().getAccess()) {
                throw new MessageError("Already have access!");
            }
            throw new MessageError("Already request access");
        }
        throw new MessageError("No doctor with this UserName");
    }

    @Override
    public List<ConnectionsListDTO> getListOfConnections() {
        List<DoctorPatientEntity> doctorPatientEntities = doctorPatientRepo.findAllByPatientName(getUserName());
        List<ConnectionsListDTO> connectionsList = new ArrayList<>();
        for (DoctorPatientEntity doctorPatient : doctorPatientEntities) {
            if (doctorPatient.getAccess()) {
                connectionsList.add(new ConnectionsListDTO(doctorPatient.getDoctorName(), userRepo.findById(doctorPatient.getDoctorName()).get().getFullName(), doctorPatient.getAccess(), true));
            } else if (doctorPatient.getWhichRequestAccess() == 2) {
                connectionsList.add(new ConnectionsListDTO(doctorPatient.getDoctorName(), userRepo.findById(doctorPatient.getDoctorName()).get().getFullName(), doctorPatient.getAccess(), true));
            }
        }
        List<OrganizationPatientEntity> organizationPatientEntities = organizationPatientRepo.findAllByPatientName(getUserName());
        for (OrganizationPatientEntity organizationPatient : organizationPatientEntities) {
            if (organizationPatient.getAccess()) {
                connectionsList.add(new ConnectionsListDTO(organizationPatient.getOrganizationName(),
                        organizationPatient.getType(), true, false));
            } else if (!organizationPatient.getWhichRequestAccess()) {
                connectionsList.add(new ConnectionsListDTO(organizationPatient.getOrganizationName(), organizationPatient.getType(), false, false));
            }
        }
        return connectionsList;
    }

    @Override
    public String giveOrganizationAccess(String organizationName) throws Exception {
        Optional<OrganizationEntity> organization = organizationRepo.findById(organizationName);
        if (organization.isPresent()) {
            Optional<OrganizationPatientEntity> organizationPatient = organizationPatientRepo.findByOrganizationNameAndPatientName(organizationName, getUserName());
            if (organizationPatient.isEmpty()) {
                OrganizationPatientEntity organizationPatientEntity = new OrganizationPatientEntity();
                organizationPatientEntity.setPatientName(getUserName());
                organizationPatientEntity.setOrganizationName(organizationName);
                organizationPatientEntity.setAccess(false);
                organizationPatientEntity.setWhichRequestAccess(true);
                organizationPatientEntity.setType(organization.get().getType());
                organizationPatientRepo.save(organizationPatientEntity);
                return "Request Access Done";
            }
            if (organizationPatient.get().getAccess()) {
                throw new MessageError("Already have access!");
            }
            throw new MessageError("Already request access");
        }
        throw new MessageError("No Organization with this UserName");
    }

    @Override
    public String acceptOrganizationAccess(String organizationName) throws Exception {
        Optional<OrganizationPatientEntity> organizationPatient = organizationPatientRepo.findByOrganizationNameAndPatientName(organizationName, getUserName());
        if (organizationPatient.isPresent()) {
            if (!organizationPatient.get().getAccess() && !organizationPatient.get().getWhichRequestAccess()) {
                organizationPatient.get().setAccess(true);
                organizationPatientRepo.save(organizationPatient.get());
                return "Done Accept Access";
            } else {
                throw new MessageError("Already have access!");
            }
        }
        throw new MessageError("No Request to be accept");
    }

    @Override
    public String removeOrganizationAccess(String organizationName) throws Exception {
        Optional<OrganizationPatientEntity> organizationPatient = organizationPatientRepo.findByOrganizationNameAndPatientName(organizationName, getUserName());
        if (organizationPatient.isPresent()) {
            organizationPatientRepo.deleteById(organizationPatient.get().getID());
            return "Access Removed";
        }
        throw new MessageError("Not Found!");
    }

    @Override
    public List<ResponseAsListDTO> getListOfXRay() {
        List<XRayEntity> xRayEntityList = xRayRepo.findAllByPatientName(getUserName());
        List<ResponseAsListDTO> responseAsListDTOList = new ArrayList<>();
        for (XRayEntity xRay : xRayEntityList) {
            responseAsListDTOList.add(xRayMapper.toXRayAsListDto(xRay));
        }
        responseAsListDTOList.sort((o1, o2) -> {
            int c = o1.getCategory().compareTo(o2.getCategory());
            if (c > 0) {
                return 1;
            } else if (c < 0) {
                return -1;
            } else {
                return o1.getId().compareTo(o2.getId()) * -1;
            }
        });
        return responseAsListDTOList;
    }

    @Override
    public List<String> getCategoryList() {
        return testsRepo.findAllCategoryPatientName(getUserName());
    }

    @Override
    public List<TestsResultDTO> getTestDetails(String category) {
        List<String> descriptions = testsRepo.findAllByDescription(category, getUserName());
        List<TestsResultDTO> resultDTOS = new ArrayList<>();
        for (String description : descriptions) {
            Long ID = testsRepo.findByDescription(description,getUserName());
            TestsEntity testEntity = testsRepo.findById(ID).get();
            TestsResultDTO resultDTO = testsMapper.toTestsResultDto(testEntity);
            resultDTO.setCreatedAt(testEntity.getCreatedAt());
            resultDTOS.add(resultDTO);
        }
        return resultDTOS;
    }

    @Override
    public List<TestsValueDTO> getListOfTests(String description) {
        return testsRepo.findAllValues(description, getUserName());
    }

    @Override
    public ResponseEntity<byte[]> getXRayPicture(Long ID) {
        Optional<XRayEntity> xRay = xRayRepo.findById(ID);
        if (xRay.isPresent()) {
            if (!xRay.get().getPatientName().equals(getUserName())) {
                throw new MessageError("User can't view this picture");
            }
            try (InputStream fileStream = blobStorageService.downloadFile(
                    "xraypictures", ID + "")) {
                byte[] content = IOUtils.toByteArray(fileStream);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                headers.setContentDispositionFormData("attachment", ID + "");
                return ResponseEntity.ok().headers(headers).contentLength(content.length).body(content);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            throw new MessageError("NO XRay Picture With This ID");
        }
    }
}
