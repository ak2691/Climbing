package com.allan.climberanalyzer.UserHandling.controller;

import java.nio.file.attribute.UserPrincipal;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import com.allan.climberanalyzer.UserHandling.model.JwtResponse;
import com.allan.climberanalyzer.UserHandling.model.LoginRequest;
import com.allan.climberanalyzer.UserHandling.model.SignUpRequest;
import com.allan.climberanalyzer.UserHandling.model.User;
import com.allan.climberanalyzer.UserHandling.model.UserProfile;
import com.allan.climberanalyzer.UserHandling.repo.UserProfileRepo;
import com.allan.climberanalyzer.UserHandling.repo.UserRepo;
import com.allan.climberanalyzer.UserHandling.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepo userRepo;

    // @Autowired
    // private UserProfileRepo userProfileRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @PostMapping("/login")
    public ResponseEntity<?> authUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (bindingResult.hasErrors()) {

            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            Map<String, String> errors = new HashMap<>();
            errors.put("message", errorMessage);

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Only useful if we happen to switch to stateful sessions
            // SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtService.generatetoken(loginRequest.getUsername());

            return new ResponseEntity<>(new JwtResponse(jwt), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> result = new HashMap<>();
            result.put("message", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest,
            BindingResult bindingResults) {
        if (bindingResults.hasErrors()) {
            String message = bindingResults.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            Map<String, Object> errors = new HashMap<>();
            errors.put("message", message);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        User user = new User(signUpRequest.getUsername(), passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getEmail());
        UserProfile userProfile = new UserProfile(user);
        user.setUserProfile(userProfile);
        userRepo.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);

    }
}
