package com.allan.climberanalyzer.analyzer.controller;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.allan.climberanalyzer.UserHandling.service.JwtService;
import com.allan.climberanalyzer.analyzer.service.DatabaseRateLimiter;
import com.allan.climberanalyzer.analyzer.service.ImageService;
import com.allan.climberanalyzer.analyzer.service.StorageService;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Autowired
    private StorageService storageService;

    @Autowired
    private ImageService imageService;

    @Value("${app.image-url-base}")
    private String imageUrlBase;

    @Autowired
    DatabaseRateLimiter rateLimiter;

    @Autowired
    JwtService jwtService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadTempImage(@RequestParam("file") MultipartFile file,
            @RequestParam("token") String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        if (!rateLimiter.isAllowed(userId, "image-upload", 3, 60)) {
            return new ResponseEntity<>("Image upload limit exceeded (3 per hour). Please try again later",
                    HttpStatus.TOO_MANY_REQUESTS);
        }
        if (file.isEmpty()) {
            return new ResponseEntity<>("No file uploaded. Try again.", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return new ResponseEntity<>("File too large, maximum size is 5MB", HttpStatus.BAD_REQUEST);
        }
        String filename = imageService.saveTemporaryImage(file);
        String imageUrl = imageUrlBase + "/" + filename;
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename) {
        try {
            Resource image = storageService.loadFile(filename);
            String contentType = imageService.getContentType(filename);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
