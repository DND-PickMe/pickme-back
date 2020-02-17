package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.License;
import com.pickmebackend.domain.dto.license.LicenseRequestDto;
import com.pickmebackend.domain.dto.license.LicenseResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import static com.pickmebackend.error.ErrorMessageConstant.*;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> saveLicense(LicenseRequestDto licenseRequestDto, Account currentUser) {
        License license = modelMapper.map(licenseRequestDto, License.class);

        license.mapAccount(currentUser);
        License savedLicense = this.licenseRepository.save(license);
        LicenseResponseDto licenseResponseDto = modelMapper.map(savedLicense, LicenseResponseDto.class);

        return new ResponseEntity<>(licenseResponseDto, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateLicense(Long licenseId, LicenseRequestDto licenseRequestDto, Account currentUser) {
        Optional<License> licenseOptional = this.licenseRepository.findById(licenseId);
        if (!licenseOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(LICENSENOTFOUND), HttpStatus.BAD_REQUEST);
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        modelMapper.map(licenseRequestDto, license);
        License modifiedLicense = this.licenseRepository.save(license);
        LicenseResponseDto licenseResponseDto = modelMapper.map(modifiedLicense, LicenseResponseDto.class);

        return new ResponseEntity<>(licenseResponseDto, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteLicense(Long licenseId, Account currentUser) {
        Optional<License> licenseOptional = this.licenseRepository.findById(licenseId);
        if (!licenseOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(LICENSENOTFOUND), HttpStatus.BAD_REQUEST);
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        this.licenseRepository.delete(license);
        return ResponseEntity.ok().build();
    }
}
