package com.pickmebackend.resource;

import com.pickmebackend.controller.ProjectController;
import com.pickmebackend.domain.dto.project.ProjectResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ProjectResource extends EntityModel<ProjectResponseDto> {

    public ProjectResource(ProjectResponseDto projectResponseDto, Link... links) {
        super(projectResponseDto, links);
        add(linkTo(ProjectController.class).slash(projectResponseDto.getId()).withSelfRel());
    }
}
