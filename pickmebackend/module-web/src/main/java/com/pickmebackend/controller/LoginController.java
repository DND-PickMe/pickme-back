package com.pickmebackend.controller;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.login.JwtResponseDto;
import com.pickmebackend.domain.dto.login.LoginRequestDto;
import com.pickmebackend.resource.LoginResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.pickmebackend.error.ErrorMessage.USER_NOT_FOUND;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/login", produces = MediaTypes.HAL_JSON_VALUE)
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final ModelMapper modelMapper;

    private final ErrorsFormatter errorsFormatter;

    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatErrors(errors));
        }
        if(!authenticate(loginRequestDto.getEmail(), loginRequestDto.getPassword()))  {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(USER_NOT_FOUND.getValue()));
        }

        Account account = modelMapper.map(loginRequestDto, Account.class);
        String jwt = jwtProvider.generateToken(account);

        JwtResponseDto jwtResponseDto = new JwtResponseDto(jwt);
        LoginResource loginResource = new LoginResource(jwtResponseDto);
        loginResource.add(linkTo(AccountController.class).withRel("load-allAccounts"));
        loginResource.add(linkTo(EnterpriseController.class).withRel("load-allEnterprises"));
        loginResource.add(linkTo(ExperienceController.class).withRel("create-experience"));
        loginResource.add(linkTo(LicenseController.class).withRel("create-license"));
        loginResource.add(linkTo(PrizeController.class).withRel("create-prize"));
        loginResource.add(linkTo(ProjectController.class).withRel("create-project"));
        loginResource.add(linkTo(SelfInterviewController.class).withRel("create-selfInterview"));
        loginResource.add(new Link("/docs/index.html#resources-login").withRel("profile"));

        return ResponseEntity.ok().body(loginResource);
    }

    private boolean authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return true;
        }
        catch (DisabledException | BadCredentialsException e) {
            return false;
        }
    }
}

