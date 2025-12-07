package com.allan.climberanalyzer.analyzer.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String saveFile(MultipartFile file, String filename);

    Resource loadFile(String filename);

    void deleteFile(String filename);
}
