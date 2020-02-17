package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewRequestDto;
import com.pickmebackend.service.SelfInterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/selfInterviews", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class SelfInterviewController {

    private final SelfInterviewService selfInterviewService;

    @PostMapping
    public ResponseEntity<?> saveSelfInterview(@RequestBody SelfInterviewRequestDto selfInterviewRequestDto, @CurrentUser Account currentUser) {
        return selfInterviewService.saveSelfInterview(selfInterviewRequestDto, currentUser);
    }

    @PutMapping("/{selfInterviewId}")
    ResponseEntity<?> updateSelfInterview(@PathVariable Long selfInterviewId, @RequestBody SelfInterviewRequestDto selfInterviewRequestDto, @CurrentUser Account currentUser) {
        return selfInterviewService.updateSelfInterview(selfInterviewId, selfInterviewRequestDto, currentUser);
    }

    @DeleteMapping("/{selfInterviewId}")
    ResponseEntity<?> deleteSelfInterview(@PathVariable Long selfInterviewId, @CurrentUser Account currentUser) {
        return selfInterviewService.deleteSelfInterview(selfInterviewId, currentUser);
    }
}
