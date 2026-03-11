package com.ciesa.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    @Value("${firebase.bucket}")
    private String bucket;

    /**
     * Sube una imagen a Firebase Storage y retorna la URL pública.
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        // Nombre único para evitar colisiones
        String fileName = folder + "/" + UUID.randomUUID() + extension;

        Storage storage = StorageClient.getInstance().bucket().getStorage();
        BlobId blobId = BlobId.of(bucket, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        // Retorna URL pública de Firebase Storage
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket,
                URLEncoder.encode(fileName, StandardCharsets.UTF_8)
        );
    }
}
