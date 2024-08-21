package com.org.vitaproject.service.impl;

import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.*;
import com.org.vitaproject.model.entity.*;
import com.org.vitaproject.model.mapper.PrescriptionMapperIm;
import com.org.vitaproject.model.mapper.TestsMapper;
import com.org.vitaproject.model.mapper.UserMapper;
import com.org.vitaproject.model.mapper.XRayMapperIm;
import com.org.vitaproject.repository.*;
import com.org.vitaproject.service.DoctorService;
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
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@RequiredArgsConstructor
@Service
public class DoctorServiceImplementation implements DoctorService {
    private final DoctorRepo doctorRepo;
    private final PatientRepo patientRepo;
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PrescriptionMapperIm prescriptionMapper;
    private final TestsInPrescriptionRepo testsInPrescriptionRepo;
    private final XRayInPrescriptionRepo xRayInPrescriptionRepo;
    private final MedicineInPrescriptionRepo medicineInPrescriptionRepo;
    private final DoctorPatientRepo doctorPatientRepo;
    private final PrescriptionRepo prescriptionRepo;
    private final XRayRepo xRayRepo;
    private final XRayMapperIm xRayMapper;
    private final TestsRepo testsRepo;
    private final TestsMapper testsMapper;
    private final BlobStorageService blobStorageService;

    @Override
    public String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public List<PatientViewAsListDTO> getAllPatients() {
        List<DoctorPatientEntity> doctorPatientEntities = doctorPatientRepo.findAllByDoctorName(getUsername());
        List<PatientViewAsListDTO> result = new ArrayList<>();
        for (DoctorPatientEntity doctorPatient : doctorPatientEntities) {
            result.add(userMapper.toPatientViewToDoctorAsListDTO(userRepo.findById(doctorPatient.getPatientName()).get()));
        }
        return result;
    }

    @Override
    public PatientViewDTO getPatient(String patientName) throws Exception {
        if (haveAccess(patientName)) {
            PatientViewDTO patient = userMapper.toPatientViewDTO(userRepo.findById(patientName).get());
            int age = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
            patient.setAge(age);
            return patient;
        }
        throw new Exception("Doctor can't access this patient");
    }

    @Override
    public Boolean haveAccess(String patientName) {
        Optional<DoctorPatientEntity> entityDP = doctorPatientRepo.
                findByDoctorNameAndPatientName(getUsername(), patientName);
        if (entityDP.isPresent()) {
            return entityDP.get().getAccess();
        } else {
            throw new MessageError("Doctor can't access this patient");
        }
    }

    @Override
    public String acceptAccess(String patientName) throws Exception {
        Optional<DoctorPatientEntity> doctorPatient = doctorPatientRepo.findByDoctorNameAndPatientName(getUsername(), patientName);
        if (doctorPatient.isPresent()) {
            if (!doctorPatient.get().getAccess() && doctorPatient.get().getWhichRequestAccess() == 1) {
                doctorPatient.get().setAccess(true);
                doctorPatientRepo.save(doctorPatient.get());
                return "Accept Access Done";
            } else {
                throw new MessageError("Already have access!");
            }
        }
        throw new MessageError("No Request to be accept");
    }

    @Override
    public String removeAccess(String patientName) throws Exception {
        Optional<DoctorPatientEntity> doctorPatient = doctorPatientRepo.findByDoctorNameAndPatientName(getUsername(), patientName);
        if (doctorPatient.isPresent()) {
            doctorPatientRepo.deleteById(doctorPatient.get().getDoctorPatientId());
            return "Access Removed";
        }
        throw new MessageError("Not Found!");
    }

    @Override
    public String getAccess(String patientName) throws Exception {
        if (patientRepo.existsById(patientName)) {
            Optional<DoctorPatientEntity> doctorPatient = doctorPatientRepo.findByDoctorNameAndPatientName(getUsername(), patientName);
            if (!doctorPatient.isPresent()) {
                DoctorPatientEntity doctorPatientEntity = new DoctorPatientEntity();
                doctorPatientEntity.setPatientName(patientName);
                doctorPatientEntity.setDoctorName(getUsername());
                doctorPatientEntity.setAccess(false);
                doctorPatientEntity.setWhichRequestAccess(2);
                doctorPatientRepo.save(doctorPatientEntity);
                return "Request Access Done";
            }
            if (doctorPatient.get().getAccess()) {
                throw new MessageError("Already have access!");
            }
            throw new MessageError("Already request access!");
        }
        throw new MessageError("No patient with this UserName!");
    }

