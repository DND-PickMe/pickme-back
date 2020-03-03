package com.pickmebackend.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.Technology;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.properties.AppProperties;
import com.pickmebackend.repository.EnterpriseRepository;
import com.pickmebackend.repository.TechnologyRepository;
import com.pickmebackend.repository.account.AccountRepository;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDateTime;
import java.util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
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
                .email(appProperties.getTestEmail())
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
                        .email(i + appProperties.getTestEmail())
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
}
