package com.org.vitaproject.service.impl;


import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.*;
import com.org.vitaproject.model.entity.*;
import com.org.vitaproject.model.mapper.OrganizationMapper;
import com.org.vitaproject.model.mapper.UserMapper;
import com.org.vitaproject.model.mapper.UserMapperIm;
import com.org.vitaproject.repository.*;
import com.org.vitaproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImplementation implements UserService {
    private final UserRepo userRepo;
    private final DoctorRepo doctorRepo;

    private final OrganizationRepo organizationRepo;
    private final PatientRepo patientRepo;
    private final UserMapper userMapper;
    private final UserMapperIm userMapperIm;
    private final OrganizationMapper organizationMapper;
    private final WorksInRepo worksInRepo;
    private final PasswordEncoder passwordEncoder;
    private final PostersRepo postersRepo;
    private final BlobStorageService blobStorageService;
    private final PostersLikesRepo postersLikesRepo;
    private final EmailService emailService;


    @Override
    public String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public boolean havePatientProfile() {
        return patientRepo.existsById(getUsername());
    }

    @Override
    public UserDTO getListOfProfiles() {
        UserDTO userDTO = new UserDTO();
        userDTO.setPatient(havePatientProfile());
        userDTO.setDoctor(haveDoctorProfile());
        userDTO.setOrganizationDTOList(new ArrayList<>());
        List<WorksInEntity> organizations = worksInRepo.findAllByUsername(getUsername());
        for (WorksInEntity organization : organizations) {
            userDTO.getOrganizationDTOList().add(new ViewOrganizationDTO(organization.getOrganizationName(), organization.getType(), organization.getAdmin()));
        }
        return userDTO;
    }

    @Override
    public boolean haveDoctorProfile() {
        return doctorRepo.existsById(getUsername());
    }

    @Override
    public List<UserEntity> get() {
        return userRepo.findAll();
    }

    @Override
    public GeneralInfoOfUserDTO getGeneralInfoOfUser() {
        UserEntity user = userRepo.findByUsername(getUsername()).get();
        return userMapperIm.toGeneralInfoOfUserDto(user);
    }

    @Override
    public String addPatientProfile() {
        Optional<UserEntity> userEntity = userRepo.findByUsername(getUsername());
        UserEntity user = userEntity.get();
        if (user.getPatientEntity() == null) {
            String roles = user.getRole();
            if (!roles.isEmpty()) {
                roles += ",";
            }
            roles += "PATIENT";
            user.setRole(roles);
            PatientEntity patientEntity = new PatientEntity(userEntity.get().getUsername());
            patientRepo.save(patientEntity);
            userRepo.save(user);
            return "Add Patient Profile Successfully!";
        }
        throw new MessageError("User Already Have Patient Profile!");
    }

    @Override
    public String addDoctorProfile(DoctorDto dto) {
        Optional<UserEntity> userEntity = userRepo.findByUsername(getUsername());
        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            if (user.getDoctor() == null) {
                String role = user.getRole();
                if (!role.isEmpty()) {
                    role += ",";
                }
                role += "DOCTOR";
                userEntity.get().setRole(role);
                DoctorEntity doctor = new DoctorEntity();
                doctor.setDoctorName(user.getUsername());
                doctor.setSpecialization(dto.getSpecialization());
                doctorRepo.save(doctor);
                userRepo.save(user);
                return "Add Doctor Profile Successfully!";
            }
        }
        throw new MessageError("User Already Have Doctor Profile!");
    }

    @Override
    public String addXRayLaboratoryProfile(OrganizationProfileDTO organizationProfileDTO) {
        OrganizationEntity organization = organizationMapper.toOrganizationEntity(organizationProfileDTO);
        if (organizationRepo.existsById(organization.getOrganizationName())) {
            throw new MessageError("username already used");
        }
        organization.setType("xray_lab");
        organizationRepo.save(organization);
        WorksInEntity works = new WorksInEntity();
        works.setUsername(getUsername());
        works.setType("xray_lab");
        works.setOrganizationName(organization.getOrganizationName());
        works.setAdmin(true);
        worksInRepo.save(works);
        return "Add XRay-Lab Profile Successfully!";
    }

    @Override
    public String addPharmacyProfile(OrganizationProfileDTO organizationProfileDTO) {
        OrganizationEntity organization = organizationMapper.toOrganizationEntity(organizationProfileDTO);

        if (organizationRepo.existsById(organization.getOrganizationName())) {
            throw new MessageError("username already used");
        }
        organization.setType("pharmacy");
        organizationRepo.save(organization);
        WorksInEntity works = new WorksInEntity();
        works.setUsername(getUsername());
        works.setType("pharmacy");
        works.setOrganizationName(organization.getOrganizationName());
        works.setAdmin(true);
        worksInRepo.save(works);
        return "Add Pharmacy Profile Successfully!";
    }

    @Override
    public String addTestLabProfile(OrganizationProfileDTO organizationProfileDTO) {
        OrganizationEntity organization = organizationMapper.toOrganizationEntity(organizationProfileDTO);
        if (organizationRepo.existsById(organization.getOrganizationName())) {
            throw new MessageError("username already used");
        }
        organization.setType("test_lab");
        organizationRepo.save(organization);
        WorksInEntity works = new WorksInEntity();
        works.setUsername(getUsername());
        works.setType("test_lab");
        works.setOrganizationName(organization.getOrganizationName());
        works.setAdmin(true);
        worksInRepo.save(works);
        return "Add Test-Lab Profile Successfully!";
    }


    @Override
    public ResponseEntity<?> modifyUserData(UserModifyDTO userModifyDTO) {
        UserEntity user = userRepo.findByUsername(getUsername()).get();

        if (userModifyDTO.getOldPassword() != null) {
            if (!passwordEncoder.matches(userModifyDTO.getOldPassword(), user.getPassword())) {
                return ResponseEntity.status(403).body("Password incorrect!");
            }
            if (userModifyDTO.getNewPassword() == null) {
                return ResponseEntity.status(403).body("You can't make new password empty!");
            }
            user.setPassword(passwordEncoder.encode(userModifyDTO.getNewPassword()));
        }
        if (userModifyDTO.getFullName() != null) {
            user.setFullName(userModifyDTO.getFullName());
        }
        if (userModifyDTO.getAddress() != null) {
            user.setAddress(userModifyDTO.getAddress());
        }
        if (userModifyDTO.getGender() != null) {
            user.setGender(userModifyDTO.getGender());
        }
        if (userModifyDTO.getMartalStatus() != null) {
            user.setMartalStatus(userModifyDTO.getMartalStatus());
        }
        if (userModifyDTO.getPhone() != null) {
            user.setPhone(userModifyDTO.getPhone());
        }
        if (userModifyDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userModifyDTO.getDateOfBirth());
        }
        userRepo.save(user);
        return ResponseEntity.status(200).body("Success!");
    }

    public UserEntity findUserByEmail(String email) {
        return userRepo.findByEmail(email).get();
    }

    @Override
    public String addPoster(int days, MultipartFile image) throws IOException {
        PostersEntity poster = new PostersEntity();
        poster.setUsername(getUsername());
        poster.setDays(days);
        poster.setDeleted_at(LocalDateTime.now().plusDays(days));
        poster = postersRepo.save(poster);
        blobStorageService.uploadFile("posters", poster.getId() + "", image.getInputStream(), image.getSize());
        return "Add successfully!";
    }

    @Override
    public List<PosterDTO> getActivePosters(Pageable pageable) {
        List<PostersEntity> postersEntities = postersRepo.findAllActivePosters(pageable);
        List<PosterDTO> posters = new ArrayList<>();
        for (PostersEntity poster : postersEntities) {
            PosterDTO posterDTO = new PosterDTO();
            posterDTO.setID(poster.getId());
            posterDTO.setLikes(poster.getLikes());
            posterDTO.setUserLike(postersLikesRepo.existsByIDAndUsername(poster.getId(), getUsername()));
            posterDTO.setUsername(poster.getUsername());
            posters.add(posterDTO);
        }
        return posters;
    }


    @Override
    public void likeButton(Long ID) {
        String uuid = postersLikesRepo.findByIDAndUsername(ID, getUsername());
        if (uuid != null) {
            PostersEntity posters = postersRepo.findById(ID).get();
            posters.setLikes(posters.getLikes() - 1);
            postersRepo.save(posters);
            postersLikesRepo.delete(postersLikesRepo.findById(uuid).get());
        } else {
            PostersEntity posters = postersRepo.findById(ID).get();
            posters.setLikes(posters.getLikes() + 1);
            postersRepo.save(posters);
            PostersLikesEntity postersLikes = new PostersLikesEntity();
            postersLikes.setID(ID);
            postersLikes.setUsername(getUsername());
            postersLikesRepo.save(postersLikes);
        }
    }

    @Override
    public String addBigDataProfile(BigDataProfileDTO bigDataProfileDTO) {
        if (userRepo.existsById(bigDataProfileDTO.getUsername())) {
            throw new MessageError("UserName already used!");
        }
        if (userRepo.existsByEmail(bigDataProfileDTO.getEmail())) {
            throw new MessageError("Email already used!");
        }
        UserEntity user = new UserEntity();
        user.setUsername(bigDataProfileDTO.getUsername());
        user.setEmail(bigDataProfileDTO.getEmail());
        user.setVerified(true);
        user.setAddress("");
        user.setRole("BIGDATA");
        user.setFullName(bigDataProfileDTO.getOrganizationName());
        user.setGender("");
        user.setDateOfBirth(LocalDate.now());
        user.setPhone("");
        user.setSSN("");
        user.setMartalStatus("");
        String password = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);
        emailService.sendSimpleMessage(bigDataProfileDTO.getEmail(), "Login Credentials for Your Vita Analysis Account"
                , "I hope this message finds you well.\n\nPlease find below the login credentials for your account:\n\n • Your username = " + bigDataProfileDTO.getUsername() + "\n" +
                        "• Your password = " + password + "\n\nYou can now log in using these details.\n\nbest regards, Eslam Ahmed");
        return "Email Sent Successfully";
    }
}
