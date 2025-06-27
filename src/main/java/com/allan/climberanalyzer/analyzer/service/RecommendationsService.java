package com.allan.climberanalyzer.analyzer.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class RecommendationsService {
    private final ResourceLoader resourceLoader;
    private Map<String, Object> recommendations;

    @Autowired
    public RecommendationsService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadRecommendations() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:analyzation.json");
        InputStream inputStream = resource.getInputStream();

        ObjectMapper mapper = new ObjectMapper();
        this.recommendations = mapper.readValue(inputStream, Map.class);

    }

    public Map<String, Object> getRecommendation(String profile, String severity) {

        if (recommendations == null) {
            System.err.println(
                    "Recommendation data not loaded. Please ensure loadRecommendations() was called successfully.");
            return Collections.emptyMap();
        }
        Object profileObj = recommendations.get(profile);
        if (!(profileObj instanceof Map)) {
            System.err.println("Profile not loaded");
            return Collections.emptyMap();
        }
        Map<String, Object> profileMap = (Map<String, Object>) profileObj;
        Object severityObj = profileMap.get(severity);
        Map<String, Object> severityMap = (Map<String, Object>) severityObj;
        return severityMap;

    }

}
