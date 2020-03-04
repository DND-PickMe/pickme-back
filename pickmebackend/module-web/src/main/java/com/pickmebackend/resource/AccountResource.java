package com.pickmebackend.resource;

import com.pickmebackend.controller.AccountController;
import com.pickmebackend.domain.dto.account.AccountFavoriteFlagResponseDto;
import com.pickmebackend.domain.dto.account.AccountResponseDto;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@NoArgsConstructor
public class AccountResource extends EntityModel<AccountResponseDto> {

    public AccountResource(AccountResponseDto accountResponseDto, Link... links) {
        super(accountResponseDto, links);
        add(linkTo(AccountController.class).slash(accountResponseDto.getId()).withSelfRel());
    }
}
