package com.pickmebackend.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.properties.AppProperties;
import com.pickmebackend.repository.AccountRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected JwtProvider jwtProvider;

    @Autowired
    protected AppProperties appProperties;

    protected String jwt;

    @Autowired
    protected AccountRepository accountRepository;

    protected Account createAccount() {
        Account account = Account.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .createdAt(LocalDateTime.now())
                .build();
        return accountRepository.save(account);
    }

    protected Account createAnotherAccount() {
        Account account = Account.builder()
                .email(appProperties.getTestAnotherEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestAnotherNickname())
                .createdAt(LocalDateTime.now())
                .build();
        return accountRepository.save(account);
    }
}
