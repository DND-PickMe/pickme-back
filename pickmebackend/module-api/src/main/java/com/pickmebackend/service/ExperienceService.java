package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Experience;
import com.pickmebackend.domain.dto.experience.ExperienceRequestDto;
import com.pickmebackend.domain.dto.experience.ExperienceResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import static com.pickmebackend.error.ErrorMessageConstant.EXPERIENCENOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;

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

    public ExperienceResponseDto updateExperience(Experience experience, ExperienceRequestDto experienceRequestDto, Account currentUser) {
        modelMapper.map(experienceRequestDto, experience);
        Experience modifiedExperience = this.experienceRepository.save(experience);
        ExperienceResponseDto experienceResponseDto = modelMapper.map(modifiedExperience, ExperienceResponseDto.class);

        return experienceResponseDto;
    }

    public ResponseEntity<?> deleteExperience(Long experienceId, Account currentUser) {
        Optional<Experience> experienceOptional = this.experienceRepository.findById(experienceId);
        if (!experienceOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(EXPERIENCENOTFOUND), HttpStatus.BAD_REQUEST);
        }

        Experience experience = experienceOptional.get();
        if (!experience.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        this.experienceRepository.delete(experience);
        return ResponseEntity.ok().build();
    }
}
