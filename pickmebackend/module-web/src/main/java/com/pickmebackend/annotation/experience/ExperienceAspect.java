package com.pickmebackend.annotation.experience;

import com.pickmebackend.annotation.account.CurrentUser;
import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Experience;
import com.pickmebackend.domain.dto.experience.ExperienceRequestDto;
import com.pickmebackend.repository.ExperienceRepository;
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

import static com.pickmebackend.error.ErrorMessage.EXPERIENCE_NOT_FOUND;
import static com.pickmebackend.error.ErrorMessage.UNAUTHORIZED_USER;

@RequiredArgsConstructor
@Component
@Aspect
public class ExperienceAspect {

    private final ExperienceRepository experienceRepository;

    private final ErrorsFormatter errorsFormatter;

    @Pointcut("@annotation(ExperienceValidation)")
    public void experienceValidation() {}

    @Around("experienceValidation() && args(experienceId, experienceRequestDto, currentUser)")
    public Object updateExperience(ProceedingJoinPoint joinPoint, Long experienceId,
                                   ExperienceRequestDto experienceRequestDto, Account currentUser) throws Throwable {
        Optional<Experience> experienceOptional = this.experienceRepository.findById(experienceId);
        if (!experienceOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(EXPERIENCE_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }

        Experience experience = experienceOptional.get();
        if (!experience.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZED_USER.getValue()), HttpStatus.BAD_REQUEST);
        }
        return joinPoint.proceed();
    }

    @Around("experienceValidation() && args(experienceId, currentUser)")
    public Object deleteExperience(ProceedingJoinPoint joinPoint, Long experienceId, Account currentUser) throws Throwable {
        Optional<Experience> experienceOptional = this.experienceRepository.findById(experienceId);
        if (!experienceOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(EXPERIENCE_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }

        Experience experience = experienceOptional.get();
        if (!experience.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZED_USER.getValue()), HttpStatus.BAD_REQUEST);
        }
        return joinPoint.proceed();
    }
}
