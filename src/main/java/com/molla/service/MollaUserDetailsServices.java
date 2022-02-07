package com.molla.service;

import com.molla.model.MollaUserDetails;
import com.molla.model.User;
import com.molla.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MollaUserDetailsServices implements UserDetailsService {

    private final UserRepository userRepository;

    public MollaUserDetailsServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .map(MollaUserDetails:: new)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }
}
