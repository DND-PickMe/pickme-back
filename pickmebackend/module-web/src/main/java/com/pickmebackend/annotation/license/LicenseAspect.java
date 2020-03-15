package com.pickmebackend.annotation.license;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.License;
import com.pickmebackend.domain.dto.license.LicenseRequestDto;
import com.pickmebackend.repository.LicenseRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessage.LICENSE_NOT_FOUND;
import static com.pickmebackend.error.ErrorMessage.UNAUTHORIZED_USER;

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
            return errorsFormatter.badRequest(LICENSE_NOT_FOUND.getValue());
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("licenseValidation() && args(licenseId, currentUser)")
    public Object deleteLicense(ProceedingJoinPoint joinPoint, Long licenseId, Account currentUser) throws Throwable {
        Optional<License> licenseOptional = this.licenseRepository.findById(licenseId);
        if (!licenseOptional.isPresent()) {
            return errorsFormatter.badRequest(LICENSE_NOT_FOUND.getValue());
        }

        License license = licenseOptional.get();
        if (!license.getAccount().getId().equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        return joinPoint.proceed();
    }
}
