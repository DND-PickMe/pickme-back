package com.pickmebackend.resource;

import com.pickmebackend.controller.SelfInterviewController;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class SelfInterviewResource extends EntityModel<SelfInterviewResponseDto> {

    public SelfInterviewResource(SelfInterviewResponseDto selfInterviewResponseDto, Link... links) {
        super(selfInterviewResponseDto, links);
        add(linkTo(SelfInterviewController.class).slash(selfInterviewResponseDto.getId()).withSelfRel());
    }
}
