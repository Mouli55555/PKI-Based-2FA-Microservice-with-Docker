package com.example.pki2fa.controller;

import com.example.pki2fa.model.DecryptRequest;
import com.example.pki2fa.model.DecryptResponse;
import com.example.pki2fa.service.DecryptService;
import org.springframework.web.bind.annotation.*;

@RestController
public class DecryptController {

    private final DecryptService decryptService;

    public DecryptController(DecryptService decryptService) {
        this.decryptService = decryptService;
    }

    @PostMapping("/decrypt-seed")
    public DecryptResponse decrypt(@RequestBody DecryptRequest request) {
        return decryptService.decryptSeed(request.getEncryptedSeed());
    }
}
