package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.License;
import com.pickmebackend.domain.dto.license.LicenseRequestDto;
import com.pickmebackend.domain.dto.license.LicenseResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.LicenseRepository;
import com.pickmebackend.resource.LicenseResource;
import com.pickmebackend.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import static com.pickmebackend.error.ErrorMessageConstant.LICENSENOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/licenses", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;

    private final LicenseRepository licenseRepository;

    @PostMapping
    ResponseEntity<?> saveLicense(@RequestBody LicenseRequestDto licenseRequestDto, @CurrentUser Account currentUser) {
        LicenseResponseDto licenseResponseDto = licenseService.saveLicense(licenseRequestDto, currentUser);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(LicenseController.class).slash(licenseResponseDto.getId());
        LicenseResource licenseResource = new LicenseResource(licenseResponseDto);
        licenseResource.add(selfLinkBuilder.withRel("update-license"));
        licenseResource.add(selfLinkBuilder.withRel("delete-license"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(licenseResource);
    }

    @PutMapping("/{licenseId}")
    ResponseEntity<?> updateLicense(@PathVariable Long licenseId, @RequestBody LicenseRequestDto licenseRequestDto, @CurrentUser Account currentUser) {
        Optional<License> licenseOptional = this.licenseRepository.findById(licenseId);
        if (!licenseOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(LICENSENOTFOUND), HttpStatus.BAD_REQUEST);
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        LicenseResponseDto modifiedLicenseResponseDto = licenseService.updateLicense(license, licenseRequestDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(LicenseController.class).slash(modifiedLicenseResponseDto.getId());
        LicenseResource licenseResource = new LicenseResource(modifiedLicenseResponseDto);
        licenseResource.add(linkTo(LicenseController.class).withRel("create-license"));
        licenseResource.add(selfLinkBuilder.withRel("delete-license"));

        return new ResponseEntity<>(licenseResource, HttpStatus.OK);
    }

    @DeleteMapping("/{licenseId}")
    ResponseEntity<?> deleteLicense(@PathVariable Long licenseId, @CurrentUser Account currentUser) {
        Optional<License> licenseOptional = this.licenseRepository.findById(licenseId);
        if (!licenseOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(LICENSENOTFOUND), HttpStatus.BAD_REQUEST);
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        LicenseResponseDto licenseResponseDto = licenseService.deleteLicense(license);
        LicenseResource licenseResource = new LicenseResource(licenseResponseDto);
        licenseResource.add(linkTo(LicenseController.class).withRel("create-license"));

        return new ResponseEntity<>(licenseResource, HttpStatus.OK);
    }
}
