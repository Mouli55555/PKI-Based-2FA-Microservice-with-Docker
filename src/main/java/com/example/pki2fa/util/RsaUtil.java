package com.example.pki2fa.util;

import org.bouncycastle.util.encoders.Hex;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

public class RsaUtil {

    private RsaUtil() {}

    public static byte[] decryptOaepSha256(String encrypted, PrivateKey privateKey) throws Exception {

        byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);

        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                new MGF1ParameterSpec("SHA-256"),
                PSource.PSpecified.DEFAULT
        );

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

        return cipher.doFinal(encryptedBytes);
    }

}
