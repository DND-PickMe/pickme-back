package com.pickmebackend.annotation.enterprise;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.account.AccountRepository;
import com.pickmebackend.service.EnterpriseService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessage.*;

@RequiredArgsConstructor
@Component
@Aspect
public class EnterpriseAspect {

    private final EnterpriseService enterpriseService;

    private final ErrorsFormatter errorsFormatter;

    private final AccountRepository accountRepository;

    @Pointcut("@annotation(com.pickmebackend.annotation.enterprise.EnterpriseValidation)")
    public void enterpriseValidation() {}

    @Around("enterpriseValidation() && args(enterpriseId)")
    public Object loadEnterprise(ProceedingJoinPoint joinPoint, Long enterpriseId) throws Throwable {
        if(enterpriseService.isNonEnterprise(enterpriseId)) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        Optional<Account> accountOptional = this.accountRepository.findById(enterpriseId);
        if (!accountOptional.isPresent()) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("enterpriseValidation() && args(enterpriseRequestDto, errors)")
    public Object saveEnterprise(ProceedingJoinPoint joinPoint,
                                 EnterpriseRequestDto enterpriseRequestDto, Errors errors) throws Throwable {
        if(errors.hasErrors())  {
            return errorsFormatter.badRequest(errors);
        }
        if(enterpriseService.isDuplicatedEnterprise(enterpriseRequestDto)) {
            return errorsFormatter.badRequest(DUPLICATED_USER.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("enterpriseValidation() && args(enterpriseId, enterpriseRequestDto, errors, currentUser)")
    public Object updateEnterprise(ProceedingJoinPoint joinPoint, Long enterpriseId,
                                   EnterpriseRequestDto enterpriseRequestDto, Errors errors, Account currentUser) throws Throwable {
        if(errors.hasErrors())  {
            return errorsFormatter.badRequest(errors);
        }
        if(enterpriseService.isNonEnterprise(enterpriseId)) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        Optional<Account> accountOptional = this.accountRepository.findById(enterpriseId);
        if (!enterpriseId.equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        if (!accountOptional.isPresent()) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("enterpriseValidation() && args(enterpriseId, currentUser)")
    public Object deleteEnterprise(ProceedingJoinPoint joinPoint, Long enterpriseId, Account currentUser) throws Throwable {
        if (enterpriseService.isNonEnterprise(enterpriseId)) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        if (!enterpriseId.equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        Optional<Account> optionalAccount = this.accountRepository.findById(enterpriseId);
        if (!optionalAccount.isPresent()) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("execution(* com.pickmebackend.controller.EnterpriseController.sendSuggestion()) && args(enterpriseId, currentUser)")
    public Object sendSuggestion(ProceedingJoinPoint joinPoint, Long enterpriseId, Account currentUser) throws Throwable {
        if(currentUser == null) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        return joinPoint.proceed();
    }
}
