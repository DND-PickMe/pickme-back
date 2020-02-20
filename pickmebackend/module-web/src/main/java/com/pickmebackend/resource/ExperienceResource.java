package com.pickmebackend.resource;

import com.pickmebackend.controller.ExperienceController;
import com.pickmebackend.domain.Experience;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ExperienceResource extends EntityModel<Experience> {

    public ExperienceResource(Experience experience, Link... links) {
        super(experience, links);
        add(linkTo(ExperienceController.class).slash(experience.getId()).withSelfRel());
    }

}
