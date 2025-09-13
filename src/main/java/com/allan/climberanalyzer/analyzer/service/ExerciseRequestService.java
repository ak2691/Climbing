package com.allan.climberanalyzer.analyzer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import com.allan.climberanalyzer.analyzer.DTOClass.CreateExerciseRequest;
import com.allan.climberanalyzer.analyzer.DTOClass.ReviewExerciseRequest;
import com.allan.climberanalyzer.analyzer.model.ExerciseImage;
import com.allan.climberanalyzer.analyzer.model.ExerciseModel;
import com.allan.climberanalyzer.analyzer.model.ExerciseRequest;
import com.allan.climberanalyzer.analyzer.model.ExerciseRequestImage;
import com.allan.climberanalyzer.analyzer.model.RequestStatus;
import com.allan.climberanalyzer.analyzer.repo.ExerciseRequestImageRepo;
import com.allan.climberanalyzer.analyzer.repo.ExerciseRequestRepo;
import com.allan.climberanalyzer.analyzer.repo.ExercisesRepo;
import com.allan.climberanalyzer.analyzer.repo.ImageRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ExerciseRequestService {
    private static final Logger log = LoggerFactory.getLogger(ExerciseRequestService.class);

    @Autowired
    private ExerciseRequestRepo exerciseRequestRepository;

    @Autowired
    private ExerciseRequestImageRepo exerciseRequestImageRepo;

    @Autowired
    private ExercisesRepo exerciseRepo;

    @Autowired
    private ImageRepo imageRepo;

    @Autowired
    private ImageService imageService;

    public ExerciseRequest createExerciseRequest(CreateExerciseRequest dto, Long userId) {

        if (exerciseRequestRepository.existsByUserIdAndStatus(userId, RequestStatus.PENDING)) {
            throw new IllegalStateException(
                    "You already have a pending exercise request. Please wait for it to be reviewed or cancel it first.");
        }

        ExerciseRequest request = new ExerciseRequest(dto.getName(), dto.getDescription(), userId);
        Pattern pattern = Pattern.compile("src=\"http://localhost:8080/api/images/([^\"]+)\"");
        Matcher matcher = pattern.matcher(request.getDescription());

        while (matcher.find()) {
            String filename = matcher.group(1);
            ExerciseImage exerciseImage = imageRepo.findByFilename(filename).orElse(null);
            request.getImages().add(exerciseImage);
            exerciseImage.setExerciseRequest(request);
        }
        request = exerciseRequestRepository.save(request);

        return request;
    }

    public Optional<ExerciseRequest> getUserPendingRequest(Long userId) {
        return exerciseRequestRepository.findByUserIdAndStatus(userId, RequestStatus.PENDING);
    }

    public List<ExerciseRequest> getUserRequestHistory(Long userId) {
        return exerciseRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void cancelUserRequest(Long userId) {
        ExerciseRequest request = exerciseRequestRepository.findByUserIdAndStatus(userId, RequestStatus.PENDING)
                .orElseThrow(() -> new IllegalStateException("No pending request found"));
        Pattern pattern = Pattern.compile("src=\"http://localhost:8080/api/images/([^\"]+)\"");
        Matcher matcher = pattern.matcher(request.getDescription());

        while (matcher.find()) {
            String filename = matcher.group(1);
            imageRepo.deleteByFilename(filename);
        }
        exerciseRequestRepository.delete(request);

    }

    public ReviewExerciseRequest convertRequestToDTO(ExerciseRequest request) {
        ReviewExerciseRequest dto = new ReviewExerciseRequest();
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        dto.setReviewNotes(request.getReviewNotes());
        dto.setRequestId(request.getId());
        return dto;
    }

    public List<ReviewExerciseRequest> getPendingRequests() {
        List<ExerciseRequest> exerciseRequests = exerciseRequestRepository
                .findByStatusOrderByCreatedAtAsc(RequestStatus.PENDING);
        List<ReviewExerciseRequest> dtoRequests = exerciseRequests.stream().map((exercise) -> {
            ReviewExerciseRequest request = new ReviewExerciseRequest();
            request.setName(exercise.getName());
            request.setDescription(exercise.getDescription());
            request.setStatus(RequestStatus.PENDING);
            request.setReviewNotes(exercise.getReviewNotes());
            request.setRequestId(exercise.getId());
            return request;
        }).collect(Collectors.toList());
        return dtoRequests;
    }

    public ReviewExerciseRequest getMyPending(Long userId) {
        ExerciseRequest request = exerciseRequestRepository.findByUserIdAndStatus(userId, RequestStatus.PENDING)
                .orElse(null);
        ReviewExerciseRequest userRequest = new ReviewExerciseRequest();
        if (request == null) {
            return null;
        }
        if (request.getName() != null) {
            userRequest.setName(request.getName());
        }
        if (request.getDescription() != null) {
            userRequest.setDescription(request.getDescription());

        }

        if (request.getReviewNotes() != null) {
            userRequest.setReviewNotes(request.getReviewNotes());
        }
        userRequest.setStatus(request.getStatus());
        userRequest.setRequestId(request.getId());
        return userRequest;

    }

    public ReviewExerciseRequest reviewRequest(Long requestId, ReviewExerciseRequest dto, Long reviewerId) {
        ExerciseRequest request = exerciseRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request has already been reviewed");
        }

        request.setStatus(dto.getStatus());
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewerId);
        request.setReviewNotes(dto.getReviewNotes());

        if (dto.getStatus() == RequestStatus.APPROVED) {
            convertRequestToExercise(request);
        }

        exerciseRequestRepository.save(request);
        ReviewExerciseRequest ret = convertRequestToDTO(request);
        if (dto.getStatus() == RequestStatus.REJECTED) {

            exerciseRequestRepository.delete(request);
            Pattern pattern = Pattern.compile("src=\"http://localhost:8080/api/images/([^\"]+)\"");
            Matcher matcher = pattern.matcher(request.getDescription());

            while (matcher.find()) {
                String filename = matcher.group(1);
                imageRepo.deleteByFilename(filename);
            }

        }
        return ret;
    }

    private void convertRequestToExercise(ExerciseRequest request) {
        // Create the Exercise
        ExerciseModel exercise = request.toExercise();
        exercise = exerciseRepo.save(exercise);

    }

    private void cleanupRequestImagesTest(Long requestId) {
        List<ExerciseRequestImage> images = exerciseRequestImageRepo.findAllById(requestId);
        for (ExerciseRequestImage image : images) {
            try {
                ExerciseImage exerciseImage = imageRepo.findByFilename(image.getFilename()).orElse(null);
                imageRepo.deleteById(exerciseImage.getId());
                exerciseRequestImageRepo.delete(image);
            } catch (Exception e) {
                log.error("Failed to delete image: " + image.getFilename(), e);
            }
        }
    }

}
