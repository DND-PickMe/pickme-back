package com.pickmebackend.controller;

import com.pickmebackend.annotation.account.CurrentUser;
import com.pickmebackend.annotation.license.LicenseValidation;
import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.License;
import com.pickmebackend.domain.dto.license.LicenseRequestDto;
import com.pickmebackend.domain.dto.license.LicenseResponseDto;
import com.pickmebackend.repository.LicenseRepository;
import com.pickmebackend.resource.LicenseResource;
import com.pickmebackend.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessage.LICENSE_NOT_FOUND;
import static com.pickmebackend.error.ErrorMessage.UNAUTHORIZED_USER;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/licenses", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;

    private final LicenseRepository licenseRepository;

    private final ErrorsFormatter errorsFormatter;

    @PostMapping
    public ResponseEntity<?> saveLicense(@RequestBody LicenseRequestDto licenseRequestDto, @CurrentUser Account currentUser) {
        LicenseResponseDto licenseResponseDto = licenseService.saveLicense(licenseRequestDto, currentUser);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(LicenseController.class).slash(licenseResponseDto.getId());
        LicenseResource licenseResource = new LicenseResource(licenseResponseDto);
        licenseResource.add(selfLinkBuilder.withRel("update-license"));
        licenseResource.add(selfLinkBuilder.withRel("delete-license"));
        licenseResource.add(new Link("/docs/index.html#resources-licenses-create").withRel("profile"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(licenseResource);
    }

    @PutMapping("/{licenseId}")
    @LicenseValidation
    public ResponseEntity<?> updateLicense(@PathVariable Long licenseId, @RequestBody LicenseRequestDto licenseRequestDto, @CurrentUser Account currentUser) {
        Optional<License> licenseOptional = this.licenseRepository.findById(licenseId);

        LicenseResponseDto modifiedLicenseResponseDto = licenseService.updateLicense(licenseOptional.get(), licenseRequestDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(LicenseController.class).slash(modifiedLicenseResponseDto.getId());
        LicenseResource licenseResource = new LicenseResource(modifiedLicenseResponseDto);
        licenseResource.add(linkTo(LicenseController.class).withRel("create-license"));
        licenseResource.add(selfLinkBuilder.withRel("delete-license"));
        licenseResource.add(new Link("/docs/index.html#resources-licenses-update").withRel("profile"));

        return new ResponseEntity<>(licenseResource, HttpStatus.OK);
    }

    @DeleteMapping("/{licenseId}")
    @LicenseValidation
    public ResponseEntity<?> deleteLicense(@PathVariable Long licenseId, @CurrentUser Account currentUser) {
        Optional<License> licenseOptional = this.licenseRepository.findById(licenseId);
        LicenseResponseDto licenseResponseDto = licenseService.deleteLicense(licenseOptional.get());
        LicenseResource licenseResource = new LicenseResource(licenseResponseDto);
        licenseResource.add(linkTo(LicenseController.class).withRel("create-license"));
        licenseResource.add(new Link("/docs/index.html#resources-licenses-delete").withRel("profile"));

        return new ResponseEntity<>(licenseResource, HttpStatus.OK);
    }
}
