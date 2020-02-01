package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
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
    ResponseEntity<?> saveExperience(@RequestBody ExperienceDto experienceDto, @CurrentUser Account currentUser) {
        return experienceService.saveExperience(experienceDto, currentUser);
    }

    @PutMapping(value = "/{experienceId}")
    ResponseEntity<?> updateExperience(@PathVariable Long experienceId, @RequestBody ExperienceDto experienceDto, @CurrentUser Account currentUser) {
        return experienceService.updateExperience(experienceId, experienceDto, currentUser);
    }

    @DeleteMapping(value = "/{experienceId}")
    ResponseEntity<?> deleteExperience(@PathVariable Long experienceId, @CurrentUser Account currentUser) {
        return experienceService.deleteExperience(experienceId, currentUser);
    }
}