    @Override
    public List<ConnectionsListDTO> getListOfConnections() {
        List<DoctorPatientEntity> doctorPatientEntities = doctorPatientRepo.findAllByDoctorName(getUsername());
        List<ConnectionsListDTO> connectionsList = new ArrayList<>();
        for (DoctorPatientEntity doctorPatient : doctorPatientEntities) {
            if (doctorPatient.getAccess()) {
                connectionsList.add(new ConnectionsListDTO(doctorPatient.getPatientName(), userRepo.findById(doctorPatient.getPatientName()).get().getFullName(), doctorPatient.getAccess(), true));
            } else if (doctorPatient.getWhichRequestAccess() == 1) {
                connectionsList.add(new ConnectionsListDTO(doctorPatient.getPatientName(), userRepo.findById(doctorPatient.getPatientName()).get().getFullName(), doctorPatient.getAccess(), true));
            }
        }
        return connectionsList;
    }

    @Override
    public PatientViewDTO findPatient(String username) throws IOException {
        Optional<PatientEntity> patientEntity = patientRepo.findById(username);
        if (patientEntity.isEmpty()) {
            throw new NoSuchElementException("Patient Not Found!");
        }
        PatientViewDTO patient = userMapper.toPatientViewDTO(userRepo.findById(username).get());
        int age = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
        patient.setAge(age);
        return patient;
    }

    @Override
    public PrescriptionViewDTO getPrescriptionTOMyPatient(Long ID, String patientName) throws Exception {
        Optional<DoctorPatientEntity> doctorPatient = doctorPatientRepo.findByDoctorNameAndPatientName(getUsername(), patientName);
        if (doctorPatient.isEmpty() || !doctorPatient.get().getAccess()) {
            throw new MessageError("Doctor can't access this patient");
        }
        Optional<PrescriptionEntity> prescription = prescriptionRepo.findById(ID);
        if (prescription.isPresent()) {
            if (prescription.get().getPatientName().equals(patientName)) {
                return prescriptionMapper.toPrescriptionViewDTO(prescription.get());
            }
            throw new MessageError("Patient didn't have prescription with this ID");
        }
        throw new Exception("No prescription with this id");
    }

