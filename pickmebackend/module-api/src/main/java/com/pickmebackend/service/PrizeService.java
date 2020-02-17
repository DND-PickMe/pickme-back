package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Prize;
import com.pickmebackend.domain.dto.prize.PrizeRequestDto;
import com.pickmebackend.domain.dto.prize.PrizeResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.PrizeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import static com.pickmebackend.error.ErrorMessageConstant.PRIZENOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;

@Service
@RequiredArgsConstructor
public class PrizeService {

    private final PrizeRepository prizeRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> savePrize(PrizeRequestDto prizeRequestDto, Account account) {
        Prize prize = modelMapper.map(prizeRequestDto, Prize.class);

        prize.mapAccount(account);
        Prize savedPrize = this.prizeRepository.save(prize);
        PrizeResponseDto prizeResponseDto = modelMapper.map(savedPrize, PrizeResponseDto.class);

        return new ResponseEntity<>(prizeResponseDto, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updatePrize(Long prizeId, PrizeRequestDto prizeRequestDto, Account currentUser) {
        Optional<Prize> prizeOptional = this.prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PRIZENOTFOUND));
        }

        Prize prize = prizeOptional.get();
        if (!prize.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        modelMapper.map(prizeRequestDto, prize);
        Prize modifiedPrize = prizeRepository.save(prize);
        PrizeResponseDto prizeResponseDto = modelMapper.map(modifiedPrize, PrizeResponseDto.class);

        return new ResponseEntity<>(prizeResponseDto, HttpStatus.OK);
    }

    public ResponseEntity<?> deletePrize(Long prizeId, Account currentUser) {
        Optional<Prize> prizeOptional = this.prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PRIZENOTFOUND));
        }

        Prize prize = prizeOptional.get();
        if (!prize.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        this.prizeRepository.delete(prize);
        return ResponseEntity.ok().build();
    }
}
