package com.pickmebackend.controller;

import com.pickmebackend.annotation.account.CurrentUser;
import com.pickmebackend.annotation.selfInterview.SelfInterviewValidation;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.SelfInterview;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewRequestDto;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewResponseDto;
import com.pickmebackend.repository.SelfInterviewRepository;
import com.pickmebackend.resource.HateoasFormatter;
import com.pickmebackend.resource.SelfInterviewResource;
import com.pickmebackend.service.SelfInterviewService;
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
@RequestMapping(value = "/api/selfInterviews", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class SelfInterviewController {

    private final SelfInterviewService selfInterviewService;

    private final SelfInterviewRepository selfInterviewRepository;

    private final HateoasFormatter hateoasFormatter;

    @PostMapping
    public ResponseEntity<?> saveSelfInterview(@RequestBody SelfInterviewRequestDto selfInterviewRequestDto, @CurrentUser Account currentUser) {
        SelfInterviewResponseDto selfInterviewResponseDto = selfInterviewService.saveSelfInterview(selfInterviewRequestDto, currentUser);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(SelfInterviewController.class).slash(selfInterviewResponseDto.getId());
        SelfInterviewResource selfInterviewResource = new SelfInterviewResource(selfInterviewResponseDto);
        selfInterviewResource.add(selfLinkBuilder.withRel(UPDATE_SELF_INTERVIEW.getValue()));
        selfInterviewResource.add(selfLinkBuilder.withRel(DELETE_SELF_INTERVIEW.getValue()));
        hateoasFormatter.addProfileRel(selfInterviewResource, "resources-selfInterviews-create");

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(selfInterviewResource);
    }

    @PutMapping("/{selfInterviewId}")
    @SelfInterviewValidation
    public ResponseEntity<?> updateSelfInterview(@PathVariable Long selfInterviewId, @RequestBody SelfInterviewRequestDto selfInterviewRequestDto, @CurrentUser Account currentUser) {
        Optional<SelfInterview> selfInterviewOptional = this.selfInterviewRepository.findById(selfInterviewId);
        SelfInterviewResponseDto modifiedSelfInterviewResponseDto =
                selfInterviewService.updateSelfInterview(selfInterviewOptional.get(), selfInterviewRequestDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(SelfInterviewController.class).slash(modifiedSelfInterviewResponseDto.getId());
        SelfInterviewResource selfInterviewResource = new SelfInterviewResource(modifiedSelfInterviewResponseDto);
        selfInterviewResource.add(linkTo(SelfInterviewController.class).withRel(CREATE_SELF_INTERVIEW.getValue()));
        selfInterviewResource.add(selfLinkBuilder.withRel(DELETE_SELF_INTERVIEW.getValue()));
        hateoasFormatter.addProfileRel(selfInterviewResource, "resources-selfInterviews-update");

        return new ResponseEntity<>(selfInterviewResource, HttpStatus.OK);
    }

    @DeleteMapping("/{selfInterviewId}")
    @SelfInterviewValidation
    public ResponseEntity<?> deleteSelfInterview(@PathVariable Long selfInterviewId, @CurrentUser Account currentUser) {
        Optional<SelfInterview> selfInterviewOptional = this.selfInterviewRepository.findById(selfInterviewId);
        SelfInterviewResponseDto selfInterviewResponseDto = selfInterviewService.deleteSelfInterview(selfInterviewOptional.get());
        SelfInterviewResource selfInterviewResource = new SelfInterviewResource(selfInterviewResponseDto);
        selfInterviewResource.add(linkTo(SelfInterviewController.class).withRel(CREATE_SELF_INTERVIEW.getValue()));
        hateoasFormatter.addProfileRel(selfInterviewResource, "resources-selfInterviews-delete");

        return new ResponseEntity<>(selfInterviewResource, HttpStatus.OK);
    }
}
