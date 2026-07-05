package com.example.community.service;

import com.example.community.dto.response.FileUploadResponse;
import com.example.community.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public FileUploadResponse save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidRequestException();
        }

        String savedName = buildSavedName(file.getOriginalFilename());

        File folder = new File(uploadDir).getAbsoluteFile();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new InvalidRequestException();
        }

        try {
            file.transferTo(new File(folder, savedName).getAbsoluteFile());
        } catch (IOException e) {
            throw new InvalidRequestException();
        }

        String url = baseUrl + "/uploads/" + savedName;

        return new FileUploadResponse(url);
    }

    private String buildSavedName(String originalName) {
        String uuid = UUID.randomUUID().toString();

        if (originalName == null || originalName.isBlank()) {
            return uuid + ".jpg";
        }

        String safeName = originalName.replaceAll("[\\\\/:*?\"<>|]", "_");
        return uuid + "_" + safeName;
    }
}