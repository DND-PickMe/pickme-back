package com.pickmebackend.annotation.prize;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Prize;
import com.pickmebackend.domain.dto.prize.PrizeRequestDto;
import com.pickmebackend.repository.PrizeRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessage.PRIZE_NOT_FOUND;
import static com.pickmebackend.error.ErrorMessage.UNAUTHORIZED_USER;

@RequiredArgsConstructor
@Component
@Aspect
public class PrizeAspect {

    private final PrizeRepository prizeRepository;

    private final ErrorsFormatter errorsFormatter;

    @Pointcut("@annotation(PrizeValidation)")
    public void prizeValidation() {}

    @Around("prizeValidation() && args(prizeId, prizeRequestDto, currentUser)")
    public Object updatePrize(ProceedingJoinPoint joinPoint, Long prizeId,
                              PrizeRequestDto prizeRequestDto, Account currentUser) throws Throwable {
        Optional<Prize> prizeOptional = this.prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(PRIZE_NOT_FOUND.getValue()));
        }

        Prize prize = prizeOptional.get();
        if (!prize.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZED_USER.getValue()), HttpStatus.BAD_REQUEST);
        }
        return joinPoint.proceed();
    }

    @Around("prizeValidation() && args(prizeId, currentUser)")
    public Object deletePrize(ProceedingJoinPoint joinPoint, Long prizeId, Account currentUser) throws Throwable {
        Optional<Prize> prizeOptional = this.prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(PRIZE_NOT_FOUND.getValue()));
        }

        Prize prize = prizeOptional.get();
        if (!prize.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZED_USER.getValue()), HttpStatus.BAD_REQUEST);
        }
        return joinPoint.proceed();
    }
}
