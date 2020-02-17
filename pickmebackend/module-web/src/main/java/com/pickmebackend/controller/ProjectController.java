package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.project.ProjectRequestDto;
import com.pickmebackend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/projects", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    ResponseEntity<?> saveProject(@RequestBody ProjectRequestDto projectRequestDto, @CurrentUser Account currentUser) {
        return projectService.saveProject(projectRequestDto, currentUser);
    }

    @PutMapping("/{projectId}")
    ResponseEntity<?> updateProject(@PathVariable Long projectId, @RequestBody ProjectRequestDto projectRequestDto, @CurrentUser Account currentUser) {
        return projectService.updateProject(projectId, projectRequestDto, currentUser);
    }

    @DeleteMapping("/{projectId}")
    ResponseEntity<?> deleteProject(@PathVariable Long projectId, @CurrentUser Account currentUser) {
        return projectService.deleteProject(projectId, currentUser);
    }
}
