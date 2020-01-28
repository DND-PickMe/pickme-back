package com.pickmebackend.service;

import com.pickmebackend.domain.Experience;
import com.pickmebackend.domain.dto.ExperienceDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import static com.pickmebackend.error.ErrorMessageConstant.EXPERIENCENOTFOUND;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> saveExperience(ExperienceDto experienceDto) {
        Experience experience = modelMapper.map(experienceDto, Experience.class);
        return new ResponseEntity<>(experienceRepository.save(experience), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateExperience(Long experienceId, ExperienceDto experienceDto) {
        Optional<Experience> experienceOptional = experienceRepository.findById(experienceId);
        if (!experienceOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(EXPERIENCENOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Experience experience = experienceOptional.get();
        modelMapper.map(experienceDto, experience);
        return new ResponseEntity<>(experienceRepository.save(experience), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteExperience(Long experienceId) {
        Optional<Experience> experienceOptional = experienceRepository.findById(experienceId);
        if (!experienceOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(EXPERIENCENOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Experience experience = experienceOptional.get();
        experienceRepository.delete(experience);
        return ResponseEntity.ok().build();
    }
}
