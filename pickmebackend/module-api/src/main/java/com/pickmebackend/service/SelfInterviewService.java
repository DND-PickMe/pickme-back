package com.pickmebackend.service;

import com.pickmebackend.domain.SelfInterview;
import com.pickmebackend.domain.dto.SelfInterviewDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.error.ErrorMessageConstant;
import com.pickmebackend.repository.SelfInterviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SelfInterviewService {

    private final SelfInterviewRepository selfInterviewRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> saveSelfInterview(SelfInterviewDto selfInterviewDto) {
        SelfInterview selfInterview = modelMapper.map(selfInterviewDto, SelfInterview.class);
        return new ResponseEntity<>(selfInterviewRepository.save(selfInterview), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateSelfInterview(Long selfInterviewId, SelfInterviewDto selfInterviewDto) {
        Optional<SelfInterview> selfInterviewOptional = selfInterviewRepository.findById(selfInterviewId);
        if (!selfInterviewOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(ErrorMessageConstant.SELFINTERVIEWNOTFOUND));
        }
        SelfInterview selfInterview = selfInterviewOptional.get();
        modelMapper.map(selfInterviewDto, selfInterview);
        return new ResponseEntity<>(selfInterviewRepository.save(selfInterview), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteSelfInterview(Long selfInterviewId) {
        Optional<SelfInterview> selfInterviewOptional = selfInterviewRepository.findById(selfInterviewId);
        if (!selfInterviewOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(ErrorMessageConstant.SELFINTERVIEWNOTFOUND));
        }
        SelfInterview selfInterview = selfInterviewOptional.get();
        selfInterviewRepository.delete(selfInterview);
        return ResponseEntity.ok().build();
    }
}
