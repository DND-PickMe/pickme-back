package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Project;
import com.pickmebackend.domain.dto.project.ProjectRequestDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.ProjectRepository;
import com.pickmebackend.resource.ProjectResource;
import com.pickmebackend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessageConstant.PROJECTNOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/projects", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    private final ProjectRepository projectRepository;

    @PostMapping
    ResponseEntity<?> saveProject(@RequestBody ProjectRequestDto projectRequestDto, @CurrentUser Account currentUser) {
        Project project = projectService.saveProject(projectRequestDto, currentUser);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ProjectController.class).slash(project.getId());
        ProjectResource projectResource = new ProjectResource(project);
        projectResource.add(selfLinkBuilder.withRel("update-project"));
        projectResource.add(selfLinkBuilder.withRel("delete-project"));

        return new ResponseEntity<>(projectResource, HttpStatus.CREATED);
    }

    @PutMapping("/{projectId}")
    ResponseEntity<?> updateProject(@PathVariable Long projectId, @RequestBody ProjectRequestDto projectRequestDto, @CurrentUser Account currentUser) {
        Optional<Project> projectOptional = this.projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PROJECTNOTFOUND));
        }

        Project project = projectOptional.get();
        if (!project.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }
        Project modifiedProject = projectService.updateProject(project, projectRequestDto, currentUser);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(ProjectController.class).slash(project.getId());
        ProjectResource projectResource = new ProjectResource(modifiedProject);
        projectResource.add(linkTo(ProjectController.class).withRel("create-project"));
        projectResource.add(selfLinkBuilder.withRel("delete-project"));

        return new ResponseEntity<>(projectResource, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    ResponseEntity<?> deleteProject(@PathVariable Long projectId, @CurrentUser Account currentUser) {
        return projectService.deleteProject(projectId, currentUser);
    }
}
