package com.pickmebackend.controller;

import com.pickmebackend.annotation.account.CurrentUser;
import com.pickmebackend.annotation.project.ProjectValidation;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Project;
import com.pickmebackend.domain.dto.project.ProjectRequestDto;
import com.pickmebackend.domain.dto.project.ProjectResponseDto;
import com.pickmebackend.repository.ProjectRepository;
import com.pickmebackend.resource.HateoasFormatter;
import com.pickmebackend.resource.ProjectResource;
import com.pickmebackend.service.ProjectService;
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
@RequestMapping(value = "/api/projects", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    private final ProjectRepository projectRepository;

    private final HateoasFormatter hateoasFormatter;

    @PostMapping
    public ResponseEntity<?> saveProject(@RequestBody ProjectRequestDto projectRequestDto, @CurrentUser Account currentUser) {
        ProjectResponseDto projectResponseDto = projectService.saveProject(projectRequestDto, currentUser);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ProjectController.class).slash(projectResponseDto.getId());
        ProjectResource projectResource = new ProjectResource(projectResponseDto);
        projectResource.add(selfLinkBuilder.withRel(UPDATE_PROJECT.getValue()));
        projectResource.add(selfLinkBuilder.withRel(DELETE_PROJECT.getValue()));
        hateoasFormatter.addProfileRel(projectResource, "resources-projects-create");

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(projectResource);
    }

    @PutMapping("/{projectId}")
    @ProjectValidation
    public ResponseEntity<?> updateProject(@PathVariable Long projectId, @RequestBody ProjectRequestDto projectRequestDto, @CurrentUser Account currentUser) {
        Optional<Project> projectOptional = this.projectRepository.findById(projectId);
        ProjectResponseDto modifiedProjectResponseDto = projectService.updateProject(projectOptional.get(), projectRequestDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(ProjectController.class).slash(modifiedProjectResponseDto.getId());
        ProjectResource projectResource = new ProjectResource(modifiedProjectResponseDto);
        projectResource.add(linkTo(ProjectController.class).withRel(CREATE_PROJECT.getValue()));
        projectResource.add(selfLinkBuilder.withRel(DELETE_PROJECT.getValue()));
        hateoasFormatter.addProfileRel(projectResource, "resources-projects-update");

        return new ResponseEntity<>(projectResource, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    @ProjectValidation
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId, @CurrentUser Account currentUser) {
        Optional<Project> projectOptional = this.projectRepository.findById(projectId);
        ProjectResponseDto projectResponseDto = projectService.deleteProject(projectOptional.get());
        ProjectResource projectResource = new ProjectResource(projectResponseDto);
        projectResource.add(linkTo(ProjectController.class).withRel(CREATE_PROJECT.getValue()));
        hateoasFormatter.addProfileRel(projectResource, "resources-projects-delete");

        return new ResponseEntity<>(projectResource, HttpStatus.OK);
    }
}
