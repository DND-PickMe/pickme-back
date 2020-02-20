package com.pickmebackend.resource;

import com.pickmebackend.controller.ProjectController;
import com.pickmebackend.domain.Project;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ProjectResource extends EntityModel<Project> {

    public ProjectResource(Project project, Link... links) {
        super(project, links);
        add(linkTo(ProjectController.class).slash(project.getId()).withSelfRel());
    }
}
