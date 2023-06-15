package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.ProductShopOwnerDto;
import com.gangoffive.birdtradingplatform.enums.ContentType;
import com.gangoffive.birdtradingplatform.util.S3Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
@Slf4j
public class UploadController {
    @GetMapping
    public String viewHomePage() throws IOException {
        String base64Image = new String(Files.readAllBytes(Paths.get("C:\\Users\\Hp\\Downloads\\message (1).txt")));
        log.info("{}", base64Image.substring(0, 19));
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        S3Utils.uploadFile("acdasd.png", inputStream);
        return "upload";
    }

    @PostMapping
    public ResponseEntity<?> handleUploadForm(
            @RequestParam("multipart") List<MultipartFile> multipartFiles,
            @ModelAttribute ProductShopOwnerDto productShopOwnerDto
    ) {
        log.info("productShopOwnerDto {}", productShopOwnerDto);
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
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

            log.info("filename: {}", newFilename);

            try {
                S3Utils.uploadFile(newFilename, multipartFile.getInputStream());

            } catch (Exception ex) {
                ex.getMessage();
            }
        }


        return ResponseEntity.ok("ok");
    }
}
