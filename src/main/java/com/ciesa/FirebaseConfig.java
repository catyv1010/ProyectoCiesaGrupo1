package com.ciesa;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.key}")
    private Resource firebaseKey;

    @Value("${firebase.bucket}")
    private String bucket;

    @PostConstruct
    public void init() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream credentialsStream = obtenerCredenciales();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .setStorageBucket(bucket)
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }

    /**
     * Obtiene las credenciales de Firebase.
     * En Render (producción): lee la variable de entorno FIREBASE_JSON (Base64).
     * En local: lee el archivo firebase-key.json del classpath.
     */
    private InputStream obtenerCredenciales() throws IOException {
        String firebaseJsonBase64 = System.getenv("FIREBASE_JSON");
        if (firebaseJsonBase64 != null && !firebaseJsonBase64.isEmpty()) {
            byte[] jsonBytes = Base64.getDecoder().decode(firebaseJsonBase64);
            return new ByteArrayInputStream(jsonBytes);
        }
        return firebaseKey.getInputStream();
    }
}
