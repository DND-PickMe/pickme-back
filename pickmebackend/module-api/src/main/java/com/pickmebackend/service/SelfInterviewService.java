package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.SelfInterview;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewRequestDto;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewResponseDto;
import com.pickmebackend.repository.SelfInterviewRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelfInterviewService {

    private final SelfInterviewRepository selfInterviewRepository;

    private final ModelMapper modelMapper;

    public SelfInterviewResponseDto saveSelfInterview(SelfInterviewRequestDto selfInterviewRequestDto, Account account) {
        SelfInterview selfInterview = modelMapper.map(selfInterviewRequestDto, SelfInterview.class);
        selfInterview.mapAccount(account);
        SelfInterview savedSelfInterview = this.selfInterviewRepository.save(selfInterview);
        return modelMapper.map(savedSelfInterview, SelfInterviewResponseDto.class);
    }

    public SelfInterviewResponseDto updateSelfInterview(SelfInterview selfInterview, SelfInterviewRequestDto selfInterviewRequestDto) {
        modelMapper.map(selfInterviewRequestDto, selfInterview);
        SelfInterview modifiedSelfInterview = this.selfInterviewRepository.save(selfInterview);
        return modelMapper.map(modifiedSelfInterview, SelfInterviewResponseDto.class);
    }

    public SelfInterviewResponseDto deleteSelfInterview(SelfInterview selfInterview) {
        this.selfInterviewRepository.delete(selfInterview);
        selfInterview.getAccount().getSelfInterviews().remove(selfInterview);
        return modelMapper.map(selfInterview, SelfInterviewResponseDto.class);
    }
}
