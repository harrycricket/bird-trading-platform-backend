package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.enums.ContentType;
import com.gangoffive.birdtradingplatform.util.S3Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
@Slf4j
public class UploadController {
    @GetMapping
    public String viewHomePage() {
        return "upload";
    }

    @PostMapping
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<?> handleUploadForm(Model model, String description,
                                           @RequestParam("multipart") MultipartFile multipart) throws IOException {
        log.info("multipart {}", multipart.getInputStream());
        String fileName = multipart.getOriginalFilename();
        int dotIndex = fileName.lastIndexOf(".");
        String typeFile = fileName.substring(dotIndex + 1);
        String newFilename = UUID.randomUUID().toString() + fileName.substring(dotIndex);
        String contentType = Arrays.stream(ContentType.values())
                .filter(
                        a -> a.name().contains(typeFile))
                .map(
                        a -> ContentType.getValue(a)
                ).findFirst()
                .get();
        if (contentType.contains("image")) {
            newFilename = "image/" + newFilename;
        } else if (contentType.contains("video")) {
            newFilename = "video/" + newFilename;
        }

        System.out.println("Description: " + description);
        System.out.println("filename: " + newFilename);

        String message = "";

        try {
            S3Utils.uploadFile(newFilename, multipart.getInputStream());
            message = "Your file has been uploaded successfully!";
        } catch (Exception ex) {
            message = "Error uploading file: " + ex.getMessage();
        }

        model.addAttribute("message", message);

        return ResponseEntity.ok("ok");
    }
}
