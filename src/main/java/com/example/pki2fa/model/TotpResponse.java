package com.example.pki2fa.model;

public class TotpResponse {

    private boolean success;
    private String secretBase32;
    private String totp;

    public TotpResponse(boolean success, String secretBase32, String totp) {
        this.success = success;
        this.secretBase32 = secretBase32;
        this.totp = totp;
    }

    public boolean isSuccess() { return success; }
    public String getSecretBase32() { return secretBase32; }
    public String getTotp() { return totp; }
}
