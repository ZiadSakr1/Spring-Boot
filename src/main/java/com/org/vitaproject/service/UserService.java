package com.org.vitaproject.service;

import com.org.vitaproject.model.dto.*;
import com.org.vitaproject.model.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface UserService {

    GeneralInfoOfUserDTO getGeneralInfoOfUser();

    String addPatientProfile();

    ResponseEntity<?> modifyUserData(UserModifyDTO userModifyDTO);


    String addDoctorProfile(DoctorDto dto);

    String addXRayLaboratoryProfile(OrganizationProfileDTO organizationProfileDTO);

    String getUsername();

    boolean havePatientProfile();

    UserDTO getListOfProfiles();

    boolean haveDoctorProfile();

    List<UserEntity> get();

    String addPharmacyProfile(OrganizationProfileDTO organizationProfileDTO);

    String addTestLabProfile(OrganizationProfileDTO organizationProfileDTO);

    String addPoster(int days, MultipartFile image) throws IOException;


     List<PosterDTO> getActivePosters(Pageable pageable);


    void likeButton(Long ID);

    String addBigDataProfile(BigDataProfileDTO bigDataProfileDTO);
}
