package com.org.vitaproject.service.impl;

import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.model.dto.AuthenticationResponseDTO;
import com.org.vitaproject.model.dto.LoginRequestDTO;
import com.org.vitaproject.model.dto.UserRegisterDTO;
import com.org.vitaproject.model.entity.UserEntity;
import com.org.vitaproject.model.entity.VerificationEntity;
import com.org.vitaproject.model.mapper.UserMapper;
import com.org.vitaproject.repository.UserRepo;
import com.org.vitaproject.repository.VerificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final VerificationRepo verificationRepo;
    private final EmailService emailService;

    public String userRegister(UserRegisterDTO userRegisterDTO) throws Exception {
        Optional<UserEntity> entityByUserName = userRepo.findByUsername(userRegisterDTO.getUsername());
        if (entityByUserName.isPresent()) {
            throw new MessageError("UserName already used!");
        }
        Optional<UserEntity> entityBySSN = userRepo.findBySSN(userRegisterDTO.getSSN());
        if (entityBySSN.isPresent()) {
            throw new MessageError("SSN already used!");
        }
        Optional<UserEntity> entityByEmail = userRepo.findByEmail(userRegisterDTO.getEmail());
        if (entityByEmail.isPresent()) {
            throw new MessageError("Email already used!");
        }
        Optional<UserEntity> entityByPhone = userRepo.findByPhone(userRegisterDTO.getPhone());
        if (entityByPhone.isPresent()) {
            throw new MessageError("Phone already used!");
        }
        UserEntity userEntity = userMapper.toEntity(userRegisterDTO);
        userEntity.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        userEntity.setRole("USER");
        userEntity.setVerified(false);
        UserEntity user = userRepo.save(userEntity);
        String token = UUID.randomUUID().toString();
        createVerificationTokenForUser(user, token);
        String verificationUrl = "https://vitaapp.azurewebsites.net/verify?token=" + token;
        emailService.sendSimpleMessage(user.getEmail(), "Email Verification",
                "To verify your email, click the link below:\n" + verificationUrl);
        return "Email Verification Sent!";
    }

    public String sendEmailVerification(String email) {
        Optional<UserEntity> user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            throw new MessageError("No user with this email!");
        }
        if (user.get().getVerified()) {
            throw new MessageError("Account already verified!");
        }
        String token = UUID.randomUUID().toString();
        createVerificationTokenForUser(user.get(), token);
        String verificationUrl = "https://vitaapp.azurewebsites.net/verify?token=" + token;
        emailService.sendSimpleMessage(user.get().getEmail(), "Email Verification",
                "To verify your email, click the link below:\n" + verificationUrl);
        return "Email Verification Sent!";
    }

    public AuthenticationResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<UserEntity> user = userRepo.findByUsername(loginRequestDTO.getUsername());
        if (user.isEmpty()) {
            throw new MessageError("UserName not found!");
        }
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.get().getPassword())) {
            throw new MessageError("Wrong Password!");
        }
        UserEntity userEntity = user.get();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),
                loginRequestDTO.getPassword()));
        AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();
        authenticationResponseDTO.setToken(jwtService.generateToken(userEntity));
        authenticationResponseDTO.setDefaultUser(userEntity.getRole().contains("USER"));
        authenticationResponseDTO.setAdmin(userEntity.getRole().contains("ADMIN"));
        return authenticationResponseDTO;
    }

    public void save(UserEntity user) {
        userRepo.save(user);
    }

    public void createVerificationTokenForUser(UserEntity user, String token) {
        VerificationEntity myToken = new VerificationEntity(token, user, 24 * 60);
        myToken.setUsername(user.getUsername());
        verificationRepo.save(myToken);
    }

    public void createPasswordResetTokenForUser(UserEntity user, String token) {
        VerificationEntity myToken = new VerificationEntity(token, user, 15);
        verificationRepo.save(myToken);
    }

    public void verifyUser(UserEntity user) {
        user.setVerified(true);
        userRepo.save(user);
    }

    public void deleteToken(String username) {
        verificationRepo.deleteByUsername(username);
    }

    public UserEntity getUserEntity(String username) {
        return userRepo.findByUsername(username).get();
    }

    public VerificationEntity getVerificationToken(String token) {
        return verificationRepo.findByToken(token);
    }
}
