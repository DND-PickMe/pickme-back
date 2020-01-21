package com.pickmebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.error.ErrorMessageConstant;
import com.pickmebackend.properties.AppProperties;
import com.pickmebackend.repository.AccountRepository;
import com.pickmebackend.service.AccountService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static com.pickmebackend.error.ErrorMessageConstant.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Autowired
    ModelMapper modelMapper;

    private final String accountURL = "/api/accounts/";

    @BeforeEach
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
                .andExpect(jsonPath("createdAt").exists());

        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        Account account = objectMapper.readValue(contentAsString, Account.class);
        assertNotNull(account.getId());
        assertEquals(account.getEmail(), appProperties.getTestEmail());
        assertEquals(account.getNickName(), appProperties.getTestNickname());
        assertNotNull(account.getCreatedAt());
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
        newAccount.setEmail("update@email.com");
        newAccount.setNickName("updateNick");

        AccountDto updateAccountDto = modelMapper.map(newAccount, AccountDto.class);
        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value("update@email.com"))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").value("updateNick"))
                .andExpect(jsonPath("createdAt").exists());
    }

    @ParameterizedTest(name = "{displayName}{index}")
    @DisplayName("유저 수정 시 email, password, nickname 중 하나라도 공백이 들어올 경우 Bad Request 반환")
    @CsvSource({"'', 'password', '디엔디'", "'user@email.com', '', '디엔디'", "'user@email.com', 'password', ''"})
    void updateAccount_empty_input(String email, String password, String nickName) throws Exception {
        Account newAccount = createAccount();
        newAccount.setEmail(email);
        newAccount.setPassword(password);
        newAccount.setNickName(nickName);

        AccountDto updateAccountDto = modelMapper.map(newAccount, AccountDto.class);
        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
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
    @DisplayName("유저 생성 시 email, password, nickname 중 하나라도 null이 들어올 경우 Bad Request 반환")
    void updateAccount_null_input(RepetitionInfo info) throws Exception {
        Account newAccount = createAccount();

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
        newAccount.setEmail("update@email.com");
        newAccount.setNickName("updateNick");

        AccountDto updateAccountDto = modelMapper.map(newAccount, AccountDto.class);
        mockMvc.perform(put(accountURL + "{accountId}", -1)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(USERNOTFOUND));
    }

    @Test
    @DisplayName("정상적으로 유저를 삭제")
    void deleteAccount() throws Exception {
        Account newAccount = createAccount();

        mockMvc.perform(delete(accountURL + "{accountId}", newAccount.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 유저 삭제 요청 시 Bad Request 반환")
    void deleteAccount_not_found_user() throws Exception {
        createAccount();

        mockMvc.perform(delete(accountURL + "{accountId}", -1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)));
    }

    private Account createAccount() {
        Account account = Account.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .createdAt(LocalDateTime.now())
                .build();
        return accountRepository.save(account);
    }
}