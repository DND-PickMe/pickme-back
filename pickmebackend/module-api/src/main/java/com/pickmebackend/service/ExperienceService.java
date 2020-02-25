package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Experience;
import com.pickmebackend.domain.dto.experience.ExperienceRequestDto;
import com.pickmebackend.domain.dto.experience.ExperienceResponseDto;
import com.pickmebackend.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    private final ModelMapper modelMapper;

    public ExperienceResponseDto saveExperience(ExperienceRequestDto experienceRequestDto, Account currentUser) {
        Experience experience = modelMapper.map(experienceRequestDto, Experience.class);
        experience.mapAccount(currentUser);
        Experience savedExperience = this.experienceRepository.save(experience);
        ExperienceResponseDto experienceResponseDto = modelMapper.map(savedExperience, ExperienceResponseDto.class);

        return experienceResponseDto;
    }

    public ExperienceResponseDto updateExperience(Experience experience, ExperienceRequestDto experienceRequestDto) {
        modelMapper.map(experienceRequestDto, experience);
        Experience modifiedExperience = this.experienceRepository.save(experience);
        ExperienceResponseDto experienceResponseDto = modelMapper.map(modifiedExperience, ExperienceResponseDto.class);

        return experienceResponseDto;
    }

    public ExperienceResponseDto deleteExperience(Experience experience) {
        ExperienceResponseDto experienceResponseDto = modelMapper.map(experience, ExperienceResponseDto.class);
        this.experienceRepository.delete(experience);

        return experienceResponseDto;
    }
}
