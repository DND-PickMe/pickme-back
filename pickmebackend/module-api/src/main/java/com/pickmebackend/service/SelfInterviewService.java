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
import static com.pickmebackend.error.ErrorMessageConstant.*;

@Service
@RequiredArgsConstructor
public class SelfInterviewService {

    private final SelfInterviewRepository selfInterviewRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> saveSelfInterview(SelfInterviewRequestDto selfInterviewRequestDto, Account account) {
        SelfInterview selfInterview = modelMapper.map(selfInterviewRequestDto, SelfInterview.class);

        selfInterview.mapAccount(account);
        SelfInterview savedSelfInterview = this.selfInterviewRepository.save(selfInterview);
        SelfInterviewResponseDto selfInterviewResponseDto = modelMapper.map(savedSelfInterview, SelfInterviewResponseDto.class);

        return new ResponseEntity<>(selfInterviewResponseDto, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateSelfInterview(Long selfInterviewId, SelfInterviewRequestDto selfInterviewRequestDto, Account currentUser) {
        Optional<SelfInterview> selfInterviewOptional = this.selfInterviewRepository.findById(selfInterviewId);
        if (!selfInterviewOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(SELFINTERVIEWNOTFOUND));
        }

        SelfInterview selfInterview = selfInterviewOptional.get();
        if (!selfInterview.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        modelMapper.map(selfInterviewRequestDto, selfInterview);
        SelfInterview modifiedSelfInterview = this.selfInterviewRepository.save(selfInterview);
        SelfInterviewResponseDto selfInterviewResponseDto = modelMapper.map(modifiedSelfInterview, SelfInterviewResponseDto.class);

        return new ResponseEntity<>(selfInterviewResponseDto, HttpStatus.OK);
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
