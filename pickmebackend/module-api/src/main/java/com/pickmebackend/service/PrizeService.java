package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Prize;
import com.pickmebackend.domain.dto.prize.PrizeRequestDto;
import com.pickmebackend.domain.dto.prize.PrizeResponseDto;
import com.pickmebackend.repository.PrizeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrizeService {

    private final PrizeRepository prizeRepository;

    private final ModelMapper modelMapper;

    public PrizeResponseDto savePrize(PrizeRequestDto prizeRequestDto, Account account) {
        Prize prize = modelMapper.map(prizeRequestDto, Prize.class);
        prize.mapAccount(account);
        Prize savedPrize = this.prizeRepository.save(prize);
        return modelMapper.map(savedPrize, PrizeResponseDto.class);
    }

    public PrizeResponseDto updatePrize(Prize prize, PrizeRequestDto prizeRequestDto) {
        modelMapper.map(prizeRequestDto, prize);
        Prize modifiedPrize = prizeRepository.save(prize);
        return modelMapper.map(modifiedPrize, PrizeResponseDto.class);
    }

    public PrizeResponseDto deletePrize(Prize prize) {
        this.prizeRepository.delete(prize);
        prize.getAccount().getPrizes().remove(prize);
        return modelMapper.map(prize, PrizeResponseDto.class);
    }
}
