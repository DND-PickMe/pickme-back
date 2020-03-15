package com.pickmebackend.annotation.login;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.dto.login.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static com.pickmebackend.error.ErrorMessage.INVALID_LOGIN;

@RequiredArgsConstructor
@Component
@Aspect
public class LoginAspect {

    private final AuthenticationManager authenticationManager;

    private final ErrorsFormatter errorsFormatter;

    @Pointcut("@annotation(LoginValidation)")
    public void loginValidation() {}

    @Around("loginValidation() && args(loginRequestDto, errors)")
    public Object login(ProceedingJoinPoint joinPoint, LoginRequestDto loginRequestDto, Errors errors) throws Throwable {
        if (errors.hasErrors()) {
            return errorsFormatter.badRequest(errors);
        }
        if(!authenticate(loginRequestDto.getEmail(), loginRequestDto.getPassword()))  {
            return errorsFormatter.badRequest(INVALID_LOGIN.getValue());
        }
        return joinPoint.proceed();
    }

    private boolean authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return true;
        }
        catch (DisabledException | BadCredentialsException e) {
            return false;
        }
    }
}
