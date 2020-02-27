package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.AccountTech;
import com.pickmebackend.domain.Technology;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.dto.account.AccountResponseDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.repository.AccountTechRepository;
import com.pickmebackend.repository.TechnologyRepository;
import com.pickmebackend.resource.AccountResource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import static com.pickmebackend.error.ErrorMessageConstant.*;
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
    }

    @Test
    @DisplayName("정상적으로 유저를 생성")
    void saveAccount() throws Exception {
        assert appProperties.getTestEmail() != null;
        assert appProperties.getTestPassword() != null;
        assert appProperties.getTestNickname() != null;

        List<Technology> technologyList = Arrays.asList(Technology.builder().name("Java").build(), Technology.builder().name("Python").build());
        List<Technology> savedTechnologyList = technologyRepository.saveAll(technologyList);
        assertEquals(technologyRepository.findAll().size(), 2);

        AccountRequestDto accountDto = AccountRequestDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .socialLink("https://github.com/mkshin96")
                .oneLineIntroduce("안녕하세요. 저는 취미도 개발, 특기도 개발인 학생 개발자 양기석입니다.")
                .technologies(savedTechnologyList)
                .build();

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
                .andExpect(jsonPath("technologies").exists())
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
                                fieldWithPath("socialLink").description("사용자의 소셜 링크"),
                                fieldWithPath("oneLineIntroduce").description("사용자의 한 줄 소개"),
                                fieldWithPath("technologies[*].id").description("사용자의 기술 식별자"),
                                fieldWithPath("technologies[*].name").description("사용자의 기술 이름")
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
                                fieldWithPath("image").description("사용자의 프로필 이미지"),
                                fieldWithPath("userRole").description("사용자 권한"),
                                fieldWithPath("createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("experiences").description("사용자의 경력 사항"),
                                fieldWithPath("licenses").description("사용자의 자격증"),
                                fieldWithPath("prizes").description("사용자의 수상 내역"),
                                fieldWithPath("projects").description("사용자의 프로젝트"),
                                fieldWithPath("selfInterviews").description("사용자의 셀프 인터뷰"),
                                fieldWithPath("technologies[*].id").description("사용자의 기술 식별자"),
                                fieldWithPath("technologies[*].name").description("사용자의 기술 이름"),
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

        List<AccountTech> allAccountTech = accountTechRepository.findAllByAccount_Id(accountResponseDto.getId());

        AccountTech accountTech = allAccountTech.get(0);
        assertEquals(accountTech.getTechnology().getName(), "Java");
    }

    @Test
    @DisplayName("회원 가입시 중복된 email 존재할 경우 Bad Request 반환")
    void check_duplicated_Account() throws Exception {
        AccountRequestDto accountDto = AccountRequestDto.builder()
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

        AccountRequestDto accountDto = AccountRequestDto.builder()
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
        AccountRequestDto accountDto = AccountRequestDto.builder()
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

        newAccount.setEmail(updatedEmail);
        newAccount.setNickName(updateNickname);
        newAccount.setOneLineIntroduce(oneLineIntroduce);

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
                                fieldWithPath("password").description("사용자가 수정할 패스워드"),
                                fieldWithPath("nickName").description("사용자가 수정할 닉네임"),
                                fieldWithPath("oneLineIntroduce").description("사용자가 수정할 한 줄 소개"),
                                fieldWithPath("socialLink").description("사용자의 소셜 링크"),
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
                                fieldWithPath("oneLineIntroduce").description("수정된 사용자의 한 줄 소개"),
                                fieldWithPath("image").description("사용자의 프로필 이미지"),
                                fieldWithPath("userRole").description("사용자 권한"),
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
        List<Technology> technologyList = technologyRepository.saveAll(Arrays.asList(Technology.builder().name("Java").build(), Technology.builder().name("Python").build(), Technology.builder().name("C").build(), Technology.builder().name("C#").build()));
        Account account = Account.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .nickName(appProperties.getTestNickname())
                .createdAt(LocalDateTime.now())
                .userRole(UserRole.USER)
                .build();
        Account account1 = accountRepository.save(account);
        jwt = jwtProvider.generateToken(account1);
        Set<AccountTech> set = new HashSet<>();
        set.add(AccountTech.builder().account(account1).technology(technologyList.get(0)).build());
        set.add(AccountTech.builder().account(account1).technology(technologyList.get(1)).build());
        account.setAccountTechSet(set);
        Account savedAccount = accountRepository.save(account);
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
                .andExpect(jsonPath("technologies").exists());
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

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

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

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

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

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

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

        AccountRequestDto updateAccountDto = modelMapper.map(newAccount, AccountRequestDto.class);

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
                                fieldWithPath("userRole").description("사용자 권한"),
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
                .andExpect(jsonPath("experiences").exists())
                .andExpect(jsonPath("licenses").exists())
                .andExpect(jsonPath("prizes").exists())
                .andExpect(jsonPath("projects").exists())
                .andExpect(jsonPath("selfInterviews").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-account").exists())
                .andExpect(jsonPath("_links.delete-account").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("load-profile",
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
                                fieldWithPath("userRole").description("사용자 권한"),
                                fieldWithPath("createdAt").description("사용자 생성 날짜"),
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
                                fieldWithPath("image").description("사용자의 프로필 이미지"),
                                fieldWithPath("userRole").description("사용자 권한"),
                                fieldWithPath("createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("experiences").description("사용자의 경력 사항"),
                                fieldWithPath("licenses").description("사용자의 자격증"),
                                fieldWithPath("prizes").description("사용자의 수상 내역"),
                                fieldWithPath("projects").description("사용자의 프로젝트"),
                                fieldWithPath("selfInterviews").description("사용자의 셀프 인터뷰"),
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists())
        ;
    }

    @Test
    @DisplayName("정상적으로 모든 유저 조회")
    void getAllAccounts() throws Exception  {
        IntStream.rangeClosed(1, 30).forEach(this::createAccounts);

        this.mockMvc.perform(get(accountURL))
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
                                fieldWithPath("_embedded.accountResponseDtoList[*].socialLink").description("사용자의 소셜링크"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].userRole").description("사용자 권한"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("_embedded.accountResponseDtoList[*].experiences").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].licenses").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].prizes").ignored(),
                                fieldWithPath("_embedded.accountResponseDtoList[*].technologies").description("사용자의 기술스택"),
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
                .andExpect(jsonPath("favoriteCount", is(1)));

        mockMvc.perform(post(accountURL + "/{accountId}/favorite", account.getId())
                .header(HttpHeaders.AUTHORIZATION, anotherAccountJwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email", is(account.getEmail())))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("nickName", is(account.getNickName())))
                .andExpect(jsonPath("favoriteCount", is(0)));
    }

    @Test
    @DisplayName("좋아요를 누를 유저가 존재하지 않을 때")
    void favorite_not_found() throws Exception {
        mockMvc.perform(post(accountURL + "{accountId}/favorite", -1)
                .header(HttpHeaders.AUTHORIZATION, createAccountJwt()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)));
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
}