package com.pickmebackend.controller;

import com.pickmebackend.annotation.login.LoginValidation;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.login.JwtResponseDto;
import com.pickmebackend.domain.dto.login.LoginRequestDto;
import com.pickmebackend.resource.HateoasFormatter;
import com.pickmebackend.resource.LoginResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.pickmebackend.properties.RestDocsConstants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/login", produces = MediaTypes.HAL_JSON_VALUE)
public class LoginController {

    private final JwtProvider jwtProvider;

    private final ModelMapper modelMapper;

    private final HateoasFormatter hateoasFormatter;

    @PostMapping
    @LoginValidation
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto, Errors errors) {
        Account account = modelMapper.map(loginRequestDto, Account.class);
        String jwt = jwtProvider.generateToken(account);

        JwtResponseDto jwtResponseDto = new JwtResponseDto(jwt);
        LoginResource loginResource = new LoginResource(jwtResponseDto);
        loginResource.add(linkTo(AccountController.class).withRel(LOAD_ALL_ACCOUNT.getValue()));
        loginResource.add(linkTo(EnterpriseController.class).withRel(LOAD_ALL_ENTERPRISE.getValue()));
        loginResource.add(linkTo(ExperienceController.class).withRel(CREATE_EXPERIENCE.getValue()));
        loginResource.add(linkTo(LicenseController.class).withRel(CREATE_LICENSE.getValue()));
        loginResource.add(linkTo(PrizeController.class).withRel(CREATE_PRIZE.getValue()));
        loginResource.add(linkTo(ProjectController.class).withRel(CREATE_PROJECT.getValue()));
        loginResource.add(linkTo(SelfInterviewController.class).withRel(CREATE_SELF_INTERVIEW.getValue()));
        hateoasFormatter.addProfileRel(loginResource, "resources-login");

        return ResponseEntity.ok().body(loginResource);
    }
}

