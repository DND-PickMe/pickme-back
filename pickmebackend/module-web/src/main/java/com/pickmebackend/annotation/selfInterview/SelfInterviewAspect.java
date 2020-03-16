package com.pickmebackend.annotation.selfInterview;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.SelfInterview;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewRequestDto;
import com.pickmebackend.repository.SelfInterviewRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessage.SELF_INTERVIEW_NOT_FOUND;
import static com.pickmebackend.error.ErrorMessage.UNAUTHORIZED_USER;

@RequiredArgsConstructor
@Component
@Aspect
public class SelfInterviewAspect {

    private final SelfInterviewRepository selfInterviewRepository;

    private final ErrorsFormatter errorsFormatter;

    @Pointcut("@annotation(SelfInterviewValidation)")
    public void selfInterviewValidation() {}

    @Around("selfInterviewValidation() && args(selfInterviewId, selfInterviewRequestDto, currentUser)")
    public Object updateSelfInterview(ProceedingJoinPoint joinPoint, Long selfInterviewId,
                                      SelfInterviewRequestDto selfInterviewRequestDto, Account currentUser) throws Throwable {
        Optional<SelfInterview> selfInterviewOptional = this.selfInterviewRepository.findById(selfInterviewId);
        if (!selfInterviewOptional.isPresent()) {
            return errorsFormatter.badRequest(SELF_INTERVIEW_NOT_FOUND.getValue());
        }

        SelfInterview selfInterview = selfInterviewOptional.get();
        if (!selfInterview.getAccount().getId().equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        return joinPoint.proceed();
    }

    @Around("selfInterviewValidation() && args(selfInterviewId, currentUser)")
    public Object deleteSelfInterview(ProceedingJoinPoint joinPoint, Long selfInterviewId, Account currentUser) throws Throwable {
        Optional<SelfInterview> selfInterviewOptional = this.selfInterviewRepository.findById(selfInterviewId);
        if (!selfInterviewOptional.isPresent()) {
            return errorsFormatter.badRequest(SELF_INTERVIEW_NOT_FOUND.getValue());
        }

        SelfInterview selfInterview = selfInterviewOptional.get();
        if (!selfInterview.getAccount().getId().equals(currentUser.getId())) {
            return errorsFormatter.badRequest(UNAUTHORIZED_USER.getValue());
        }
        return joinPoint.proceed();
    }
}
