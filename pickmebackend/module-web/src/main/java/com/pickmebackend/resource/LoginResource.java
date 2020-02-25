package com.pickmebackend.resource;

import com.pickmebackend.controller.LoginController;
import com.pickmebackend.domain.dto.login.JwtResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class LoginResource extends EntityModel<JwtResponseDto> {

    public LoginResource(JwtResponseDto jwtResponseDto, Link... links) {
        super(jwtResponseDto, links);
        add(linkTo(LoginController.class).withSelfRel());
    }
}
