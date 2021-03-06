package com.pickmebackend.controller;

import com.pickmebackend.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/technologies")
    public ResponseEntity<?> getTechnology() {
        return skillService.getTechnology();
    }
}
