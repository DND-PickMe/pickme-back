package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.License;
import com.pickmebackend.domain.dto.license.LicenseRequestDto;
import com.pickmebackend.domain.dto.license.LicenseResponseDto;
import com.pickmebackend.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;

    private final ModelMapper modelMapper;

    public LicenseResponseDto saveLicense(LicenseRequestDto licenseRequestDto, Account currentUser) {
        License license = modelMapper.map(licenseRequestDto, License.class);
        license.mapAccount(currentUser);
        License savedLicense = this.licenseRepository.save(license);
        LicenseResponseDto licenseResponseDto = modelMapper.map(savedLicense, LicenseResponseDto.class);

        return licenseResponseDto;
    }

    public LicenseResponseDto updateLicense(License license, LicenseRequestDto licenseRequestDto) {
        modelMapper.map(licenseRequestDto, license);
        License modifiedLicense = this.licenseRepository.save(license);
        LicenseResponseDto licenseResponseDto = modelMapper.map(modifiedLicense, LicenseResponseDto.class);

        return licenseResponseDto;
    }

    public LicenseResponseDto deleteLicense(License license) {
        LicenseResponseDto licenseResponseDto = modelMapper.map(license, LicenseResponseDto.class);
        this.licenseRepository.delete(license);

        return licenseResponseDto;
    }
}
