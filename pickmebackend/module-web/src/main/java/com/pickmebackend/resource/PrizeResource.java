package com.pickmebackend.resource;

import com.pickmebackend.controller.PrizeController;
import com.pickmebackend.domain.dto.prize.PrizeResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class PrizeResource extends EntityModel<PrizeResponseDto> {

    public PrizeResource(PrizeResponseDto prizeResponseDto, Link... links) {
        super(prizeResponseDto, links);
        add(linkTo(PrizeController.class).slash(prizeResponseDto.getId()).withSelfRel());
    }
}
