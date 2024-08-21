package com.org.vitaproject.service.impl;


import com.org.vitaproject.Exceptions.MessageError;
import com.org.vitaproject.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        username = username.replace("%20", " ");
        return userRepo.findByUsername
                (username).orElseThrow(() -> new MessageError("user not found"));
    }
}
