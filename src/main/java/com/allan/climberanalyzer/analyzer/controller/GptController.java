package com.allan.climberanalyzer.analyzer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allan.climberanalyzer.analyzer.DTOClass.ExerciseDisplayDTO;
import com.allan.climberanalyzer.analyzer.DTOClass.GptRequest;
import com.allan.climberanalyzer.analyzer.DTOClass.GptResponse;
import com.allan.climberanalyzer.analyzer.service.GptService;

@RestController
@RequestMapping("/api/gpt")
public class GptController {

    @Autowired
    private GptService gptService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody GptRequest request) {
        try {
            List<ExerciseDisplayDTO> response = gptService.generateResponse(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
