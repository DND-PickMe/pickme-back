package com.pickmebackend.service;

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

@Service
@RequiredArgsConstructor
public class PrizeService {

    private final PrizeRepository prizeRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> saveSelfInterview(PrizeDto prizeDto) {
        Prize prize = modelMapper.map(prizeDto, Prize.class);
        return new ResponseEntity<>(prizeRepository.save(prize), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateSelfInterview(Long prizeId, PrizeDto prizeDto) {
        Optional<Prize> prizeOptional = prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PRIZENOTFOUND));
        }
        Prize prize = prizeOptional.get();
        modelMapper.map(prizeDto, prize);
        return new ResponseEntity<>(prizeRepository.save(prize), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteSelfInterview(Long prizeId) {
        Optional<Prize> prizeOptional = prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PRIZENOTFOUND));
        }
        Prize prize = prizeOptional.get();
        prizeRepository.delete(prize);
        return ResponseEntity.ok().build();
    }
}
