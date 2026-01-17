package com.example.pki2fa.controller;

import com.example.pki2fa.model.TotpResponse;
import com.example.pki2fa.model.VerifyRequest;
import com.example.pki2fa.model.VerifyResponse;
import com.example.pki2fa.service.TotpService;
import org.springframework.web.bind.annotation.*;

@RestController
public class TfaController {

    private final TotpService totpService;

    public TfaController(TotpService totpService) {
        this.totpService = totpService;
    }

    @RequestMapping(value = "/generate-2fa", method = {RequestMethod.GET, RequestMethod.POST})
    public TotpResponse generate() {
        return totpService.generateTotp();
    }


    @PostMapping("/verify-2fa")
    public VerifyResponse verify2fa(@RequestBody VerifyRequest request) {
        return totpService.verifyTotp(request.getCode());
    }
}
