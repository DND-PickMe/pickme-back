package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.domain.dto.EnterpriseDto;
import com.pickmebackend.domain.dto.LoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest extends BaseControllerTest {

    private final String loginURL = "/api/login";

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        enterpriseRepository.deleteAll();
    }

    @Test
    @Description("정상적으로 일반 유저 로그인 하기")
    void loginAccountSuccess() throws Exception {
        AccountDto accountDto = this.createAccountDto();
        LoginDto loginDto = modelMapper.map(accountDto, LoginDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("jwt").exists())
        ;
    }

    @Test
    @Description("정상적으로 기업 담당자 로그인 하기")
    void loginEnterpriseSuccess() throws Exception {
        EnterpriseDto enterpriseDto = this.saveEnterprise();
        LoginDto loginDto = modelMapper.map(enterpriseDto, LoginDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("jwt").exists())
        ;
    }

    @Test
    @Description("일반유저가 잘못된 이메일 입력시 400")
    void loginFailByAccountEmail() throws Exception {
        AccountDto accountDto = this.createAccountDto();
        accountDto.setEmail("kiseok@email.com");
        LoginDto loginDto = modelMapper.map(accountDto, LoginDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @Description("기업 담당자가 잘못된 이메일 입력시 400")
    void loginFailByEnterpriseEmail() throws Exception {
        EnterpriseDto enterpriseDto = this.saveEnterprise();
        enterpriseDto.setEmail("kiseok@email.com");
        LoginDto loginDto = modelMapper.map(enterpriseDto, LoginDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @Description("일반유저가 잘못된 패스워드 입력시 400")
    void loginFailByAccountPassword() throws Exception {
        AccountDto accountDto = this.createAccountDto();
        accountDto.setPassword("kiseokyang");
        LoginDto loginDto = modelMapper.map(accountDto, LoginDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @Description("기업 담당자가 잘못된 패스워드 입력시 400")
    void loginFailByEnterprisePassword() throws Exception {
        EnterpriseDto enterpriseDto = this.saveEnterprise();
        enterpriseDto.setPassword("kiseokyang");
        LoginDto loginDto = modelMapper.map(enterpriseDto, LoginDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
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

    private EnterpriseDto saveEnterprise() throws Exception {
        EnterpriseDto enterpriseDto = EnterpriseDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .registrationNumber(appProperties.getTestRegistrationNumber())
                .name(appProperties.getTestName())
                .address(appProperties.getTestAddress())
                .ceoName(appProperties.getTestCeoName())
                .build();

        this.mockMvc.perform(post("/api/enterprises")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("password").doesNotExist())
        ;

        return enterpriseDto;
    }

}
