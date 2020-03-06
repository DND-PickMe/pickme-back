package com.pickmebackend.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.Technology;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.domain.dto.verificationCode.SendCodeRequestDto;
import com.pickmebackend.domain.dto.verificationCode.VerifyCodeRequestDto;
import com.pickmebackend.domain.dto.verificationCode.VerifyCodeResponseDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.properties.AppProperties;
import com.pickmebackend.repository.TechnologyRepository;
import com.pickmebackend.repository.VerificationCodeRepository;
import com.pickmebackend.repository.account.AccountRepository;
import com.pickmebackend.repository.enterprise.EnterpriseRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "pickme-back.ga", uriPort = 8083)
@Import(RestDocsConfiguration.class)
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

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected EnterpriseRepository enterpriseRepository;

    @Autowired
    protected TechnologyRepository technologyRepository;

    @Autowired
    protected VerificationCodeRepository verificationCodeRepository;

    protected String jwt;

    protected Account createAccount() {
        Account account = Account.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .createdAt(LocalDateTime.now())
                .career("신입")
                .positions(new HashSet<>(Arrays.asList("BackEnd", "FrontEnd")))
                .userRole(UserRole.USER)
                .build();
        return accountRepository.save(account);
    }

    protected Account createAnotherAccount() {
        Account account = Account.builder()
                .email(appProperties.getTestAnotherEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestAnotherNickname())
                .oneLineIntroduce("hello")
                .createdAt(LocalDateTime.now())
                .career("5년차 이상")
                .positions(new HashSet<>(Collections.singleton("Designer")))
                .userRole(UserRole.USER)
                .build();
        return accountRepository.save(account);
    }

    protected void createAccounts(int i) {
        Account account = Account.builder()
                .email(i + appProperties.getTestEmail())
                .password(i + appProperties.getTestPassword())
                .nickName(i + appProperties.getTestNickname())
                .oneLineIntroduce(i + "한 줄 소개")
                .createdAt(LocalDateTime.now())
                .career(i + "년차")
                .positions(new HashSet<>(Collections.singleton("개발자" + i)))
                .userRole(UserRole.USER)
                .build();
        accountRepository.save(account);
    }

    protected void createAccountsWithTech(int i, List<Technology> technologyList) throws Exception {
        Account account = Account.builder()
                .email(i + appProperties.getTestEmail())
                .password(i + appProperties.getTestPassword())
                .nickName(i + appProperties.getTestNickname())
                .oneLineIntroduce(i + "한 줄 소개")
                .createdAt(LocalDateTime.now())
                .career(i + "년차")
                .favoriteCount(i)
                .hits(40 - i)
                .positions(new HashSet<>(Collections.singleton("개발자")))
                .userRole(UserRole.USER)
                .build();
        Account savedAccount = accountRepository.save(account);
        AccountRequestDto map = modelMapper.map(savedAccount, AccountRequestDto.class);
        map.setTechnologies(Collections.singletonList(technologyList.get(i % 3)));

        jwt = jwtProvider.generateToken(savedAccount);

        mockMvc.perform(put("/api/accounts/" + "{accountId}", savedAccount.getId())
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    protected EnterpriseRequestDto createEnterpriseDto() {
        EnterpriseRequestDto enterpriseRequestDto =
                EnterpriseRequestDto.builder()
                .email("ENT" + appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .registrationNumber(appProperties.getTestRegistrationNumber())
                .name(appProperties.getTestName())
                .address(appProperties.getTestAddress())
                .ceoName(appProperties.getTestCeoName())
                .build();
        Enterprise enterprise = modelMapper.map(enterpriseRequestDto, Enterprise.class);

        Account account = modelMapper.map(enterpriseRequestDto, Account.class);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setUserRole(UserRole.ENTERPRISE);
        account.setEnterprise(enterprise);

        enterprise.setAccount(account);

        accountRepository.save(account);
        enterpriseRepository.save(enterprise);

        return enterpriseRequestDto;
    }

    protected EnterpriseRequestDto createAnotherEnterpriseDto() {
        EnterpriseRequestDto enterpriseRequestDto =
                EnterpriseRequestDto.builder()
                        .email("another" + appProperties.getTestEmail())
                        .password("another" + appProperties.getTestPassword())
                        .registrationNumber("another" + appProperties.getTestRegistrationNumber())
                        .name("another" + appProperties.getTestName())
                        .address("another" + appProperties.getTestAddress())
                        .ceoName("another" + appProperties.getTestCeoName())
                        .build();
        Enterprise enterprise = modelMapper.map(enterpriseRequestDto, Enterprise.class);

        Account account = modelMapper.map(enterpriseRequestDto, Account.class);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setUserRole(UserRole.ENTERPRISE);
        account.setEnterprise(enterprise);

        enterprise.setAccount(account);

        accountRepository.save(account);
        enterpriseRepository.save(enterprise);

        return enterpriseRequestDto;
    }

    protected EnterpriseRequestDto createEnterpriseDtos(int i) {
        EnterpriseRequestDto enterpriseRequestDto =
                EnterpriseRequestDto.builder()
                        .email(i + "ENT" + appProperties.getTestEmail())
                        .password(i + appProperties.getTestPassword())
                        .registrationNumber(i + appProperties.getTestRegistrationNumber())
                        .name(i + appProperties.getTestName())
                        .address(i + appProperties.getTestAddress())
                        .ceoName(i + appProperties.getTestCeoName())
                        .build();
        Enterprise enterprise = modelMapper.map(enterpriseRequestDto, Enterprise.class);

        Account account = modelMapper.map(enterpriseRequestDto, Account.class);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setCreatedAt(LocalDateTime.now());
        account.setUserRole(UserRole.ENTERPRISE);
        account.setEnterprise(enterprise);

        enterprise.setAccount(account);

        accountRepository.save(account);
        enterpriseRepository.save(enterprise);

        return enterpriseRequestDto;
    }

    protected String createEnterpriseJwt() {
        EnterpriseRequestDto enterpriseRequestDto = createAnotherEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        return "Bearer " + jwtProvider.generateToken(account);
    }

    protected String createAccountJwt() {
        Account newAccount = createAnotherAccount();
        return "Bearer " + jwtProvider.generateToken(newAccount);
    }

    protected void verifyEmail(String email) throws Exception {
        SendCodeRequestDto sendCodeRequestDto = SendCodeRequestDto.builder()
                .email(email)
                .build();

        ResultActions resultActions = this.mockMvc.perform(post("/api/accounts/sendCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendCodeRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        VerifyCodeRequestDto verifyCodeRequestDto = objectMapper.readValue(contentAsString, VerifyCodeRequestDto.class);

        assertThat(verifyCodeRequestDto.getEmail()).isEqualTo(appProperties.getTestEmail());
        assertThat(verifyCodeRequestDto.getCode()).isNotBlank();

        ResultActions resultActions2 = this.mockMvc.perform(put("/api/accounts/matchCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyCodeRequestDto)))
                .andDo(print())
                .andExpect(status().isOk());

        String contentAsString2 = resultActions2.andReturn().getResponse().getContentAsString();
        VerifyCodeResponseDto verifyCodeResponseDto = objectMapper.readValue(contentAsString2, VerifyCodeResponseDto.class);

        assertThat(verifyCodeResponseDto.getEmail()).isEqualTo(appProperties.getTestEmail());
        assertThat(verifyCodeResponseDto.getCode()).isEqualTo(verifyCodeRequestDto.getCode());
        assertThat(verifyCodeResponseDto.isVerified()).isTrue();

    }
}
