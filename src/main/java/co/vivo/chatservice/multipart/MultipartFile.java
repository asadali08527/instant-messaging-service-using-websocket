package co.vivo.chatservice.multipart;

import jakarta.ws.rs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFile {
    // Define fields for the file and any additional data you need
    @FormParam("file")
    @MultipartForm
    @PartType("application/octet-stream")
    private InputStream file;

    @FormParam("fileName")
    @PartType("text/plain")
    private String fileName;

    public MultipartFile() {
    }

    public MultipartFile(InputStream file, String fileName) {
        this.file = file;
        this.fileName = fileName;
    }

    // Getters and setters
    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = file.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }
}
