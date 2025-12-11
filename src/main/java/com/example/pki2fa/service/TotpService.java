package com.example.pki2fa.service;

import com.example.pki2fa.model.TotpResponse;
import com.example.pki2fa.model.VerifyResponse;
import com.example.pki2fa.util.FileUtil;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TotpService {

    /* ---------------------------------------------------------
     * Convert hex string → byte array
     * --------------------------------------------------------- */
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return data;
    }

    /* ---------------------------------------------------------
     * Generate a 6-digit TOTP using SHA-1, RFC6238 standard
     * --------------------------------------------------------- */
    private int generateTotpCode(byte[] key, long timeStep) throws Exception {

        // Convert timestep into an 8-byte array
        byte[] data = new byte[8];
        long value = timeStep;

        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (value & 0xFF);
            value >>= 8;
        }

        // HMAC-SHA1
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);

        byte[] hash = mac.doFinal(data);

        // Dynamic truncation
        int offset = hash[hash.length - 1] & 0xF;
        int binary =
                ((hash[offset] & 0x7F) << 24) |
                        ((hash[offset + 1] & 0xFF) << 16) |
                        ((hash[offset + 2] & 0xFF) << 8 ) |
                        (hash[offset + 3] & 0xFF);

        return binary % 1_000_000;  // 6-digit code
    }

    /* ---------------------------------------------------------
     * Step 6: Generate TOTP + Return Base32 Secret
     * --------------------------------------------------------- */
    public TotpResponse generateTotp() {
        try {
            String seedHex = FileUtil.readSeed();   // read data/seed.txt
            byte[] seedBytes = hexToBytes(seedHex); // hex → bytes

            Base32 base32 = new Base32();
            String base32Secret = base32.encodeAsString(seedBytes); // Base32 secret

            long timeStep = (System.currentTimeMillis() / 1000) / 30;
            int totp = generateTotpCode(seedBytes, timeStep);

            return new TotpResponse(
                    true,
                    base32Secret,
                    String.format("%06d", totp)
            );

        } catch (Exception e) {
            return new TotpResponse(false, null, "Error: " + e.getMessage());
        }
    }

    public VerifyResponse verifyTotp(String userCode) {

        try {
            String seedHex = FileUtil.readSeed();
            byte[] seedBytes = hexToBytes(seedHex);

            long currentStep = (System.currentTimeMillis() / 1000) / 30;

            int[] codes = new int[] {
                    generateTotpCode(seedBytes, currentStep),     // current
                    generateTotpCode(seedBytes, currentStep - 1), // previous
                    generateTotpCode(seedBytes, currentStep + 1)  // next
            };

            for (int code : codes) {
                String formatted = String.format("%06d", code);

                if (formatted.equals(userCode)) {

                    // ✔ FIXED: write to mounted docker volume
                    Files.createDirectories(Path.of("/cron-output"));
                    Files.writeString(Path.of("/cron-output/last_code.txt"), formatted);

                    return new VerifyResponse(true, "Valid 2FA code");
                }
            }

            return new VerifyResponse(false, "Invalid or expired 2FA code");

        } catch (Exception e) {
            return new VerifyResponse(false, "Error: " + e.getMessage());
        }
    }

}
