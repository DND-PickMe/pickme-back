package com.pickmebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.properties.AppProperties;
import com.pickmebackend.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.pickmebackend.error.ErrorMessageConstant.INVALIDIMAGE;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AccountImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private AccountRepository accountRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private JwtProvider jwtProvider;

    @AfterEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("유저 생성 시 디폴트 이미지 주입 받는지 확인")
    void createUser_default_image() throws Exception {
        String oneLineIntroduce = "테스트 코드 작성을 중요시 합니다!";
        AccountRequestDto accountDto = createAccount();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(appProperties.getTestEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(appProperties.getTestNickname())))
                .andExpect(jsonPath("oneLineIntroduce", is(oneLineIntroduce)))
                .andExpect(jsonPath("image", is("https://pickme-back.ga/api/images/default_user.png")));
    }

    @Test
    @DisplayName("이미지가 정상적으로 저장되는지 테스트")
    void save_image() throws Exception {
        MockMultipartFile mockImage = new MockMultipartFile("image", "test.png", MediaType.IMAGE_PNG_VALUE, "test image content".getBytes());

        Account savedAccount = generateAccount();
        String jwt = jwtProvider.generateToken(savedAccount);

        mockMvc.perform(multipart("/api/images")
                        .file(mockImage)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("이미지 파일이 아닌 경우")
    void save_image_not_image() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile("image", "mugon.txt", MediaType.TEXT_PLAIN_VALUE, "test txt content".getBytes());

        Account savedAccount = generateAccount();
        String jwt = jwtProvider.generateToken(savedAccount);

        mockMvc.perform(multipart("/api/images")
                .file(invalidFile)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(INVALIDIMAGE)));
    }

    @Test
    @DisplayName("파일 이름이 유효하지 않은 경우")
    void save_image_invalid_fileName() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "../mugon.png", MediaType.IMAGE_PNG_VALUE, "test png content".getBytes());

        Account savedAccount = generateAccount();
        String jwt = jwtProvider.generateToken(savedAccount);

        mockMvc.perform(multipart("/api/images")
                .file(image)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(INVALIDIMAGE)));
    }

    @Test
    @DisplayName("파일 내용이 없는 경우")
    void save_image_empty_content() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "mugon.png", MediaType.IMAGE_PNG_VALUE, "".getBytes());

        Account savedAccount = generateAccount();
        String jwt = jwtProvider.generateToken(savedAccount);

        mockMvc.perform(multipart("/api/images")
                .file(image)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(INVALIDIMAGE)));
    }

    private AccountRequestDto createAccount() {
        return AccountRequestDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .oneLineIntroduce("테스트 코드 작성을 중요시 합니다!")
                .build();

    }

    private Account generateAccount() {
        Account account = modelMapper.map(AccountRequestDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .oneLineIntroduce("테스트 코드 작성을 중요시 합니다!")
                .build(), Account.class);

        account.setUserRole(UserRole.USER);

        return accountRepository.save(account);
    }
}