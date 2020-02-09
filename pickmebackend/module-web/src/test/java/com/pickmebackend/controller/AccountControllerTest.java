package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.AccountDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Arrays;
import java.util.List;
import static com.pickmebackend.error.ErrorMessageConstant.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends BaseControllerTest {

    private final String accountURL = "/api/accounts/";

    private final String BEARER = "Bearer ";

    @AfterEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 유저를 생성")
    void saveAccount() throws Exception {
        assert appProperties.getTestEmail() != null;
        assert appProperties.getTestPassword() != null;
        assert appProperties.getTestNickname() != null;

        AccountDto accountDto = AccountDto.builder()
                                            .email(appProperties.getTestEmail())
                                            .password(appProperties.getTestPassword())
                                            .nickName(appProperties.getTestNickname())
                                            .oneLineIntroduce("안녕하세요. 저는 취미도 개발, 특기도 개발인 학생 개발자 양기석입니다.")
                                            .technology(Arrays.asList("SpringBoot", "NodeJS", "Git", "Github", "JPA", "Java8"))
                                            .build();

        ResultActions actions = mockMvc.perform(post(accountURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").value(appProperties.getTestNickname()))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("oneLineIntroduce", is("안녕하세요. 저는 취미도 개발, 특기도 개발인 학생 개발자 양기석입니다.")))
                .andExpect(jsonPath("technology", is(Arrays.asList("SpringBoot", "NodeJS", "Git", "Github", "JPA", "Java8"))));

        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        Account account = objectMapper.readValue(contentAsString, Account.class);
        assertNotNull(account.getId());
        assertEquals(account.getEmail(), appProperties.getTestEmail());
        assertEquals(account.getNickName(), appProperties.getTestNickname());
        assertNotNull(account.getCreatedAt());
    }

    @Test
    @DisplayName("회원 가입시 중복된 email 존재할 경우 Bad Request 반환")
    void check_duplicated_Account() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .build();

        this.mockMvc.perform(post(accountURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        this.mockMvc.perform(post(accountURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(DUPLICATEDUSER)))
        ;
    }

    @ParameterizedTest(name = "{displayName}{index}")
    @DisplayName("유저 생성 시 email, password, nickname 중 하나라도 공백이 들어올 경우 Bad Request 반환")
    @CsvSource({"'', 'password', '디엔디'", "'user@email.com', '', '디엔디'", "'user@email.com', 'password', ''"})
    void saveAccount_empty_input(String email, String password, String nickName) throws Exception {
        assertNotNull(email);
        assertNotNull(password);
        assertNotNull(nickName);

        AccountDto accountDto = AccountDto.builder()
                                            .email(email)
                                            .password(password)
                                            .nickName(nickName)
                                            .build();

        mockMvc.perform(post(accountURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists());
    }

    @RepeatedTest(value = 3, name = "{displayName} {currentRepetition}")
    @DisplayName("유저 생성 시 email, password, nickname 중 하나라도 null이 들어올 경우 Bad Request 반환")
    void saveAccount_null_input(RepetitionInfo info) throws Exception {
        AccountDto accountDto = AccountDto.builder()
                                            .email(appProperties.getTestEmail())
                                            .password(appProperties.getTestPassword())
                                            .nickName(appProperties.getTestNickname())
                                            .build();

        int currentRepetition = info.getCurrentRepetition();
        if (currentRepetition == 1) {
            accountDto.setEmail(null);
        } else if (currentRepetition == 2) {
            accountDto.setPassword(null);
        } else if (currentRepetition == 3) {
            accountDto.setNickName(null);
        }

        mockMvc.perform(post(accountURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists());
    }

    @Test
    @DisplayName("정상적으로 유저를 수정")
    void updateAccount() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        String updatedEmail = "update@email.com";
        String updateNickname = "updateNick";
        String oneLineIntroduce = "안녕하세요. 저는 백엔드 개발자를 지망하고 있습니다.";
        List<String> technology = Arrays.asList("SpringBoot", "Java8", "MySQL");

        newAccount.setEmail(updatedEmail);
        newAccount.setNickName(updateNickname);
        newAccount.setOneLineIntroduce(oneLineIntroduce);
        newAccount.setTechnology(technology);

        AccountDto updateAccountDto = modelMapper.map(newAccount, AccountDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(updatedEmail))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").value(updateNickname))
                .andExpect(jsonPath("technology", is(technology)))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("oneLineIntroduce").value(oneLineIntroduce));
    }

    @ParameterizedTest(name = "{displayName}{index}")
    @DisplayName("유저 수정 시 email, password, nickname 중 하나라도 공백이 들어올 경우 Bad Request 반환")
    @CsvSource({"'', 'password', '디엔디'", "'user@email.com', '', '디엔디'", "'user@email.com', 'password', ''"})
    void updateAccount_empty_input(String email, String password, String nickName) throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        newAccount.setEmail(email);
        newAccount.setPassword(password);
        newAccount.setNickName(nickName);

        AccountDto updateAccountDto = modelMapper.map(newAccount, AccountDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists());
    }

    @RepeatedTest(value = 3, name = "{displayName} {currentRepetition}")
    @DisplayName("유저 수정 시 email, password, nickname 중 하나라도 null이 들어올 경우 Bad Request 반환")
    void updateAccount_null_input(RepetitionInfo info) throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        int currentRepetition = info.getCurrentRepetition();
        if (currentRepetition == 1) {
            newAccount.setEmail(null);
        } else if (currentRepetition == 2) {
            newAccount.setPassword(null);
        } else if (currentRepetition == 3) {
            newAccount.setNickName(null);
        }

        AccountDto updateAccountDto = modelMapper.map(newAccount, AccountDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists());
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 유저 수정 요청 시 Bad Request 반환")
    void updateAccount_not_fount_user() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        newAccount.setEmail("update@email.com");
        newAccount.setNickName("updateNick");

        AccountDto updateAccountDto = modelMapper.map(newAccount, AccountDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", -1)
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(USERNOTFOUND));
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 수정 요청 시 Bad Request 반환")
    void updateAccount_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = jwtProvider.generateToken(anotherAccount);

        newAccount.setEmail("update@email.com");
        newAccount.setNickName("updateNick");

        AccountDto updateAccountDto = modelMapper.map(newAccount, AccountDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(UNAUTHORIZEDUSER));
    }

    @Test
    @DisplayName("정상적으로 유저를 삭제")
    void deleteAccount() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        mockMvc.perform(delete(accountURL + "{accountId}", newAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 유저 삭제 요청 시 Bad Request 반환")
    void deleteAccount_not_found_user() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        mockMvc.perform(delete(accountURL + "{accountId}", -1)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)));
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 삭제 요청 시 Bad Request 반환")
    void deleteAccount_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = jwtProvider.generateToken(anotherAccount);

        mockMvc.perform(delete(accountURL + "{accountId}", newAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)));
    }

    @Test
    @DisplayName("정상적으로 유저 조회")
    void getAccount() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        mockMvc.perform(get(accountURL + "{accountId}", newAccount.getId())
                                    .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(appProperties.getTestEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(appProperties.getTestNickname())));
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 유저 조회 요청 시 Bad Request 반환")
    void getAccount_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        mockMvc.perform(get(accountURL + "{accountId}", -1)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)));
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 조회 요청 시 Bad Request 반환")
    void getAccount_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = jwtProvider.generateToken(anotherAccount);

        mockMvc.perform(get(accountURL + "{accountId}", newAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)));
    }
}