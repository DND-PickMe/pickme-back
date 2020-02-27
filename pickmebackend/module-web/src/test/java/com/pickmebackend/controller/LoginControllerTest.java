package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.dto.account.AccountResponseDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.domain.dto.login.LoginRequestDto;
import com.pickmebackend.resource.AccountResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
        AccountRequestDto accountDto = this.createAccountDto();
        LoginRequestDto loginRequestDto = modelMapper.map(accountDto, LoginRequestDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("jwt").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.load-allAccounts").exists())
                .andExpect(jsonPath("_links.load-allEnterprises").exists())
                .andExpect(jsonPath("_links.create-experience").exists())
                .andExpect(jsonPath("_links.create-license").exists())
                .andExpect(jsonPath("_links.create-prize").exists())
                .andExpect(jsonPath("_links.create-project").exists())
                .andExpect(jsonPath("_links.create-selfInterview").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("login-account",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("load-allAccounts").description("link to load all accounts"),
                                linkWithRel("load-allEnterprises").description("link to load all enterprises"),
                                linkWithRel("create-experience").description("link to create experience"),
                                linkWithRel("create-license").description("link to create license"),
                                linkWithRel("create-prize").description("link to create prize"),
                                linkWithRel("create-project").description("link to create project"),
                                linkWithRel("create-selfInterview").description("link to create self interview"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("사용자의 이메일"),
                                fieldWithPath("password").description("사용자의 패스워드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("jwt").description("인증된 사용자에게 발급되는 jwt 토큰"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                        ))
        ;
    }

    @Test
    @Description("정상적으로 기업 담당자 로그인 하기")
    void loginEnterpriseSuccess() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = this.saveEnterprise();
        LoginRequestDto loginRequestDto = modelMapper.map(enterpriseRequestDto, LoginRequestDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("jwt").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.load-allAccounts").exists())
                .andExpect(jsonPath("_links.load-allEnterprises").exists())
                .andExpect(jsonPath("_links.create-experience").exists())
                .andExpect(jsonPath("_links.create-license").exists())
                .andExpect(jsonPath("_links.create-prize").exists())
                .andExpect(jsonPath("_links.create-project").exists())
                .andExpect(jsonPath("_links.create-selfInterview").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("login-enterprise",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("load-allAccounts").description("link to load all accounts"),
                                linkWithRel("load-allEnterprises").description("link to load all enterprises"),
                                linkWithRel("create-experience").description("link to create experience"),
                                linkWithRel("create-license").description("link to create license"),
                                linkWithRel("create-prize").description("link to create prize"),
                                linkWithRel("create-project").description("link to create project"),
                                linkWithRel("create-selfInterview").description("link to create self interview"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("기업 담당자의 이메일"),
                                fieldWithPath("password").description("기업 담당자의 패스워드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("jwt").description("인증된 기업 담당자에게 발급되는 jwt 토큰"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @Description("일반유저가 잘못된 이메일 입력시 400")
    void loginFailByAccountEmail() throws Exception {
        AccountRequestDto accountDto = this.createAccountDto();
        accountDto.setEmail("kiseok@email.com");
        LoginRequestDto loginRequestDto = modelMapper.map(accountDto, LoginRequestDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @Description("기업 담당자가 잘못된 이메일 입력시 400")
    void loginFailByEnterpriseEmail() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = this.saveEnterprise();
        enterpriseRequestDto.setEmail("kiseok@email.com");
        LoginRequestDto loginRequestDto = modelMapper.map(enterpriseRequestDto, LoginRequestDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @Description("일반유저가 잘못된 패스워드 입력시 400")
    void loginFailByAccountPassword() throws Exception {
        AccountRequestDto accountDto = this.createAccountDto();
        accountDto.setPassword("kiseokyang");
        LoginRequestDto loginRequestDto = modelMapper.map(accountDto, LoginRequestDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @Description("기업 담당자가 잘못된 패스워드 입력시 400")
    void loginFailByEnterprisePassword() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = this.saveEnterprise();
        enterpriseRequestDto.setPassword("kiseokyang");
        LoginRequestDto loginRequestDto = modelMapper.map(enterpriseRequestDto, LoginRequestDto.class);

        this.mockMvc.perform(post(loginURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    AccountRequestDto createAccountDto() throws Exception {
        AccountRequestDto accountDto =  AccountRequestDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .build();
        saveAccount(accountDto);

        return accountDto;
    }

    void saveAccount(AccountRequestDto accountDto) throws Exception {
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
        AccountResource accountResource = objectMapper.readValue(contentAsString, AccountResource.class);
        AccountResponseDto accountResponseDto = accountResource.getContent();
        assertNotNull(accountResponseDto.getId());
        assertEquals(accountResponseDto.getEmail(), appProperties.getTestEmail());
        assertEquals(accountResponseDto.getNickName(), appProperties.getTestNickname());
        assertNotNull(accountResponseDto.getCreatedAt());
    }

    private EnterpriseRequestDto saveEnterprise() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = EnterpriseRequestDto.builder()
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
                .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("password").doesNotExist())
        ;

        return enterpriseRequestDto;
    }

}
