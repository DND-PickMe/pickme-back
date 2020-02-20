package com.pickmebackend.resource;

import com.pickmebackend.controller.PrizeController;
import com.pickmebackend.domain.Prize;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class PrizeResource extends EntityModel<Prize> {

    public PrizeResource(Prize prize, Link... links) {
        super(prize, links);
        add(linkTo(PrizeController.class).slash(prize.getId()).withSelfRel());
    }
}
