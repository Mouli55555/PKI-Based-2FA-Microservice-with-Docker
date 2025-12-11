package com.example.pki2fa.model;

public class VerifyRequest {

    private String code;

    public VerifyRequest() {}

    public VerifyRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
