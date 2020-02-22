package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.SelfInterview;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewRequestDto;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.SelfInterviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import static com.pickmebackend.error.ErrorMessageConstant.SELFINTERVIEWNOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;

@Service
@RequiredArgsConstructor
public class SelfInterviewService {

    private final SelfInterviewRepository selfInterviewRepository;

    private final ModelMapper modelMapper;

    public SelfInterviewResponseDto saveSelfInterview(SelfInterviewRequestDto selfInterviewRequestDto, Account account) {
        SelfInterview selfInterview = modelMapper.map(selfInterviewRequestDto, SelfInterview.class);
        selfInterview.mapAccount(account);
        SelfInterview savedSelfInterview = this.selfInterviewRepository.save(selfInterview);
        SelfInterviewResponseDto selfInterviewResponseDto = modelMapper.map(savedSelfInterview, SelfInterviewResponseDto.class);

        return selfInterviewResponseDto;
    }

    public SelfInterviewResponseDto updateSelfInterview(SelfInterview selfInterview, SelfInterviewRequestDto selfInterviewRequestDto, Account currentUser) {
        modelMapper.map(selfInterviewRequestDto, selfInterview);
        SelfInterview modifiedSelfInterview = this.selfInterviewRepository.save(selfInterview);
        SelfInterviewResponseDto selfInterviewResponseDto = modelMapper.map(modifiedSelfInterview, SelfInterviewResponseDto.class);

        return selfInterviewResponseDto;
    }

    public ResponseEntity<?> deleteSelfInterview(Long selfInterviewId, Account currentUser) {
        Optional<SelfInterview> selfInterviewOptional = this.selfInterviewRepository.findById(selfInterviewId);
        if (!selfInterviewOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(SELFINTERVIEWNOTFOUND));
        }

        SelfInterview selfInterview = selfInterviewOptional.get();
        if (!selfInterview.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        this.selfInterviewRepository.delete(selfInterview);
        return ResponseEntity.ok().build();
    }
}
