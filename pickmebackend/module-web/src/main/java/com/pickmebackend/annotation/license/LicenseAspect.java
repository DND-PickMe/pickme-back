package com.pickmebackend.annotation.license;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Experience;
import com.pickmebackend.domain.License;
import com.pickmebackend.domain.dto.experience.ExperienceRequestDto;
import com.pickmebackend.domain.dto.license.LicenseRequestDto;
import com.pickmebackend.repository.ExperienceRepository;
import com.pickmebackend.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessage.*;

@RequiredArgsConstructor
@Component
@Aspect
public class LicenseAspect {

    private final LicenseRepository licenseRepository;

    private final ErrorsFormatter errorsFormatter;

    @Pointcut("@annotation(LicenseValidation)")
    public void licenseValidation() {}

    @Around("licenseValidation() && args(licenseId, licenseRequestDto, currentUser)")
    public Object updateLicense(ProceedingJoinPoint joinPoint, Long licenseId,
                                   LicenseRequestDto licenseRequestDto, Account currentUser) throws Throwable {
        Optional<License> licenseOptional = this.licenseRepository.findById(licenseId);
        if (!licenseOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(LICENSE_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZED_USER.getValue()), HttpStatus.BAD_REQUEST);
        }
        return joinPoint.proceed();
    }

    @Around("licenseValidation() && args(licenseId, currentUser)")
    public Object deleteLicense(ProceedingJoinPoint joinPoint, Long licenseId, Account currentUser) throws Throwable {
        Optional<License> licenseOptional = this.licenseRepository.findById(licenseId);
        if (!licenseOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(LICENSE_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZED_USER.getValue()), HttpStatus.BAD_REQUEST);
        }
        return joinPoint.proceed();
    }
}
