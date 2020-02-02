package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Prize;
import com.pickmebackend.domain.dto.PrizeDto;
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

    public ResponseEntity<?> savePrize(PrizeDto prizeDto, Account account) {
        Prize prize = modelMapper.map(prizeDto, Prize.class);

        prize.mapAccount(account);
        Prize save = prizeRepository.save(prize);
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updatePrize(Long prizeId, PrizeDto prizeDto, Account currentUser) {
        Optional<Prize> prizeOptional = prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PRIZENOTFOUND));
        }

        Prize prize = prizeOptional.get();
        if (!prize.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        modelMapper.map(prizeDto, prize);
        return new ResponseEntity<>(prizeRepository.save(prize), HttpStatus.OK);
    }

    public ResponseEntity<?> deletePrize(Long prizeId, Account currentUser) {
        Optional<Prize> prizeOptional = prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PRIZENOTFOUND));
        }

        Prize prize = prizeOptional.get();
        if (!prize.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        prizeRepository.delete(prize);
        return ResponseEntity.ok().build();
    }
}
