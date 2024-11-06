package co.vivo.chatservice.service;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.vivo.chatservice.factory.S3ClientFactory;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@ApplicationScoped
public class MediaService {

    private static final Logger logger = LoggerFactory.getLogger(MediaService.class);
    private static final String BUCKET_NAME = System.getenv("AWS_BUCKET_NAME");
    private static final String REGION = System.getenv("AWS_REGION");
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public MediaService() {
        this.s3Client = S3ClientFactory.createS3Client();
        this.s3Presigner = S3ClientFactory.createS3Presigner();
    }

    /**
     * Uploads media to the specified S3 bucket and returns the pre-signed URL for download.
     */
    public String uploadMedia(byte[] fileData, String fileName) throws IOException {
        if (!doesBucketExist(BUCKET_NAME)) {
            createBucket(BUCKET_NAME);
        }

        String uniqueFileName = UUID.randomUUID().toString() + "-" + fileName;
        PutObjectResponse response = s3Client.putObject(PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(uniqueFileName)
                .build(), RequestBody.fromBytes(fileData));

        logger.info("File uploaded to S3 with response: {}", response);

        return generatePreSignedUrl(BUCKET_NAME, uniqueFileName);
    }

    /**
     * Generates a pre-signed URL to access the uploaded file.
     */
    public String generatePreSignedUrl(String bucketName, String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    /**
     * Checks if the S3 bucket exists.
     */
    private boolean doesBucketExist(String bucketName) {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;  // Bucket doesn't exist
            }
            throw e;  // Other S3 errors
        }
    }

    /**
     * Creates a new S3 bucket if it doesn't exist.
     */
    private void createBucket(String bucketName) {
        try {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .createBucketConfiguration(CreateBucketConfiguration.builder()
                            .locationConstraint(REGION)
                            .build())
                    .build();
            s3Client.createBucket(createBucketRequest);
            logger.info("S3 bucket created: {}", bucketName);
        } catch (S3Exception e) {
            logger.error("Error creating S3 bucket: {}", bucketName, e);
            throw new RuntimeException("Failed to create bucket: " + bucketName, e);
        }
    }
}
