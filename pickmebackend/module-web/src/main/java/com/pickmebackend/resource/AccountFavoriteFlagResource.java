package com.pickmebackend.resource;

import com.pickmebackend.controller.AccountController;
import com.pickmebackend.domain.dto.account.AccountFavoriteFlagResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AccountFavoriteFlagResource extends EntityModel<AccountFavoriteFlagResponseDto> {

    public AccountFavoriteFlagResource(AccountFavoriteFlagResponseDto content, Link... links) {
        super(content, links);
        add(linkTo(AccountController.class).slash(content.getId()).withSelfRel());
    }
}
