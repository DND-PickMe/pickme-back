package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Technology;
import com.pickmebackend.domain.VerificationCode;
import com.pickmebackend.domain.dto.account.AccountInitialRequestDto;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.dto.account.AccountResponseDto;
import com.pickmebackend.domain.dto.verificationCode.SendCodeRequestDto;
import com.pickmebackend.domain.dto.verificationCode.VerifyCodeRequestDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.repository.TechnologyRepository;
import com.pickmebackend.repository.account.AccountTechRepository;
import com.pickmebackend.resource.AccountResource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest extends BaseControllerTest {

    private final String accountURL = "/api/accounts/";

    private final String BEARER = "Bearer ";

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private AccountTechRepository accountTechRepository;

    @AfterEach
    void setUp() {
        accountTechRepository.deleteAll();
        accountRepository.deleteAll();
        enterpriseRepository.deleteAll();
        verificationCodeRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 이메일에 인증코드 전송")
    @Disabled
    void send_code() throws Exception   {
        SendCodeRequestDto sendCodeRequestDto = SendCodeRequestDto
                .builder()
                .email(appProperties.getTestEmail())
                .build();

        this.mockMvc.perform(post(accountURL + "sendCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendCodeRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
        ;
    }

    @Test
    @DisplayName("이메일이 null일 때 인증코드 전송 시 Bad Request")
    void send_code_null() throws Exception   {
        VerifyCodeRequestDto verifyCodeRequestDto = VerifyCodeRequestDto.builder().build();

        this.mockMvc.perform(post(accountURL + "sendCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyCodeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("정상적으로 인증코드 검증 성공")
    void match_code() throws Exception  {
        VerificationCode verificationCode = VerificationCode.builder()
                .email(appProperties.getTestEmail())
                .code("111111")
                .build();

        VerificationCode savedVerificationCode = this.verificationCodeRepository.save(verificationCode);
        VerifyCodeRequestDto verifyCodeRequestDto = modelMapper.map(savedVerificationCode, VerifyCodeRequestDto.class);

        this.mockMvc.perform(put(accountURL + "matchCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyCodeRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("code").value("111111"))
        ;
    }

    @Test
    @DisplayName("인증코드 검증 실패 시 Bad Request")
    void match_code_failed() throws Exception  {
        VerificationCode verificationCode = VerificationCode.builder()
                .email(appProperties.getTestEmail())
                .code("111111")
                .build();

        VerificationCode savedVerificationCode = this.verificationCodeRepository.save(verificationCode);
        VerifyCodeRequestDto verifyCodeRequestDto = modelMapper.map(savedVerificationCode, VerifyCodeRequestDto.class);
        verifyCodeRequestDto.setCode("222222");

        this.mockMvc.perform(put(accountURL + "matchCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyCodeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("인증코드 검증 시 Email과 Code가 null일 때 Bad Request")
    void match_code_failed_by_null() throws Exception  {
        VerificationCode verificationCode = VerificationCode.builder()
                .email(appProperties.getTestEmail())
                .code("111111")
                .build();

        this.verificationCodeRepository.save(verificationCode);
        VerifyCodeRequestDto verifyCodeRequestDto = VerifyCodeRequestDto.builder().build();

        this.mockMvc.perform(put(accountURL + "matchCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyCodeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("인증코드 검증 시 Email이 틀렸을 때 Bad Request")
    void match_code_failed_by_email() throws Exception  {
        VerificationCode verificationCode = VerificationCode.builder()
                .email(appProperties.getTestEmail())
                .code("111111")
                .build();

        VerificationCode savedVerificationCode = this.verificationCodeRepository.save(verificationCode);
        VerifyCodeRequestDto verifyCodeRequestDto = modelMapper.map(savedVerificationCode, VerifyCodeRequestDto.class);
        verifyCodeRequestDto.setEmail("rltjr219@naver.com");

        this.mockMvc.perform(put(accountURL + "matchCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyCodeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("정상적으로 유저를 생성")
    @Disabled
    void saveAccount() throws Exception {
        assert appProperties.getTestEmail() != null;
        assert appProperties.getTestPassword() != null;
        assert appProperties.getTestNickname() != null;

        AccountInitialRequestDto accountDto = AccountInitialRequestDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .oneLineIntroduce("안녕하세요. 저는 취미도 개발, 특기도 개발인 학생 개발자 양기석입니다.")
                .build();

        verifyEmail(appProperties.getTestEmail());

        ResultActions actions = mockMvc.perform(post(accountURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").value(appProperties.getTestNickname()))
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("oneLineIntroduce", is("안녕하세요. 저는 취미도 개발, 특기도 개발인 학생 개발자 양기석입니다.")))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.login-account").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-account",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("login-account").description("link to login"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("사용자가 사용할 이메일"),
                                fieldWithPath("password").description("사용자가 사용할 패스워드"),
                                fieldWithPath("nickName").description("사용자가 사용할 닉네임"),
                                fieldWithPath("oneLineIntroduce").description("사용자의 한 줄 소개")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("nickName").description("사용자 닉네임"),
                                fieldWithPath("socialLink").description("사용자의 소셜 링크"),
                                fieldWithPath("favoriteCount").description("사용자가 받은 좋아요 수"),
                                fieldWithPath("oneLineIntroduce").description("사용자의 한 줄 소개"),
                                fieldWithPath("career").description("사용자의 경력"),
                                fieldWithPath("positions").description("사용자의 역할"),
                                fieldWithPath("image").description("사용자의 프로필 이미지"),
                                fieldWithPath("userRole").description("사용자 권한"),
                                fieldWithPath("createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("experiences").description("사용자의 경력 사항"),
                                fieldWithPath("licenses").description("사용자의 자격증"),
                                fieldWithPath("prizes").description("사용자의 수상 내역"),
                                fieldWithPath("projects").description("사용자의 프로젝트"),
                                fieldWithPath("selfInterviews").description("사용자의 셀프 인터뷰"),
                                fieldWithPath("hits").description("조회 수"),
                                fieldWithPath("technologies").description("사용자의 기술"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;

        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        AccountResource accountResource = objectMapper.readValue(contentAsString, AccountResource.class);
        AccountResponseDto accountResponseDto = accountResource.getContent();

        assertNotNull(accountResponseDto.getId());
        assertEquals(accountResponseDto.getEmail(), appProperties.getTestEmail());
        assertEquals(accountResponseDto.getNickName(), appProperties.getTestNickname());
        assertNotNull(accountResponseDto.getCreatedAt());
    }

    @Test
    @DisplayName("회원 가입시 중복된 email 존재할 경우 Bad Request 반환")
    @Disabled
    void check_duplicated_Account() throws Exception {
        AccountInitialRequestDto accountDto = AccountInitialRequestDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .build();

        verifyEmail(accountDto.getEmail());

        this.mockMvc.perform(post(accountURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        this.mockMvc.perform(post(accountURL + "sendCode")
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "{displayName}{index}")
    @DisplayName("유저 생성 시 email, password, nickname 중 하나라도 공백이 들어올 경우 Bad Request 반환")
    @CsvSource({"'', 'password', '디엔디'", "'user@email.com', '', '디엔디'", "'user@email.com', 'password', ''"})
    void saveAccount_empty_input(String email, String password, String nickName) throws Exception {
        assertNotNull(email);
        assertNotNull(password);
        assertNotNull(nickName);

        AccountInitialRequestDto accountDto = AccountInitialRequestDto.builder()
                .email(email)
                .password(password)
                .nickName(nickName)
                .build();

        mockMvc.perform(post(accountURL)
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @RepeatedTest(value = 3, name = "{displayName} {currentRepetition}")
    @DisplayName("유저 생성 시 email, password, nickname 중 하나라도 null이 들어올 경우 Bad Request 반환")
    void saveAccount_null_input(RepetitionInfo info) throws Exception {
        AccountInitialRequestDto accountDto = AccountInitialRequestDto.builder()
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 유저를 수정")
    void updateAccount() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        String updatedEmail = "update@email.com";
        String updateNickname = "updateNick";
        String oneLineIntroduce = "안녕하세요. 저는 백엔드 개발자를 지망하고 있습니다.";
        String career = "신입";
        Set<String> positions = new HashSet<>(Arrays.asList("BackEnd", "FrontEnd", "Designer"));

        newAccount.setEmail(updatedEmail);
        newAccount.setNickName(updateNickname);
        newAccount.setOneLineIntroduce(oneLineIntroduce);
        newAccount.setCareer(career);
        newAccount.setPositions(positions);

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

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
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("career").exists())
                .andExpect(jsonPath("positions").exists())
                .andExpect(jsonPath("oneLineIntroduce").value(oneLineIntroduce))
                .andExpect(jsonPath("technologies").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.delete-account").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-account",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("delete-account").description("link to delete account"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("사용자가 수정할 이메일"),
                                fieldWithPath("nickName").description("사용자가 수정할 닉네임"),
                                fieldWithPath("oneLineIntroduce").description("사용자가 수정할 한 줄 소개"),
                                fieldWithPath("socialLink").description("사용자의 소셜 링크"),
                                fieldWithPath("career").description("사용자의 경력"),
                                fieldWithPath("positions").description("사용자의 개발 직군"),
                                fieldWithPath("technologies").description("사용자의 기술 리스트")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("수정된 사용자 이메일"),
                                fieldWithPath("nickName").description("수정된 사용자 닉네임"),
                                fieldWithPath("favoriteCount").description("사용자가 받은 좋아요 수"),
                                fieldWithPath("career").description("사용자의 경력"),
                                fieldWithPath("positions").description("사용자의 역할"),
                                fieldWithPath("oneLineIntroduce").description("수정된 사용자의 한 줄 소개"),
                                fieldWithPath("image").description("사용자의 프로필 이미지"),
                                fieldWithPath("userRole").description("사용자 권한"),
                                fieldWithPath("hits").description("조회 수"),
                                fieldWithPath("createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("experiences").description("사용자의 경력 사항"),
                                fieldWithPath("licenses").description("사용자의 자격증"),
                                fieldWithPath("prizes").description("사용자의 수상 내역"),
                                fieldWithPath("projects").description("사용자의 프로젝트"),
                                fieldWithPath("selfInterviews").description("사용자의 셀프 인터뷰"),
                                fieldWithPath("socialLink").description("사용자의 소셜 링크"),
                                fieldWithPath("technologies").description("사용자의 기술 리스트"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 유저의 기술 리스트가 수정되는지 테스트")
    void updateAccount_technology() throws Exception {
        List<Technology> technologyList = technologyRepository.saveAll(Arrays.asList(Technology.builder().id(1L).name("Java").build(), Technology.builder().id(2L).name("Python").build(), Technology.builder().id(3L).name("C").build(), Technology.builder().id(4L).name("C#").build()));
        Account account = Account.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .createdAt(LocalDateTime.now())
                .userRole(UserRole.USER)
                .build();
        Account account1 = accountRepository.save(account);
        jwt = jwtProvider.generateToken(account1);
        Account savedAccount = accountRepository.save(account1);
        AccountRequestDto map = modelMapper.map(savedAccount, AccountRequestDto.class);
        map.setTechnologies(Arrays.asList(technologyList.get(2), technologyList.get(3)));

        mockMvc.perform(put(accountURL + "{accountId}", account1.getId())
                .header(HttpHeaders.AUTHORIZATION,BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("technologies").isNotEmpty());

        mockMvc.perform(get(accountURL + "profile")
                .header(HttpHeaders.AUTHORIZATION,BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("technologies").isNotEmpty());
    }

    @Test
    @DisplayName("정상적으로 유저의 직군 리스트가 수정되는지 테스트")
    void updateAccount_positions() throws Exception {
        Set<String> positions = new HashSet<>(Arrays.asList("백엔드", "프론트엔드"));
        Set<String> updatePositions = new HashSet<>(Arrays.asList("DBA", "UI/UX디자이너"));

        Account first = createAccount_need_index(1);
        Account second = createAccount_need_index(2);

        Account account1 = accountRepository.save(first);
        Account account2 = accountRepository.save(second);

        account1.setPositions(updatePositions);
        account2.setPositions(updatePositions);
        AccountRequestDto firstMap = modelMapper.map(account1, AccountRequestDto.class);
        AccountRequestDto secondMap = modelMapper.map(account2, AccountRequestDto.class);
        jwt = jwtProvider.generateToken(account1);

        mockMvc.perform(put(accountURL + "{accountId}", account1.getId())
                .header(HttpHeaders.AUTHORIZATION,BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstMap)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("positions").exists());

        jwt = jwtProvider.generateToken(account2);
        mockMvc.perform(put(accountURL + "{accountId}", account2.getId())
                .header(HttpHeaders.AUTHORIZATION,BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondMap)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("positions").exists());
    }

    @ParameterizedTest(name = "{displayName}{index}")
    @DisplayName("유저 수정 시 email, password, nickname 중 하나라도 공백이 들어올 경우 Bad Request 반환")
    @CsvSource({"'', '디엔디'", "'user@email.com', ''"})
    void updateAccount_empty_input(String email, String nickName) throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        newAccount.setEmail(email);
        newAccount.setNickName(nickName);

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @RepeatedTest(value = 2, name = "{displayName} {currentRepetition}")
    @DisplayName("유저 수정 시 email, nickname 중 하나라도 null이 들어올 경우 Bad Request 반환")
    void updateAccount_null_input(RepetitionInfo info) throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        int currentRepetition = info.getCurrentRepetition();
        if (currentRepetition == 1) {
            newAccount.setEmail(null);
        } else if (currentRepetition == 2) {
            newAccount.setNickName(null);
        }

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 유저 수정 요청 시 Bad Request 반환")
    void updateAccount_not_fount_user() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        newAccount.setEmail("update@email.com");
        newAccount.setNickName("updateNick");

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", -1)
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 수정 요청 시 Bad Request 반환")
    void updateAccount_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = jwtProvider.generateToken(anotherAccount);

        newAccount.setEmail("update@email.com");
        newAccount.setNickName("updateNick");

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기업 담당자가 유저 수정 요청 시 Forbidden")
    void updateAccount_forbidden() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = jwtProvider.generateToken(anotherAccount);

        newAccount.setEmail("update@email.com");
        newAccount.setNickName("updateNick");

        jwt = createEnterpriseJwt();

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

        mockMvc.perform(put(accountURL + "{accountId}", newAccount.getId())
                .accept(MediaTypes.HAL_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("정상적으로 유저를 삭제")
    void deleteAccount() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        mockMvc.perform(delete(accountURL + "{accountId}", newAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.login-account").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("delete-account",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("login-account").description("link to login"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("삭제된 사용자 이메일"),
                                fieldWithPath("nickName").description("삭제된 사용자 닉네임"),
                                fieldWithPath("favoriteCount").description("사용자가 받은 좋아요 수"),
                                fieldWithPath("oneLineIntroduce").description("삭제된 사용자의 한 줄 소개"),
                                fieldWithPath("image").description("사용자의 프로필 이미지"),
                                fieldWithPath("career").description("사용자의 경력"),
                                fieldWithPath("positions").description("사용자의 역할"),
                                fieldWithPath("userRole").description("사용자 권한"),
                                fieldWithPath("createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("experiences").description("사용자의 경력 사항"),
                                fieldWithPath("licenses").description("사용자의 자격증"),
                                fieldWithPath("prizes").description("사용자의 수상 내역"),
                                fieldWithPath("projects").description("사용자의 프로젝트"),
                                fieldWithPath("selfInterviews").description("사용자의 셀프 인터뷰"),
                                fieldWithPath("socialLink").description("사용자의 소셜 링크"),
                                fieldWithPath("technologies").description("사용자의 기술 리스트"),
                                fieldWithPath("hits").description("조회 수"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 유저 삭제 요청 시 Bad Request 반환")
    void deleteAccount_not_found_user() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);
        mockMvc.perform(delete(accountURL + "{accountId}", -1)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기업 담당자가 유저 삭제 요청 시 Forbidden")
    void deleteAccount_forbidden() throws Exception {
        Account newAccount = createAccount();
        jwt = createEnterpriseJwt();

        mockMvc.perform(delete(accountURL + "{accountId}", newAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("정상적으로 자신의 프로필 조회")
    void getProfile() throws Exception {
        Account newAccount = createAccount();
        jwt = jwtProvider.generateToken(newAccount);

        mockMvc.perform(get(accountURL + "/profile")
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(appProperties.getTestEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(appProperties.getTestNickname())))
                .andExpect(jsonPath("userRole").exists())
                .andExpect(jsonPath("technologies").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("career").exists())
                .andExpect(jsonPath("positions").exists())
                .andExpect(jsonPath("experiences").exists())
                .andExpect(jsonPath("licenses").exists())
                .andExpect(jsonPath("prizes").exists())
                .andExpect(jsonPath("projects").exists())
                .andExpect(jsonPath("selfInterviews").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-account").exists())
                .andExpect(jsonPath("_links.delete-account").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("load-account-profile",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("update-account").description("link to update account"),
                                linkWithRel("delete-account").description("link to delete account")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("nickName").description("사용자 닉네임"),
                                fieldWithPath("socialLink").description("사용자의 소셜링크"),
                                fieldWithPath("favoriteCount").description("사용자가 받은 좋아요 수"),
                                fieldWithPath("oneLineIntroduce").description("사용자의 한 줄 소개"),
                                fieldWithPath("image").description("사용자의 프로필 이미지"),
                                fieldWithPath("career").description("사용자의 경력"),
                                fieldWithPath("positions").description("사용자의 역할"),
                                fieldWithPath("userRole").description("사용자 권한"),
                                fieldWithPath("createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("hits").description("조회 수"),
                                fieldWithPath("experiences").description("사용자의 경력 사항"),
                                fieldWithPath("licenses").description("사용자의 자격증"),
                                fieldWithPath("prizes").description("사용자의 수상 내역"),
                                fieldWithPath("projects").description("사용자의 프로젝트"),
                                fieldWithPath("selfInterviews").description("사용자의 셀프 인터뷰"),
                                fieldWithPath("technologies").description("사용자가 가진 기술스택"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
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
                .andExpect(jsonPath("nickName", is(appProperties.getTestNickname())))
                .andExpect(jsonPath("userRole").exists())
                .andExpect(jsonPath("technologies").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("career").exists())
                .andExpect(jsonPath("positions").exists())
                .andExpect(jsonPath("experiences").exists())
                .andExpect(jsonPath("licenses").exists())
                .andExpect(jsonPath("prizes").exists())
                .andExpect(jsonPath("projects").exists())
                .andExpect(jsonPath("selfInterviews").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("load-account",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("nickName").description("사용자 닉네임"),
                                fieldWithPath("socialLink").description("사용자의 소셜링크"),
                                fieldWithPath("technologies").description("사용자가 가진 기술스택"),
                                fieldWithPath("favoriteCount").description("사용자가 받은 좋아요 수"),
                                fieldWithPath("oneLineIntroduce").description("사용자의 한 줄 소개"),
                                fieldWithPath("career").description("사용자의 경력"),
                                fieldWithPath("positions").description("사용자의 역할"),
                                fieldWithPath("image").description("사용자의 프로필 이미지"),
                                fieldWithPath("userRole").description("사용자 권한"),
                                fieldWithPath("createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("experiences").description("사용자의 경력 사항"),
                                fieldWithPath("hits").description("조회 수"),
                                fieldWithPath("licenses").description("사용자의 자격증"),
                                fieldWithPath("prizes").description("사용자의 수상 내역"),
                                fieldWithPath("projects").description("사용자의 프로젝트"),
                                fieldWithPath("selfInterviews").description("사용자의 셀프 인터뷰"),
                                fieldWithPath("favoriteFlag").description("사용자가 좋아요를 눌렀는지"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 다른 유저 조회")
    void getAnotherAccount() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = jwtProvider.generateToken(anotherAccount);

        mockMvc.perform(get(accountURL + "{accountId}", newAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(appProperties.getTestEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(appProperties.getTestNickname())))
                .andExpect(jsonPath("userRole").exists())
                .andExpect(jsonPath("technologies").exists())
                .andExpect(jsonPath("createdAt").exists())
                .andExpect(jsonPath("career").exists())
                .andExpect(jsonPath("positions").exists())
                .andExpect(jsonPath("experiences").exists())
                .andExpect(jsonPath("licenses").exists())
                .andExpect(jsonPath("prizes").exists())
                .andExpect(jsonPath("projects").exists())
                .andExpect(jsonPath("selfInterviews").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("load-account",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 식별자"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("nickName").description("사용자 닉네임"),
                                fieldWithPath("socialLink").description("사용자의 소셜링크"),
                                fieldWithPath("technologies").description("사용자가 가진 기술스택"),
                                fieldWithPath("favoriteCount").description("사용자가 받은 좋아요 수"),
                                fieldWithPath("oneLineIntroduce").description("사용자의 한 줄 소개"),
                                fieldWithPath("career").description("사용자의 경력"),
                                fieldWithPath("positions").description("사용자의 역할"),
                                fieldWithPath("image").description("사용자의 프로필 이미지"),
                                fieldWithPath("userRole").description("사용자 권한"),
                                fieldWithPath("createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("experiences").description("사용자의 경력 사항"),
                                fieldWithPath("licenses").description("사용자의 자격증"),
                                fieldWithPath("prizes").description("사용자의 수상 내역"),
                                fieldWithPath("hits").description("조회 수"),
                                fieldWithPath("projects").description("사용자의 프로젝트"),
                                fieldWithPath("selfInterviews").description("사용자의 셀프 인터뷰"),
                                fieldWithPath("favoriteFlag").description("사용자가 좋아요를 눌렀는지"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("DB에 없는 사용자 정보 요청시 Bad Request")
    void get_none_account() throws Exception {
        Account account = createAccount();
        jwt = jwtProvider.generateToken(account);

        mockMvc.perform(get(accountURL + "{accountId}", -1)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 모든 유저 조회")
    void getAllAccounts() throws Exception  {
        List<Technology> technologyList = technologyRepository.saveAll(Arrays.asList(Technology.builder().id(1L).name("Java").build(), Technology.builder().id(2L).name("Python").build(), Technology.builder().id(3L).name("C").build(), Technology.builder().id(4L).name("C#").build()));
        IntStream.rangeClosed(1, 30).forEach(i -> {
            try {
                createAccountsWithTech(i, technologyList);
                createEnterpriseDtos(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.mockMvc.perform(get(accountURL)
                .queryParam("orderBy", "favorite"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].id").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].email").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].password").doesNotExist())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].nickName").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].userRole").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].socialLink").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].career").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].positions").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].oneLineIntroduce").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("page.size").exists())
                .andExpect(jsonPath("page.totalElements").exists())
                .andExpect(jsonPath("page.totalPages").exists())
                .andExpect(jsonPath("page.number").exists())
                .andDo(document("load-allAccounts",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("last").description("link to last page")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.accountResponseDtoList[*].id").description("사용자 식별자"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].email").description("사용자 이메일"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].nickName").description("사용자 이름"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].favoriteCount").description("사용자가 받은 좋아요 수"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].oneLineIntroduce").description("사용자의 한 줄 소개"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].image").description("사용자 이미지"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].career").description("사용자의 경력"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].positions").description("사용자의 역할"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].socialLink").description("사용자의 소셜링크"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].userRole").description("사용자 권한"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].hits").description("조회 수"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].experiences").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].licenses").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].prizes").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].technologies[*].id").description("사용자의 기술스택 식별자"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].technologies[*].name").description("사용자의 기술스택 이름"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].projects").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].selfInterviews").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*]._links.self.href").ignored(),
                                fieldWithPath("_links.*.*").ignored(),
                                fieldWithPath("page.size").description("size of page"),
                                fieldWithPath("page.totalElements").description("total elements of pages"),
                                fieldWithPath("page.totalPages").description("total pages"),
                                fieldWithPath("page.number").description("current page number")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 필터링 된 모든 유저 조회")
    void load_filtered_all_accounts() throws Exception  {
        List<Technology> technologyList = technologyRepository.saveAll(Arrays.asList(Technology.builder().id(1L).name("Java").build(), Technology.builder().id(2L).name("Python").build(), Technology.builder().id(3L).name("C").build(), Technology.builder().id(4L).name("C#").build()));
        IntStream.rangeClosed(1, 30).forEach(i -> {
            try {
                createAccountsWithTech(i, technologyList);
                createEnterpriseDtos(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.mockMvc.perform(get(accountURL)
                .queryParam("nickName", "1")
                .queryParam("oneLineIntroduce", "1")
                .queryParam("career", "1년차")
                .queryParam("positions", "개발자")
                .queryParam("technology", "Python")
                .queryParam("orderBy", "hits"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].id").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].email").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].password").doesNotExist())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].nickName").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].userRole").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].socialLink").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].career").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].positions").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].oneLineIntroduce").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("page.size").exists())
                .andExpect(jsonPath("page.totalElements").exists())
                .andExpect(jsonPath("page.totalPages").exists())
                .andExpect(jsonPath("page.number").exists())
                .andDo(document("load-filtered-accounts",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.accountResponseDtoList[*].id").description("사용자 식별자"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].email").description("사용자 이메일"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].nickName").description("사용자 이름"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].favoriteCount").description("사용자가 받은 좋아요 수"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].oneLineIntroduce").description("사용자의 한 줄 소개"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].image").description("사용자 이미지"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].career").description("사용자의 경력"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].positions").description("사용자의 역할"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].socialLink").description("사용자의 소셜링크"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].userRole").description("사용자 권한"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].hits").description("조회 수"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].experiences").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].licenses").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].prizes").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].technologies[*].id").description("사용자의 기술스택 식별자"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].technologies[*].name").description("사용자의 기술스택 이름"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].projects").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].selfInterviews").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*]._links.self.href").ignored(),
                                fieldWithPath("_links.*.*").ignored(),
                                fieldWithPath("page.size").description("size of page"),
                                fieldWithPath("page.totalElements").description("total elements of pages"),
                                fieldWithPath("page.totalPages").description("total pages"),
                                fieldWithPath("page.number").description("current page number")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("필터링 조건에 어떠한 값도 넣지 않을 시")
    void load_accounts_with_any_filter() throws Exception  {
        List<Technology> technologyList = technologyRepository.saveAll(Arrays.asList(Technology.builder().id(1L).name("Java").build(), Technology.builder().id(2L).name("Python").build(), Technology.builder().id(3L).name("C").build(), Technology.builder().id(4L).name("C#").build()));
        IntStream.rangeClosed(1, 30).forEach(i -> {
            try {
                createAccountsWithTech(i, technologyList);
                createEnterpriseDtos(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.mockMvc.perform(get(accountURL))
                .andDo(print())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].id").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].email").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].password").doesNotExist())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].nickName").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].userRole").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].socialLink").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].career").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].positions").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].oneLineIntroduce").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("page.size").exists())
                .andExpect(jsonPath("page.totalElements").exists())
                .andExpect(jsonPath("page.totalPages").exists())
                .andExpect(jsonPath("page.number").exists())
                .andDo(document("load-filtered-accounts-none-value",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("last").description("link to last page")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.accountResponseDtoList[*].id").description("사용자 식별자"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].email").description("사용자 이메일"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].nickName").description("사용자 이름"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].favoriteCount").description("사용자가 받은 좋아요 수"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].oneLineIntroduce").description("사용자의 한 줄 소개"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].image").description("사용자 이미지"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].career").description("사용자의 경력"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].positions").description("사용자의 역할"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].socialLink").description("사용자의 소셜링크"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].userRole").description("사용자 권한"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].hits").description("조회 수"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].experiences").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].licenses").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].prizes").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].technologies[*].id").description("사용자의 기술스택 식별자"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].technologies[*].name").description("사용자의 기술스택 이름"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].projects").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].selfInterviews").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*]._links.self.href").ignored(),
                                fieldWithPath("_links.*.*").ignored(),
                                fieldWithPath("page.size").description("size of page"),
                                fieldWithPath("page.totalElements").description("total elements of pages"),
                                fieldWithPath("page.totalPages").description("total pages"),
                                fieldWithPath("page.number").description("current page number")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("필터링 조건에 해당하는 구직자가 없을 시")
    void load_filtered_accounts_empty() throws Exception  {
        List<Technology> technologyList = technologyRepository.saveAll(Arrays.asList(Technology.builder().id(1L).name("Java").build(), Technology.builder().id(2L).name("Python").build(), Technology.builder().id(3L).name("C").build(), Technology.builder().id(4L).name("C#").build()));
        IntStream.rangeClosed(1, 30).forEach(i -> {
            try {
                createAccountsWithTech(i, technologyList);
                createEnterpriseDtos(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.mockMvc.perform(get(accountURL)
                .queryParam("nickName", "kiseok")
                .queryParam("oneLineIntroduce", "I'm kiseok")
                .queryParam("career", "신입")
                .queryParam("positions", "구직자")
                .queryParam("technology", "vim"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("page.size").exists())
                .andExpect(jsonPath("page.totalElements").exists())
                .andExpect(jsonPath("page.totalPages").exists())
                .andExpect(jsonPath("page.number").exists())
                .andDo(document("load-filtered-accounts-none",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("_links.*.*").ignored(),
                                fieldWithPath("page.size").description("size of page"),
                                fieldWithPath("page.totalElements").description("total elements of pages"),
                                fieldWithPath("page.totalPages").description("total pages"),
                                fieldWithPath("page.number").description("current page number")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 좋아요 생성")
    void favorite() throws Exception {
        Account account = createAccount();

        mockMvc.perform(post(accountURL + "{accountId}/favorite", account.getId())
                .header(HttpHeaders.AUTHORIZATION, createAccountJwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(account.getEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(account.getNickName())))
                .andExpect(jsonPath("favoriteCount", is(1)));
    }

    @Test
    @DisplayName("정상적으로 좋아요 삭제(좋아요 개수 1 -> 0)")
    void favorite_remove() throws Exception {
        Account account = createAccount();
        String anotherAccountJwt = createAccountJwt();
        mockMvc.perform(post(accountURL + "{accountId}/favorite", account.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherAccountJwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(account.getEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(account.getNickName())))
                .andExpect(jsonPath("favoriteCount", is(1)))
                .andExpect(jsonPath("favoriteFlag", is(true)));

        mockMvc.perform(post(accountURL + "/{accountId}/favorite", account.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherAccountJwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(account.getEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(account.getNickName())))
                .andExpect(jsonPath("favoriteCount", is(0)))
                .andExpect(jsonPath("favoriteFlag", is(false)));
    }

    @Test
    @DisplayName("좋아요를 누를 유저가 존재하지 않을 때")
    void favorite_not_found() throws Exception {
        mockMvc.perform(post(accountURL + "{accountId}/favorite", -1)
                .header(HttpHeaders.AUTHORIZATION, createAccountJwt()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("좋아요를 누른 유저 조회")
    void getFavoriteUser() throws Exception {
        Account account = createAccount();
        Account secondAccount = accountRepository.save(Account.builder()
                .email("sangyeop@email.com")
                .password(passwordEncoder.encode("password"))
                .nickName("sangyeopzzangzzang")
                .userRole(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build());
        String firstAccountjwt = createAccountJwt();
        String secondJwt = jwtProvider.generateToken(secondAccount);

        mockMvc.perform(post(accountURL + "{accountId}/favorite", account.getId())
                .header(HttpHeaders.AUTHORIZATION, firstAccountjwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(account.getEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(account.getNickName())))
                .andExpect(jsonPath("favoriteCount", is(1)));

        mockMvc.perform(post(accountURL + "{accountId}/favorite", account.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + secondJwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(account.getEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(account.getNickName())))
                .andExpect(jsonPath("favoriteCount", is(2)));

        mockMvc.perform(get(accountURL + "{accountId}/favorite", account.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("[*].id").exists())
                .andExpect(jsonPath("[*].email").exists())
                .andExpect(jsonPath("[*].image").exists());
    }

    @Test
    @DisplayName("좋아요 순으로 정렬하여 반환")
    void orderByFavorite() throws Exception {
        IntStream.rangeClosed(1, 4).forEach(this::createAccount_need_index);

        List<Account> all = accountRepository.findAll();
        assertEquals(all.size(), 4);

        Account first = all.get(0);
        Account second = all.get(1);
        Account third = all.get(2);
        Account fourth = all.get(3);

        String firstJwt = BEARER + jwtProvider.generateToken(first);
        String secondJwt = BEARER + jwtProvider.generateToken(second);
        String thirdJwt = BEARER + jwtProvider.generateToken(third);
        String fourthJwt = BEARER + jwtProvider.generateToken(fourth);

        List<String> jwtList = Arrays.asList(firstJwt, secondJwt, thirdJwt, fourthJwt);

        jwtList.stream().limit(3).forEach(index -> {
            try {
                mockMvc.perform(post(accountURL + "{accountId}/favorite", fourth.getId())
                        .header(HttpHeaders.AUTHORIZATION, index))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("id").exists())
                        .andExpect(jsonPath("email", is(fourth.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("nickName", is(fourth.getNickName())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        jwtList.stream().limit(2).forEach(index -> {
            try {
                mockMvc.perform(post(accountURL + "{accountId}/favorite", third.getId())
                        .header(HttpHeaders.AUTHORIZATION, index))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("id").exists())
                        .andExpect(jsonPath("email", is(third.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("nickName", is(third.getNickName())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        jwtList.stream().limit(1).forEach(index -> {
            try {
                mockMvc.perform(post(accountURL + "{accountId}/favorite", second.getId())
                        .header(HttpHeaders.AUTHORIZATION, index))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("id").exists())
                        .andExpect(jsonPath("email", is(second.getEmail())))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("nickName", is(second.getNickName())))
                        .andExpect(jsonPath("favoriteCount", is(1)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        List<Account> accountList = accountRepository.findAll();
        int fourthFavoriteCount = accountList.get(3).getFavorite().size();
        int thirdFavoriteCount = accountList.get(2).getFavorite().size();
        int secondFavoriteCount = accountList.get(1).getFavorite().size();
        int firstFavoriteCount = accountList.get(0).getFavorite().size();

        assertEquals(fourthFavoriteCount, 3);
        assertEquals(thirdFavoriteCount, 2);
        assertEquals(secondFavoriteCount, 1);
        assertEquals(firstFavoriteCount, 0);

        mockMvc.perform(get(accountURL)
                        .queryParam("orderBy", "favorite"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].id").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].email").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].password").doesNotExist())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].nickName").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].userRole").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].socialLink").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*].oneLineIntroduce").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[*]._links.self").exists())
                .andExpect(jsonPath("_embedded.accountResponseDtoList[0].favoriteCount", is(3)))
                .andExpect(jsonPath("_embedded.accountResponseDtoList[1].favoriteCount", is(2)))
                .andExpect(jsonPath("_embedded.accountResponseDtoList[2].favoriteCount", is(1)))
                .andExpect(jsonPath("_embedded.accountResponseDtoList[3].favoriteCount", is(0)));
    }

    @Test
    @DisplayName("같은 유저가 다른 유저를 여러번 조회해도 다른 유저의 조회 수는 1인지 테스트")
    void getAnotherAccount_hits() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        Account thirdAccount = accountRepository.save(
                        Account.builder()
                        .email("test10@email.com")
                        .password(appProperties.getTestPassword())
                        .nickName(appProperties.getTestNickname())
                        .createdAt(LocalDateTime.now())
                        .career("신입")
                        .positions(new HashSet<>(Arrays.asList("BackEnd", "FrontEnd")))
                        .userRole(UserRole.USER)
                        .build());
        jwt = BEARER + jwtProvider.generateToken(anotherAccount);

        //유저1을 한번 조회했을 경우
        mockMvc.perform(get(accountURL + "{accountId}", newAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(appProperties.getTestEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(appProperties.getTestNickname())))
                .andExpect(jsonPath("userRole").exists())
                .andExpect(jsonPath("technologies").exists())
                .andExpect(jsonPath("hits", is(1)))
                .andExpect(cookie().exists("cookie" + newAccount.getId()));

        //유저1을 두번 조회했을 경우
        mockMvc.perform(get(accountURL + "{accountId}", newAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .cookie(new Cookie("cookie" + newAccount.getId(), "|" + newAccount.getId() + "|")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(appProperties.getTestEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(appProperties.getTestNickname())))
                .andExpect(jsonPath("userRole").exists())
                .andExpect(jsonPath("technologies").exists())
                .andExpect(jsonPath("hits", is(1)));

        //유저3을 한번 조회했을 경우
        mockMvc.perform(get(accountURL + "{accountId}", thirdAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(thirdAccount.getEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(thirdAccount.getNickName())))
                .andExpect(jsonPath("userRole").exists())
                .andExpect(jsonPath("technologies").exists())
                .andExpect(jsonPath("hits", is(1)))
                .andExpect(cookie().exists("cookie" + thirdAccount.getId()));

        //유저3을 두번 조회했을 경우
        mockMvc.perform(get(accountURL + "{accountId}", thirdAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .cookie(new Cookie("cookie" + thirdAccount.getId(), "|" + thirdAccount.getId() + "|")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(thirdAccount.getEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(thirdAccount.getNickName())))
                .andExpect(jsonPath("userRole").exists())
                .andExpect(jsonPath("technologies").exists())
                .andExpect(jsonPath("hits", is(1)));

        //유저1을 다시 조회했을 경우
        mockMvc.perform(get(accountURL + "{accountId}", newAccount.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .cookie(new Cookie("cookie" + newAccount.getId(), "|" + newAccount.getId() + "|")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(appProperties.getTestEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(appProperties.getTestNickname())))
                .andExpect(jsonPath("userRole").exists())
                .andExpect(jsonPath("technologies").exists())
                .andExpect(jsonPath("hits", is(1)));
    }

    protected Account createAccount_need_index(int index) {
        Account account = Account.builder()
                .email("test" + index + "@email.com")
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .createdAt(LocalDateTime.now())
                .userRole(UserRole.USER)
                .build();
        return accountRepository.save(account);
    }
}