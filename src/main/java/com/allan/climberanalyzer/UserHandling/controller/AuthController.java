package com.allan.climberanalyzer.UserHandling.controller;

import java.nio.file.attribute.UserPrincipal;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import com.allan.climberanalyzer.UserHandling.DTO.AuthResponse;
import com.allan.climberanalyzer.UserHandling.DTO.VerificationRequest;
import com.allan.climberanalyzer.UserHandling.model.JwtResponse;
import com.allan.climberanalyzer.UserHandling.model.LoginRequest;
import com.allan.climberanalyzer.UserHandling.model.SignUpRequest;
import com.allan.climberanalyzer.UserHandling.model.User;
import com.allan.climberanalyzer.UserHandling.model.UserProfile;
import com.allan.climberanalyzer.UserHandling.repo.RefreshTokenRepo;
import com.allan.climberanalyzer.UserHandling.repo.UserProfileRepo;
import com.allan.climberanalyzer.UserHandling.repo.UserRepo;
import com.allan.climberanalyzer.UserHandling.service.CustomUserDetailsService;
import com.allan.climberanalyzer.UserHandling.service.EmailService;
import com.allan.climberanalyzer.UserHandling.service.JwtService;
import com.allan.climberanalyzer.UserHandling.service.VerificationService;
import com.allan.climberanalyzer.analyzer.DTOClass.ProfileDTO;
import com.allan.climberanalyzer.config.JwtFilter;

import jakarta.servlet.http.Cookie;
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

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    RefreshTokenRepo refreshTokenRepo;
    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationService verificationService;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

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
            Optional<User> userOptional = userRepo.findByUsername(loginRequest.getUsername());
            if (userOptional.isPresent() && !userOptional.get().getEnabled()) {
                // Create a specific error response for unverified accounts
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Account not verified yet, please verify");
                errorResponse.put("errorCode", "ACCOUNT_NOT_VERIFIED");
                errorResponse.put("email", userOptional.get().getEmail());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            // Only useful if we happen to switch to stateful sessions
            // SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtService.generateAccessToken(loginRequest.getUsername());
            String refreshToken = jwtService.generateRefreshToken(loginRequest.getUsername());
            // Set refresh token as httpOnly cookie
            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            // refreshCookie.setSecure(true); // Use in production with HTTPS
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(refreshCookie);

            return new ResponseEntity<>(new AuthResponse(accessToken, "Bearer"), HttpStatus.OK);
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
                    .map(error -> error.getField() + " " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            Map<String, Object> errors = new HashMap<>();
            errors.put("message", message);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByUsername(signUpRequest.getUsername())) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Username already exists");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Email already exists");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        String verificationCode = verificationService.generateVerificationCode();
        emailService.sendHtmlVerificationEmail(signUpRequest.getEmail(), verificationCode);

        User user = new User(signUpRequest.getUsername(), passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getEmail());
        user.setVerificationCode(verificationCode);
        user.setVerificationExpiration();
        UserProfile userProfile = new UserProfile(user);
        user.setUserProfile(userProfile);
        userRepo.save(user);

        return new ResponseEntity<>(
                signUpRequest.getEmail(), HttpStatus.OK);

    }

    @PostMapping("/verifyuser")
    public ResponseEntity<?> verifyUser(@RequestBody VerificationRequest verificationRequest) {
        try {
            Optional<User> optionalUser = userRepo.findByEmail(verificationRequest.getEmail());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (user.getVerificationExpiration().isBefore(LocalDateTime.now())) {
                    throw new RuntimeException("Verification code expired");
                }
                if (user.getVerificationCode().equals(verificationRequest.getVerificationCode())) {
                    user.setEnabled(true);
                    user.setVerificationCode(null);
                    userRepo.save(user);
                } else {
                    throw new RuntimeException("Invalid verification code");
                }
            }
            return new ResponseEntity<>("Verification successful!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("No refresh token provided");
        }

        try {
            // Extract username from token (even if token is expired, we can still get the
            // username)
            String username = jwtService.getUsernameFromToken(refreshToken);

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate refresh token with user details
            if (!jwtService.validateRefreshToken(refreshToken, userDetails)) {
                return ResponseEntity.status(401).body("Invalid refresh token");
            }

            // Generate new access token
            String newAccessToken = jwtService.generateAccessToken(username);

            return ResponseEntity.ok(new AuthResponse(newAccessToken, "Bearer"));

        } catch (Exception e) {
            // Could be expired token, invalid signature, user not found, etc.
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String accessToken) {
        try {
            jwtService.deleteTokenByUsername(accessToken);

            return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate() {

        return new ResponseEntity<>("Success!", HttpStatus.OK);

    }
}
