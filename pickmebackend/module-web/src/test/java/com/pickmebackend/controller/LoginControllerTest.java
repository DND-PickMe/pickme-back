package com.pickmebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.properties.AppProperties;
import com.pickmebackend.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Autowired
    ObjectMapper objectMapper;

    private final String loginURL = "/api/login";

    @BeforeEach
    void setUp() throws Exception {
        this.accountRepository.deleteAll();
    }

    @Test
    @Description("정상적으로 로그인 하기")
    void loginSuccess() throws Exception {
        AccountDto accountDto = this.createAccountDto();

        this.mockMvc.perform(post(loginURL)
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("jwt").exists())
        ;
    }

    @Test
    @Description("잘못된 이메일 입력시 401")
    void loginFailByEmail() throws Exception {
        AccountDto accountDto = this.createAccountDto();
        accountDto.setEmail("kiseok@email.com");

        this.mockMvc.perform(post(loginURL)
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @Description("잘못된 패스워드 입력시 400")
    void loginFailByPassword() throws Exception {
        AccountDto accountDto = this.createAccountDto();
        accountDto.setPassword("kiseokyang");

        this.mockMvc.perform(post(loginURL)
                        .accept(MediaTypes.HAL_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }



    AccountDto createAccountDto() throws Exception {
        AccountDto accountDto =  AccountDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .build();
        saveAccount(accountDto);

        return accountDto;
    }

    void saveAccount(AccountDto accountDto) throws Exception {
        ResultActions actions = mockMvc.perform(post("/api/accounts")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").value(appProperties.getTestNickname()))
                .andExpect(jsonPath("createdAt").exists());

        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        Account account = objectMapper.readValue(contentAsString, Account.class);
        assertNotNull(account.getId());
        assertEquals(account.getEmail(), appProperties.getTestEmail());
        assertEquals(account.getNickName(), appProperties.getTestNickname());
        assertNotNull(account.getCreatedAt());
    }

}
