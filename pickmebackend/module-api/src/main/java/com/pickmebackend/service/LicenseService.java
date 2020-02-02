package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.License;
import com.pickmebackend.domain.dto.LicenseDto;
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

    public ResponseEntity<?> saveLicense(LicenseDto licenseDto, Account currentUser) {
        License license = modelMapper.map(licenseDto, License.class);

        license.mapAccount(currentUser);
        return new ResponseEntity<>(licenseRepository.save(license), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateLicense(Long licenseId, LicenseDto licenseDto, Account currentUser) {
        Optional<License> licenseOptional = licenseRepository.findById(licenseId);
        if (!licenseOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(LICENSENOTFOUND), HttpStatus.BAD_REQUEST);
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        modelMapper.map(licenseDto, license);
        return new ResponseEntity<>(licenseRepository.save(license), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteLicense(Long licenseId, Account currentUser) {
        Optional<License> licenseOptional = licenseRepository.findById(licenseId);
        if (!licenseOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(LICENSENOTFOUND), HttpStatus.BAD_REQUEST);
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        licenseRepository.delete(license);
        return ResponseEntity.ok().build();
    }
}
