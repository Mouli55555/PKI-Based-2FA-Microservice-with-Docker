package com.example.pki2fa.model;

public class VerifyResponse {

    private boolean success;
    private String message;

    public VerifyResponse() {}

    public VerifyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
