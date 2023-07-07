package com.gangoffive.birdtradingplatform.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class FileNameUtils {
    public static String getNewImageFileName(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        String newFilename = UUID.randomUUID() + "." + contentType.substring(6);
        newFilename = "image/" + newFilename;
        return newFilename;
    }

    public static String getNewVideoFileName(MultipartFile multipartVideo) {
        String contentType = multipartVideo.getContentType();
        String newFilename = UUID.randomUUID() + "." + contentType.substring(6);
        newFilename = "video/" + newFilename;
        return newFilename;
    }
}
