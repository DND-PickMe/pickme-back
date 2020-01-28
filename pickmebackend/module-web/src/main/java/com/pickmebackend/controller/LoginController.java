package com.pickmebackend.controller;

import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/login", produces = MediaTypes.HAL_JSON_VALUE)
public class LoginController {

    private final LoginService loginService;

    @GetMapping
    public String login() {
        return "/login";
    }

    @PostMapping
    ResponseEntity<?> login(@Valid @RequestBody AccountDto accountDto, Errors errors) throws Exception {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return loginService.login(accountDto);
    }
}
