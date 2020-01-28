package com.pickmebackend.controller;

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
    ResponseEntity<?> saveLicense(@RequestBody LicenseDto licenseDto) {
        return licenseService.saveLicense(licenseDto);
    }

    @PutMapping("/{licenseId}")
    ResponseEntity<?> updateLicense(@PathVariable Long licenseId, @RequestBody LicenseDto licenseDto) {
        return licenseService.updateLicense(licenseId, licenseDto);
    }

    @DeleteMapping("/{licenseId}")
    ResponseEntity<?> deleteLicense(@PathVariable Long licenseId) {
        return licenseService.deleteLicense(licenseId);
    }
}
