package com.gangoffive.birdtradingplatform.util;

import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.enums.ContentType;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class S3Utils {
    private static final String BUCKET = "bird-trading-platform";
    private static AppProperties appProperties;

    public S3Utils(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public static void uploadFile(String fileName, InputStream inputStream)
            throws AwsServiceException, SdkClientException, IOException {
        int dotIndex = fileName.lastIndexOf(".");
        String typeFile = fileName.substring(dotIndex + 1);
//        for (ContentType type: ContentType.values()) {
//            if (type.name().equalsIgnoreCase(typeFile)) {
//                contentType = ContentType.getValue(type);
//                break;
//            }
//        }
        String contentType = Arrays.stream(ContentType.values())
                .filter(
                        a -> a.name().contains(typeFile))
                .map(
                        a -> ContentType.getValue(a)
                ).findFirst()
                .get();
        S3Client client = S3Client.builder()
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(
                        new AwsCredentialsProvider() {
                            @Override
                            public AwsCredentials resolveCredentials() {
                                return new AwsCredentials() {
                                    @Override
                                    public String accessKeyId() {
                                        return appProperties.getAws().getAccessKey();
                                    }

                                    @Override
                                    public String secretAccessKey() {
                                        return appProperties.getAws().getSecretKey();
                                    }
                                };
                            }
                        }
                )
                .build();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .contentType(contentType)
//                .acl("public-read")
                .build();
        client.putObject(
                request,
                RequestBody.fromInputStream(
                        inputStream,
                        inputStream.available()
                )
        );

        S3Waiter waiter = client.waiter();
        HeadObjectRequest waitRequest = HeadObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .build();

        WaiterResponse<HeadObjectResponse> waiterResponse = waiter.waitUntilObjectExists(waitRequest);
        waiterResponse.matched().response().ifPresent(x -> {
            // run custom code that should be executed after the upload file exists
        });
    }

    public static void deleteFile(String fileName)
            throws AwsServiceException, SdkClientException {
        S3Client client = S3Client.builder()
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(() -> new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return appProperties.getAws().getAccessKey();
                    }

                    @Override
                    public String secretAccessKey() {
                        return appProperties.getAws().getSecretKey();
                    }
                })
                .build();

        client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET)
                .key(fileName)
                .build());
    }

}