    @Override
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionTOMyPatient(String patientName) throws Exception {
        Optional<DoctorPatientEntity> doctorPatient = doctorPatientRepo.findByDoctorNameAndPatientName(getUsername(), patientName);
        if (doctorPatient.isEmpty() || !doctorPatient.get().getAccess()) {
            throw new MessageError("Doctor can't access this patient");
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
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByID(String patientName) throws Exception {
        return getAllPrescriptionTOMyPatient(patientName);
    }

    @Override
    public List<PrescriptionDTOToViewAsList> getAllPrescriptionSortedByDoctorName(String patientName) throws Exception {
        List<PrescriptionDTOToViewAsList> result = getAllPrescriptionTOMyPatient(patientName);
        result.sort(new Comparator<PrescriptionDTOToViewAsList>() {
            @Override
            public int compare(PrescriptionDTOToViewAsList o1, PrescriptionDTOToViewAsList o2) {
                return o1.getDoctorName().compareTo(o2.getDoctorName()) * -1;
            }
        });
        return result;
    }

    @Override
    public PrescriptionViewDTO addPrescriptionToMyPatient(PrescriptionAddFromDoctorDTO prescriptionEntity) throws Exception {
        if (!doctorPatientRepo.existsByDoctorNameAndPatientName(getUsername(), prescriptionEntity.getPatientName()) || !doctorPatientRepo.findByDoctorNameAndPatientName(getUsername(), prescriptionEntity.getPatientName()).get().getAccess()) {

            throw new Exception("Doctor don't have access to this patient");
        }
        PrescriptionEntity prescription = new PrescriptionEntity();
        prescription.setNote(prescriptionEntity.getNote());
        prescription.setDoctorName(getUsername());
        prescription.setPatientName(prescriptionEntity.getPatientName());
        prescription.setDiagnosis(prescriptionEntity.getDiagnosis());
        prescription = prescriptionRepo.save(prescription);
        for (MedicineDTO medicine : prescriptionEntity.getMedicines()) {
            MedicineInPrescriptionEntity medicineEntity = new MedicineInPrescriptionEntity();
            medicineEntity.setPrescriptionId(prescription.getId());
            medicineEntity.setMedicine(medicine.getMedicine());
            medicineEntity.setNote(medicine.getNote());
            medicineInPrescriptionRepo.save(medicineEntity);
        }
        for (XRayInPrescriptionDTO xRayInPrescriptionDTO : prescriptionEntity.getXr()) {
            XRayInPrescriptionEntity xRayInPrescription = new XRayInPrescriptionEntity();
            xRayInPrescription.setPrescriptionId(prescription.getId());
            xRayInPrescription.setXRay(xRayInPrescriptionDTO.getXray());
            xRayInPrescription.setNote(xRayInPrescriptionDTO.getNote());
            xRayInPrescriptionRepo.save(xRayInPrescription);
        }
        for (TestsInPrescriptionDTO tests : prescriptionEntity.getTests()) {
            TestsPrescriptionEntity testsEntity = new TestsPrescriptionEntity();
            testsEntity.setPrescriptionId(prescription.getId());
            testsEntity.setTest(tests.getTest());
            testsEntity.setNote(tests.getNote());
            testsInPrescriptionRepo.save(testsEntity);
        }
        return new PrescriptionViewDTO(prescription.getId(), prescription.getPatientName(), prescription.getDoctorName(), prescriptionEntity.getMedicines(), prescriptionEntity.getXr(), prescriptionEntity.getTests(), prescriptionEntity.getNote(), prescriptionEntity.getDiagnosis(), prescription.getCreatedAt());
    }

    @Override
    public List<ResponseAsListDTO> getListOfXRay(String patientName) {
        if (!haveAccess(patientName)) {
            throw new MessageError("Doctor can't access this patient");
        }
        List<XRayEntity> xRayEntityList = xRayRepo.findAllByPatientName(patientName);
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
    public ResponseEntity<byte[]> getXRayPicture(Long ID, String patientName) {
        if (!haveAccess(patientName)) {
            throw new MessageError("Doctor can't access this patient");
        }
        Optional<XRayEntity> xRay = xRayRepo.findById(ID);
        if (xRay.isPresent()) {
            if (!xRay.get().getPatientName().equals(patientName)) {
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

    @Override
    public List<String> getCategoryList(String patientName) {
        return testsRepo.findAllCategoryPatientName(patientName);
    }

    @Override
    public List<TestsResultDTO> getTestDetails(String category, String patientName) {
        if (!haveAccess(patientName)) {
            throw new MessageError("Doctor can't access this patient");
        }
        List<String> descriptions = testsRepo.findAllByDescription(category, patientName);
        List<TestsResultDTO> resultDTOS = new ArrayList<>();
        for (String description : descriptions) {
            Long ID = testsRepo.findByDescription(description, patientName);
            resultDTOS.add(testsMapper.toTestsResultDto(testsRepo.findById(ID).get()));
        }
        return resultDTOS;
    }

    @Override
    public List<TestsValueDTO> getListOfTests(String description, String patientName) {
        return testsRepo.findAllValues(description, patientName);
    }

    @Override
    public String changeTestAbnormal(Long ID) {
        Optional<TestsEntity> test = testsRepo.findById(ID);
        if (test.isPresent()) {
            TestsEntity entity = test.get();
            System.out.println(test.get().getPatientName());
            if (haveAccess(entity.getPatientName())) {
                if (!entity.getIs_abnormal()) {
                    entity.setIs_abnormal(true);
                    testsRepo.save(entity);
                    return "test was set abnormal!";
                } else {
                    entity.setIs_abnormal(false);
                    testsRepo.save(entity);
                    return "test was set normal!";
                }
            } else {
                throw new MessageError("Doctor can't access this patient!");
            }
        } else {
            throw new MessageError("NO Test With This ID!");
        }
    }
}
