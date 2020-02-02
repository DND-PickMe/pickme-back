package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.LicenseDto;
import com.pickmebackend.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/licenses", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;

    @PostMapping
    ResponseEntity<?> saveLicense(@RequestBody LicenseDto licenseDto, @CurrentUser Account currentUser) {
        return licenseService.saveLicense(licenseDto, currentUser);
    }

    @PutMapping("/{licenseId}")
    ResponseEntity<?> updateLicense(@PathVariable Long licenseId, @RequestBody LicenseDto licenseDto, @CurrentUser Account currentUser) {
        return licenseService.updateLicense(licenseId, licenseDto, currentUser);
    }

    @DeleteMapping("/{licenseId}")
    ResponseEntity<?> deleteLicense(@PathVariable Long licenseId, @CurrentUser Account currentUser) {
        return licenseService.deleteLicense(licenseId, currentUser);
    }
}
