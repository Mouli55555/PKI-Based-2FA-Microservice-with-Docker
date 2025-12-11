package com.example.pki2fa.service;

import com.example.pki2fa.model.DecryptResponse;
import com.example.pki2fa.util.RsaUtil;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class DecryptService {

    private PrivateKey loadPrivateKey() throws Exception {

        String keyPem = Files.readString(Path.of("/app/student_private.pem"))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(keyPem);
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }



    public DecryptResponse decryptSeed(String encryptedSeed) {

        try {
            // Load private key correctly
            PrivateKey privateKey = loadPrivateKey();

            // Decrypt and get bytes
            byte[] seedBytes = RsaUtil.decryptOaepSha256(encryptedSeed, privateKey);

            // Convert to lowercase hex
            StringBuilder hex = new StringBuilder();
            for (byte b : seedBytes) {
                hex.append(String.format("%02x", b));
            }
            String seedHex = hex.toString();

            // Save to data/seed.txt
            Files.createDirectories(Path.of("data"));
            Files.writeString(Path.of("data/seed.txt"), seedHex);

            return new DecryptResponse(true, "Seed decrypted successfully", seedHex);

        } catch (Exception e) {
            e.printStackTrace();
            return new DecryptResponse(false, "Decryption failed: " + e.getMessage(), null);
        }
    }
}
