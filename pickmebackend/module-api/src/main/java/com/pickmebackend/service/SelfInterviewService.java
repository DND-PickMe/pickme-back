package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.SelfInterview;
import com.pickmebackend.domain.dto.SelfInterviewDto;
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

    public ResponseEntity<?> saveSelfInterview(SelfInterviewDto selfInterviewDto, Account account) {
        SelfInterview selfInterview = modelMapper.map(selfInterviewDto, SelfInterview.class);

        selfInterview.mapAccount(account);
        return new ResponseEntity<>(selfInterviewRepository.save(selfInterview), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateSelfInterview(Long selfInterviewId, SelfInterviewDto selfInterviewDto, Account currentUser) {
        Optional<SelfInterview> selfInterviewOptional = selfInterviewRepository.findById(selfInterviewId);
        if (!selfInterviewOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(SELFINTERVIEWNOTFOUND));
        }

        SelfInterview selfInterview = selfInterviewOptional.get();
        if (!selfInterview.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        modelMapper.map(selfInterviewDto, selfInterview);
        return new ResponseEntity<>(selfInterviewRepository.save(selfInterview), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteSelfInterview(Long selfInterviewId, Account currentUser) {
        Optional<SelfInterview> selfInterviewOptional = selfInterviewRepository.findById(selfInterviewId);
        if (!selfInterviewOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(SELFINTERVIEWNOTFOUND));
        }

        SelfInterview selfInterview = selfInterviewOptional.get();
        if (!selfInterview.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        selfInterviewRepository.delete(selfInterview);
        return ResponseEntity.ok().build();
    }
}
