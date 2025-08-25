package com.allan.climberanalyzer.analyzer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allan.climberanalyzer.UserHandling.model.Role;
import com.allan.climberanalyzer.UserHandling.model.User;
import com.allan.climberanalyzer.UserHandling.repo.UserRepo;
import com.allan.climberanalyzer.UserHandling.service.JwtService;
import com.allan.climberanalyzer.analyzer.DTOClass.CreateExerciseRequest;
import com.allan.climberanalyzer.analyzer.DTOClass.ReviewExerciseRequest;
import com.allan.climberanalyzer.analyzer.model.ExerciseRequest;
import com.allan.climberanalyzer.analyzer.service.ExerciseRequestService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exercise-requests")
public class ExerciseRequestController {
    @Autowired
    private ExerciseRequestService exerciseRequestService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("No valid JWT token found");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return jwtService.getUserIdFromToken(token); // Your method to extract user ID
    }

    @PostMapping("/request")
    public ResponseEntity<?> createExerciseRequest(@Valid @RequestBody CreateExerciseRequest dto,
            BindingResult bindingResults, HttpServletRequest request) {

        if (bindingResults.hasErrors()) {
            String message = bindingResults.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            Map<String, Object> errors = new HashMap<>();
            errors.put("message", message);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        try {
            Long userId = getUserIdFromToken(request);
            ExerciseRequest exerciseRequest = exerciseRequestService.createExerciseRequest(dto, userId);
            return new ResponseEntity<>(exerciseRequest, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/my-pending")
    public ResponseEntity<?> getMyPendingRequest(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            ReviewExerciseRequest userRequest = exerciseRequestService.getMyPending(userId);
            return new ResponseEntity<>(userRequest, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelMyRequest(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            exerciseRequestService.cancelUserRequest(userId);
            return new ResponseEntity<>("Request cancelled successfully!", HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/pending")

    public ResponseEntity<?> getPendingRequests(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            User user = userRepo.getReferenceById(userId);
            if (!(user.getRole() == Role.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }
            List<ReviewExerciseRequest> requests = exerciseRequestService.getPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token"));
        }

    }

    @PostMapping("/admin/{requestId}/review")

    public ResponseEntity<?> reviewRequest(
            @PathVariable Long requestId,
            @RequestBody ReviewExerciseRequest dto,
            HttpServletRequest request) {

        try {
            Long reviewerId = getUserIdFromToken(request);
            ReviewExerciseRequest exerciseRequest = exerciseRequestService.reviewRequest(requestId, dto, reviewerId);
            return new ResponseEntity<>(exerciseRequest, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
