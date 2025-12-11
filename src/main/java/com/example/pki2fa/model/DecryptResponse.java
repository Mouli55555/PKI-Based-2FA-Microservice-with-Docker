package com.example.pki2fa.model;

public class DecryptResponse {

    private boolean success;
    private String message;
    private String decryptedSeedHex;

    public DecryptResponse(boolean success, String message, String decryptedSeedHex) {
        this.success = success;
        this.message = message;
        this.decryptedSeedHex = decryptedSeedHex;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getDecryptedSeedHex() { return decryptedSeedHex; }
}
