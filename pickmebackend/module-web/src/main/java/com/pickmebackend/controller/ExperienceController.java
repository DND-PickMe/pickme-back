package com.pickmebackend.controller;

import com.pickmebackend.annotation.account.CurrentUser;
import com.pickmebackend.annotation.experience.ExperienceValidation;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Experience;
import com.pickmebackend.domain.dto.experience.ExperienceRequestDto;
import com.pickmebackend.domain.dto.experience.ExperienceResponseDto;
import com.pickmebackend.repository.ExperienceRepository;
import com.pickmebackend.resource.ExperienceResource;
import com.pickmebackend.resource.HateoasFormatter;
import com.pickmebackend.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.pickmebackend.properties.RestDocsConstants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/experiences", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    private final ExperienceRepository experienceRepository;

    private final HateoasFormatter hateoasFormatter;

    @PostMapping
    public ResponseEntity<?> saveExperience(@RequestBody ExperienceRequestDto experienceRequestDto, @CurrentUser Account currentUser) {
        ExperienceResponseDto experienceResponseDto =  experienceService.saveExperience(experienceRequestDto, currentUser);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ExperienceController.class).slash(experienceResponseDto.getId());
        ExperienceResource experienceResource = new ExperienceResource(experienceResponseDto);
        experienceResource.add(selfLinkBuilder.withRel(UPDATE_EXPERIENCE.getValue()));
        experienceResource.add(selfLinkBuilder.withRel(DELETE_EXPERIENCE.getValue()));
        hateoasFormatter.addProfileRel(experienceResource, "resources-experiences-create");

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(experienceResource);
    }

    @PutMapping(value = "/{experienceId}")
    @ExperienceValidation
    public ResponseEntity<?> updateExperience(@PathVariable Long experienceId, @RequestBody ExperienceRequestDto experienceRequestDto, @CurrentUser Account currentUser) {
        Optional<Experience> experienceOptional = this.experienceRepository.findById(experienceId);
        ExperienceResponseDto modifiedExperienceResponseDto = experienceService.updateExperience(experienceOptional.get(), experienceRequestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ExperienceController.class).slash(modifiedExperienceResponseDto.getId());
        ExperienceResource experienceResource = new ExperienceResource(modifiedExperienceResponseDto);
        experienceResource.add(linkTo(ExperienceController.class).withRel(CREATE_EXPERIENCE.getValue()));
        experienceResource.add(selfLinkBuilder.withRel(DELETE_EXPERIENCE.getValue()));
        hateoasFormatter.addProfileRel(experienceResource, "resources-experiences-update");

        return new ResponseEntity<>(experienceResource, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{experienceId}")
    @ExperienceValidation
    public ResponseEntity<?> deleteExperience(@PathVariable Long experienceId, @CurrentUser Account currentUser) {
        Optional<Experience> experienceOptional = this.experienceRepository.findById(experienceId);
        ExperienceResponseDto experienceResponseDto = experienceService.deleteExperience(experienceOptional.get());
        ExperienceResource experienceResource = new ExperienceResource(experienceResponseDto);
        experienceResource.add(linkTo(ExperienceController.class).withRel(CREATE_EXPERIENCE.getValue()));
        hateoasFormatter.addProfileRel(experienceResource, "resources-experiences-delete");

        return new ResponseEntity<>(experienceResource, HttpStatus.OK);
    }
}
