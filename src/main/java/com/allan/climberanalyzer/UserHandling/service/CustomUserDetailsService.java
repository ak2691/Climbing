package com.allan.climberanalyzer.UserHandling.service;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allan.climberanalyzer.UserHandling.model.User;
import com.allan.climberanalyzer.UserHandling.model.UserPrincipal;
import com.allan.climberanalyzer.UserHandling.repo.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserPrincipal(user);
    }

    public UserDetails loadUserById(BigInteger id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id not found: " + id));

        return new UserPrincipal(user);
    }
}
