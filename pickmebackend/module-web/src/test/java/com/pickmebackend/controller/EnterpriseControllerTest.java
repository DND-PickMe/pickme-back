package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.EnterpriseDto;
import com.pickmebackend.domain.enums.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Optional;
import java.util.stream.Stream;
import static com.pickmebackend.error.ErrorMessageConstant.DUPLICATEDUSER;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnterpriseControllerTest extends BaseControllerTest {

    private String enterpriseURL = "/api/enterprises/";

    private final String BEARER = "Bearer ";

    @AfterEach
    void setUp() {
        enterpriseRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 한명의 기업담당자 불러오기")
    void load_enterprise() throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        this.mockMvc.perform(get(enterpriseURL + "{enterpriseId}", account.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("저장되어 있지 않은 기업담당자 불러 오기")
    void load_non_enterprise() throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        this.mockMvc.perform(get(enterpriseURL + "{enterpriseId}", -1)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @DisplayName("정상적으로 기업담담자 생성")
    void save_enterprise() throws Exception {
        EnterpriseDto enterpriseDto = EnterpriseDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .registrationNumber(appProperties.getTestRegistrationNumber())
                .name(appProperties.getTestName())
                .address(appProperties.getTestAddress())
                .ceoName(appProperties.getTestCeoName())
                .build();

        ResultActions actions = this.mockMvc.perform(post(enterpriseURL)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("password").doesNotExist())
        ;

        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        Account account = objectMapper.readValue(contentAsString, Account.class);
        Enterprise enterprise = account.getEnterprise();

        assertNotNull(account.getId());
        assertEquals(account.getEmail(), appProperties.getTestEmail());
        assertEquals(account.getNickName(), appProperties.getTestName());
        assertEquals(account.getUserRole(), UserRole.ENTERPRISE);
        assertNotNull(account.getCreatedAt());
        assertEquals(account.getEnterprise(), enterprise);

        assertNotNull(enterprise.getId());
        assertEquals(enterprise.getRegistrationNumber(), appProperties.getTestRegistrationNumber());
        assertEquals(enterprise.getName(), appProperties.getTestName());
        assertEquals(enterprise.getAddress(), appProperties.getTestAddress());
        assertEquals(enterprise.getCeoName(), appProperties.getTestCeoName());
    }

    @Test
    @DisplayName("기업 담당자 저장 시 동일한 email을 가진 유저가 존재할 경우 Bad Request 반환")
    void check_duplicated_enterprise() throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        this.mockMvc.perform(post(enterpriseURL)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(DUPLICATEDUSER)))
        ;
    }

    @ParameterizedTest
    @DisplayName("기업 담당자 저장시 하나의 필드라도 공백이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForEmptyStringCheck")
    void check_save_empty_input_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        EnterpriseDto enterpriseDto = EnterpriseDto.builder()
                .email(email)
                .password(password)
                .registrationNumber(registrationNumber)
                .name(name)
                .address(address)
                .ceoName(ceoName)
                .build();

        this.mockMvc.perform(post(enterpriseURL)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
        ;
    }

    @ParameterizedTest
    @DisplayName("기업 담당자 저장시 하나의 필드라도 null이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForNullCheck")
    void check_save_null_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        EnterpriseDto enterpriseDto = EnterpriseDto.builder()
                .email(email)
                .password(password)
                .registrationNumber(registrationNumber)
                .name(name)
                .address(address)
                .ceoName(ceoName)
                .build();

        this.mockMvc.perform(post(enterpriseURL)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
        ;
    }

    @Test
    @DisplayName("정상적으로 기업 담당자 수정")
    void update_enterprise() throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        enterpriseDto.setEmail("newEmail@email.com");
        enterpriseDto.setPassword("newPassword");
        enterpriseDto.setRegistrationNumber("newRegistrationNumber");
        enterpriseDto.setName("newName");
        enterpriseDto.setAddress("newAddress");
        enterpriseDto.setCeoName("newCeoName");

        ResultActions resultActions = this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", account.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("newEmail@email.com"))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").value("newName"))
        ;
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        Account savedAccount = objectMapper.readValue(contentAsString, Account.class);
        Enterprise enterprise = enterpriseRepository.findById(account.getEnterprise().getId()).get();

        assertNotNull(savedAccount.getId());
        assertEquals(savedAccount.getEmail(), "newEmail@email.com");
        assertEquals(savedAccount.getNickName(), "newName");
        assertEquals(savedAccount.getUserRole(), UserRole.ENTERPRISE);
        assertNotNull(savedAccount.getCreatedAt());
        assertEquals(savedAccount.getEnterprise(), enterprise);

        assertNotNull(enterprise.getId());
        assertEquals(enterprise.getRegistrationNumber(), "newRegistrationNumber");
        assertEquals(enterprise.getName(), "newName");
        assertEquals(enterprise.getAddress(), "newAddress");
        assertEquals(enterprise.getCeoName(), "newCeoName");
    }

    @Test
    @DisplayName("수정시 존재 하지 않는 기업 담당자 수정 할 경우 Bad Request 반환")
    void update_non_enterprise() throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        enterpriseDto.setEmail("newEmail@email.com");
        enterpriseDto.setPassword("newPassword");
        enterpriseDto.setRegistrationNumber("newRegistrationNumber");
        enterpriseDto.setName("newName");
        enterpriseDto.setAddress("newAddress");
        enterpriseDto.setCeoName("newCeoName");

        this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", -1)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @ParameterizedTest
    @DisplayName("기업 담당자 수정시 하나의 필드라도 공백이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForEmptyStringCheck")
    void check_update_empty_string_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        enterpriseDto.setEmail(email);
        enterpriseDto.setPassword(password);
        enterpriseDto.setRegistrationNumber(registrationNumber);
        enterpriseDto.setName(name);
        enterpriseDto.setAddress(address);
        enterpriseDto.setCeoName(ceoName);

        this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", account.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
        ;
    }

    @ParameterizedTest
    @DisplayName("기업 담당자 수정시 하나의 필드라도 null이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForNullCheck")
    void check_update_null_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        enterpriseDto.setEmail(email);
        enterpriseDto.setPassword(password);
        enterpriseDto.setRegistrationNumber(registrationNumber);
        enterpriseDto.setName(name);
        enterpriseDto.setAddress(address);
        enterpriseDto.setCeoName(ceoName);

        this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", account.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
        ;
    }

    @Test
    @DisplayName("정상적으로 기업 담당자 삭제")
    void delete_enterprise() throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        this.mockMvc.perform(delete(enterpriseURL + "{enterpriseId}", account.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 기업담당자 삭제 요청시 Bad Request 반환")
    void delete_non_enterprise() throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        this.mockMvc.perform(delete(enterpriseURL + "{enterpriseId}", -1)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    private static Stream<Arguments> streamForEmptyStringCheck() {
        return Stream.of(
                Arguments.of("", "password", "registraionNumber", "name", "address", "ceoName"),
                Arguments.of("email", "", "registraionNumber", "name", "address", "ceoName"),
                Arguments.of("email", "password", "", "name", "address", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "", "address", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "name", "", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "name", "address", "")
        );
    }

    private static Stream<Arguments> streamForNullCheck() {
        return Stream.of(
                Arguments.of(null, "password", "registraionNumber", "name", "address", "ceoName"),
                Arguments.of("email", null, "registraionNumber", "name", "address", "ceoName"),
                Arguments.of("email", "password", null, "name", "address", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", null, "address", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "name", null, "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "name", "address", null)
        );
    }
}