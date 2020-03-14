package com.pickmebackend.annotation;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.VerificationCode;
import com.pickmebackend.domain.dto.account.AccountInitialRequestDto;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.dto.verificationCode.SendCodeRequestDto;
import com.pickmebackend.domain.dto.verificationCode.VerifyCodeRequestDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.VerificationCodeRepository;
import com.pickmebackend.repository.account.AccountRepository;
import com.pickmebackend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.pickmebackend.error.ErrorMessage.*;

@Component
@Aspect
@RequiredArgsConstructor
public class AccountAspect {

    private final AccountRepository accountRepository;

    private final ErrorsFormatter errorsFormatter;

    private final AccountService accountService;

    private final VerificationCodeRepository verificationCodeRepository;

    @Pointcut("@annotation(com.pickmebackend.annotation.AccountValidation)")
    public void accountIsPresent() {}

    @Around("accountIsPresent() && args(accountDto, errors)")
    public Object saveAccount(ProceedingJoinPoint joinPoint, AccountInitialRequestDto accountDto, Errors errors) throws Throwable {
        if (errors.hasErrors()) {
            return errorsFormatter.badRequest(errors);
        }
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatErrors(errors));
        }
        Optional<VerificationCode> verificationCodeOptional = this.verificationCodeRepository.findByEmail(accountDto.getEmail());
        if (verificationCodeOptional.isPresent())   {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(UNVERIFIED_USER.getValue()));
        }
        if(accountService.isDuplicatedAccount(accountDto.getEmail()))  {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(DUPLICATED_USER.getValue()));
        }
        return joinPoint.proceed();
    }

    @Around("accountIsPresent() && args(accountId, accountDto, errors, currentUser)")
    public Object updateAccount(ProceedingJoinPoint joinPoint, Long accountId, AccountRequestDto accountDto, Errors errors,
                                Account currentUser) throws Throwable {
        if (errors.hasErrors()) {
            return errorsFormatter.badRequest(errors);
        }
        if (!accountRepository.findById(accountId).isPresent()) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        if (!accountId.equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("accountIsPresent() && args(accountId, currentUser)")
    public Object deleteAccount(ProceedingJoinPoint joinPoint, Long accountId, Account currentUser) throws Throwable {
        if (!accountRepository.findById(accountId).isPresent()) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        if (!accountId.equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("accountIsPresent() && args(currentUser)")
    public Object loadProfile(ProceedingJoinPoint joinPoint, Account currentUser) throws Throwable {
        if (currentUser == null) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        Optional<Account> accountOptional = accountRepository.findById(currentUser.getId());
        if (!accountOptional.isPresent()) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("accountIsPresent() && args(accountId, currentUser, request, response)")
    public Object loadAccount(ProceedingJoinPoint joinPoint, Long accountId, Account currentUser,
                              HttpServletRequest request, HttpServletResponse response) throws Throwable {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent() || currentUser == null) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("accountIsPresent() && args(sendCodeRequestDto, errors)")
    public Object sendVerificationCode(ProceedingJoinPoint joinPoint, SendCodeRequestDto sendCodeRequestDto, Errors errors) throws Throwable {
        if(errors.hasErrors())  {
            return errorsFormatter.badRequest(errors);
        }
        if(accountService.isDuplicatedAccount(sendCodeRequestDto.getEmail()))  {
            return errorsFormatter.badRequest(DUPLICATED_USER.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("accountIsPresent() && args(verifyCodeRequestDto, errors)")
    public Object verifyCode(ProceedingJoinPoint joinPoint, VerifyCodeRequestDto verifyCodeRequestDto, Errors errors) throws Throwable {
        if(errors.hasErrors()) {
            return errorsFormatter.badRequest(errors);
        }
        if(accountService.isDuplicatedAccount(verifyCodeRequestDto.getEmail()))  {
            return errorsFormatter.badRequest(DUPLICATED_USER.getValue());
        }
        return joinPoint.proceed();
    }
}
