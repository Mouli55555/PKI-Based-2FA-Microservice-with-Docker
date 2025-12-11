package com.example.pki2fa.model;

public class DecryptRequest {
    private String encryptedSeed;

    public String getEncryptedSeed() {
        return encryptedSeed;
    }

    public void setEncryptedSeed(String encryptedSeed) {
        this.encryptedSeed = encryptedSeed;
    }
}
