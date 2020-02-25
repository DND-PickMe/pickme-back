package com.pickmebackend.resource;

import com.pickmebackend.controller.ExperienceController;
import com.pickmebackend.domain.dto.experience.ExperienceResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ExperienceResource extends EntityModel<ExperienceResponseDto> {

    public ExperienceResource(ExperienceResponseDto experienceResponseDto, Link... links) {
        super(experienceResponseDto, links);
        add(linkTo(ExperienceController.class).slash(experienceResponseDto.getId()).withSelfRel());
    }

}
