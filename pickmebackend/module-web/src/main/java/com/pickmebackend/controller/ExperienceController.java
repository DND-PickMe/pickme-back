package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.experience.ExperienceRequestDto;
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
    ResponseEntity<?> saveExperience(@RequestBody ExperienceRequestDto experienceRequestDto, @CurrentUser Account currentUser) {
        return experienceService.saveExperience(experienceRequestDto, currentUser);
    }

    @PutMapping(value = "/{experienceId}")
    ResponseEntity<?> updateExperience(@PathVariable Long experienceId, @RequestBody ExperienceRequestDto experienceRequestDto, @CurrentUser Account currentUser) {
        return experienceService.updateExperience(experienceId, experienceRequestDto, currentUser);
    }

    @DeleteMapping(value = "/{experienceId}")
    ResponseEntity<?> deleteExperience(@PathVariable Long experienceId, @CurrentUser Account currentUser) {
        return experienceService.deleteExperience(experienceId, currentUser);
    }
}
