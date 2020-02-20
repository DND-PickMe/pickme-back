package com.pickmebackend.resource;

import com.pickmebackend.controller.SelfInterviewController;
import com.pickmebackend.domain.SelfInterview;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class SelfInterviewResource extends EntityModel<SelfInterview> {

    public SelfInterviewResource(SelfInterview selfInterview, Link... links) {
        super(selfInterview, links);
        add(linkTo(SelfInterviewController.class).slash(selfInterview.getId()).withSelfRel());
    }
}
