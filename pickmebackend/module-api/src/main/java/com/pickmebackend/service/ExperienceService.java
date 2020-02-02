package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Experience;
import com.pickmebackend.domain.dto.ExperienceDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.error.ErrorMessageConstant;
import com.pickmebackend.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

import static com.pickmebackend.error.ErrorMessageConstant.*;
import static com.pickmebackend.error.ErrorMessageConstant.EXPERIENCENOTFOUND;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> saveExperience(ExperienceDto experienceDto, Account currentUser) {
        Experience experience = modelMapper.map(experienceDto, Experience.class);

        experience.mapAccount(currentUser);
        return new ResponseEntity<>(experienceRepository.save(experience), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateExperience(Long experienceId, ExperienceDto experienceDto, Account currentUser) {
        Optional<Experience> experienceOptional = experienceRepository.findById(experienceId);
        if (!experienceOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(EXPERIENCENOTFOUND), HttpStatus.BAD_REQUEST);
        }

        Experience experience = experienceOptional.get();
        if (!experience.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        modelMapper.map(experienceDto, experience);
        return new ResponseEntity<>(experienceRepository.save(experience), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteExperience(Long experienceId, Account currentUser) {
        Optional<Experience> experienceOptional = experienceRepository.findById(experienceId);
        if (!experienceOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(EXPERIENCENOTFOUND), HttpStatus.BAD_REQUEST);
        }

        Experience experience = experienceOptional.get();
        if (!experience.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        experienceRepository.delete(experience);
        return ResponseEntity.ok().build();
    }
}
