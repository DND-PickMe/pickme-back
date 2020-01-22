package com.pickmebackend.controller;

import com.pickmebackend.domain.dto.ExperienceDto;
import com.pickmebackend.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/experiences", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    ResponseEntity<?> saveExperience(@RequestBody ExperienceDto experienceDto) {
        return experienceService.saveExperience(experienceDto);
    }

    @PutMapping(value = "/{experienceId}")
    ResponseEntity<?> updateExperience(@PathVariable Long experienceId, @RequestBody ExperienceDto experienceDto) {
        return experienceService.updateExperience(experienceId, experienceDto);
    }

    @DeleteMapping(value = "/{experienceId}")
    ResponseEntity<?> deleteExperience(@PathVariable Long experienceId) {
        return experienceService.deleteExperience(experienceId);
    }
}
