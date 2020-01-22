package com.pickmebackend.controller;

import com.pickmebackend.domain.dto.SelfInterviewDto;
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
    ResponseEntity<?> saveSelfInterview(@RequestBody SelfInterviewDto selfInterviewDto) {
        return selfInterviewService.saveSelfInterview(selfInterviewDto);
    }

    @PutMapping("/{selfInterviewId}")
    ResponseEntity<?> updateSelfInterview(@PathVariable Long selfInterviewId, @RequestBody SelfInterviewDto selfInterviewDto) {
        return selfInterviewService.updateSelfInterview(selfInterviewId, selfInterviewDto);
    }

    @DeleteMapping("/{selfInterviewId}")
    ResponseEntity<?> deleteSelfInterview(@PathVariable Long selfInterviewId) {
        return selfInterviewService.deleteSelfInterview(selfInterviewId);
    }
}
