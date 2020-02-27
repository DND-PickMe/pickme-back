package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseResponseDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.resource.EnterpriseResource;
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
import static com.pickmebackend.error.ErrorMessageConstant.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EnterpriseControllerTest extends BaseControllerTest {

    private String enterpriseURL = "/api/enterprises/";

    private final String BEARER = "Bearer ";

    @AfterEach
    void setUp() {
        enterpriseRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 자신의 프로필 조회")
    void load_profile() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        mockMvc.perform(get(enterpriseURL + "/profile")
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("registrationNumber").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("address").exists())
                .andExpect(jsonPath("ceoName").exists())
                .andExpect(jsonPath("account").exists())
                .andExpect(jsonPath("account.userRole", is("ENTERPRISE")))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-enterprise").exists())
                .andExpect(jsonPath("_links.delete-enterprise").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("load-enterprise",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-enterprise").description("link to update enterprise"),
                                linkWithRel("delete-enterprise").description("link to delete enterprise"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("기업 식별자"),
                                fieldWithPath("email").description("기업 담당자 이메일"),
                                fieldWithPath("registrationNumber").description("사업자 등록 번호"),
                                fieldWithPath("name").description("기업 명"),
                                fieldWithPath("address").description("회사 주소"),
                                fieldWithPath("ceoName").description("ceo 이름"),
                                fieldWithPath("account.id").description("기업 담당자 식별자"),
                                fieldWithPath("account.email").ignored(),
                                fieldWithPath("account.nickName").ignored(),
                                fieldWithPath("account.favorite").ignored(),
                                fieldWithPath("account.positions").ignored(),
                                fieldWithPath("account.userRole").description("기업 담당자 권한"),
                                fieldWithPath("account.career").ignored(),
                                fieldWithPath("account.createdAt").description("기업 담당자 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").ignored(),
                                fieldWithPath("account.image").ignored(),
                                fieldWithPath("account.socialLink").ignored(),
                                fieldWithPath("account.enterprise.*").ignored(),
                                fieldWithPath("account.experiences").ignored(),
                                fieldWithPath("account.licenses").ignored(),
                                fieldWithPath("account.prizes").ignored(),
                                fieldWithPath("account.projects").ignored(),
                                fieldWithPath("account.selfInterviews").ignored(),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("정상적으로 한명의 기업담당자 불러오기")
    void load_enterprise() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        this.mockMvc.perform(get(enterpriseURL + "{enterpriseId}", account.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("registrationNumber").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("address").exists())
                .andExpect(jsonPath("ceoName").exists())
                .andExpect(jsonPath("account").exists())
                .andExpect(jsonPath("account.userRole", is("ENTERPRISE")))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("load-enterprise",
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
                                fieldWithPath("id").description("기업 식별자"),
                                fieldWithPath("email").description("기업 담당자 이메일"),
                                fieldWithPath("registrationNumber").description("사업자 등록 번호"),
                                fieldWithPath("name").description("기업 명"),
                                fieldWithPath("address").description("회사 주소"),
                                fieldWithPath("ceoName").description("ceo 이름"),
                                fieldWithPath("account.id").description("기업 담당자 식별자"),
                                fieldWithPath("account.email").ignored(),
                                fieldWithPath("account.nickName").ignored(),
                                fieldWithPath("account.favorite").ignored(),
                                fieldWithPath("account.positions").ignored(),
                                fieldWithPath("account.userRole").description("기업 담당자 권한"),
                                fieldWithPath("account.career").ignored(),
                                fieldWithPath("account.createdAt").description("기업 담당자 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").ignored(),
                                fieldWithPath("account.image").ignored(),
                                fieldWithPath("account.socialLink").ignored(),
                                fieldWithPath("account.enterprise.*").ignored(),
                                fieldWithPath("account.experiences").ignored(),
                                fieldWithPath("account.licenses").ignored(),
                                fieldWithPath("account.prizes").ignored(),
                                fieldWithPath("account.projects").ignored(),
                                fieldWithPath("account.selfInterviews").ignored(),
                                fieldWithPath("_links.*.*").ignored()
                        )
                        ))
        ;
    }

    @Test
    @DisplayName("정상적으로 다른 기업담당자 불러오기")
    void load_another_enterprise() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        EnterpriseRequestDto enterpriseAnotherRequestDto = createAnotherEnterpriseDto();
        Optional<Account> accountAnotherOptional = accountRepository.findByEmail(enterpriseAnotherRequestDto.getEmail());
        Account anotherAccount = accountAnotherOptional.get();

        jwt = jwtProvider.generateToken(anotherAccount);

        this.mockMvc.perform(get(enterpriseURL + "{enterpriseId}", account.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("registrationNumber").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("address").exists())
                .andExpect(jsonPath("ceoName").exists())
                .andExpect(jsonPath("account").exists())
                .andExpect(jsonPath("account.userRole", is("ENTERPRISE")))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("load-enterprise",
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
                                fieldWithPath("id").description("기업 식별자"),
                                fieldWithPath("email").description("기업 담당자 이메일"),
                                fieldWithPath("registrationNumber").description("사업자 등록 번호"),
                                fieldWithPath("name").description("기업 명"),
                                fieldWithPath("address").description("회사 주소"),
                                fieldWithPath("ceoName").description("ceo 이름"),
                                fieldWithPath("account.id").description("기업 담당자 식별자"),
                                fieldWithPath("account.email").ignored(),
                                fieldWithPath("account.nickName").ignored(),
                                fieldWithPath("account.favorite").ignored(),
                                fieldWithPath("account.positions").ignored(),
                                fieldWithPath("account.userRole").description("기업 담당자 권한"),
                                fieldWithPath("account.career").ignored(),
                                fieldWithPath("account.createdAt").description("기업 담당자 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").ignored(),
                                fieldWithPath("account.image").ignored(),
                                fieldWithPath("account.socialLink").ignored(),
                                fieldWithPath("account.enterprise.*").ignored(),
                                fieldWithPath("account.experiences").ignored(),
                                fieldWithPath("account.licenses").ignored(),
                                fieldWithPath("account.prizes").ignored(),
                                fieldWithPath("account.projects").ignored(),
                                fieldWithPath("account.selfInterviews").ignored(),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("저장되어 있지 않은 기업담당자 불러 오기")
    void load_non_enterprise() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
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
        EnterpriseRequestDto enterpriseRequestDto = EnterpriseRequestDto.builder()
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
                .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestEmail()))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("registrationNumber").value(appProperties.getTestRegistrationNumber()))
                .andExpect(jsonPath("name").value(appProperties.getTestName()))
                .andExpect(jsonPath("address").value(appProperties.getTestAddress()))
                .andExpect(jsonPath("ceoName").value(appProperties.getTestCeoName()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.login-enterprise").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-enterprise",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("login-enterprise").description("link to login"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("기업 담당자가 사용할 이메일"),
                                fieldWithPath("password").description("기업 담당자가 사용할 패스워드"),
                                fieldWithPath("registrationNumber").description("사업자 등록 번호"),
                                fieldWithPath("name").description("회사 이름"),
                                fieldWithPath("address").description("회사 주소"),
                                fieldWithPath("ceoName").description("회사 사장 이름")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("기업 담당자 식별자"),
                                fieldWithPath("email").description("기업 담당자가 사용할 이메일"),
                                fieldWithPath("registrationNumber").description("사업자 등록 번호"),
                                fieldWithPath("name").description("회사 이름"),
                                fieldWithPath("address").description("회사 주소"),
                                fieldWithPath("ceoName").description("회사 사장 이름"),
                                fieldWithPath("account.id").description("사용자 식별자"),
                                fieldWithPath("account.email").description("사용자 이메일"),
                                fieldWithPath("account.nickName").description("회사 이름"),
                                fieldWithPath("account.favorite").ignored(),
                                fieldWithPath("account.userRole").description("사용자 권한"),
                                fieldWithPath("account.createdAt").description("사용자 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").ignored(),
                                fieldWithPath("account.image").ignored(),

                                fieldWithPath("account.enterprise.id").ignored(),
                                fieldWithPath("account.enterprise.registrationNumber").ignored(),
                                fieldWithPath("account.enterprise.name").ignored(),
                                fieldWithPath("account.enterprise.address").ignored(),
                                fieldWithPath("account.enterprise.ceoName").ignored(),
                                fieldWithPath("account.enterprise.account").ignored(),

                                fieldWithPath("account.experiences").ignored(),
                                fieldWithPath("account.licenses").ignored(),
                                fieldWithPath("account.prizes").ignored(),
                                fieldWithPath("account.projects").ignored(),
                                fieldWithPath("account.selfInterviews").ignored(),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;

        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        EnterpriseResource enterpriseResource = objectMapper.readValue(contentAsString, EnterpriseResource.class);
        EnterpriseResponseDto enterpriseResponseDto = enterpriseResource.getContent();
        Account account = accountRepository.findByEmail(enterpriseResponseDto.getEmail()).get();
        Enterprise enterprise = enterpriseRepository.findById(enterpriseResponseDto.getId()).get();


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
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        this.mockMvc.perform(post(enterpriseURL)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(DUPLICATEDUSER)))
        ;
    }

    @ParameterizedTest
    @DisplayName("기업 담당자 저장시 하나의 필드라도 공백이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForEmptyStringCheck")
    void check_save_empty_input_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = EnterpriseRequestDto.builder()
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
                        .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
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
        EnterpriseRequestDto enterpriseRequestDto = EnterpriseRequestDto.builder()
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
                .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
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
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        enterpriseRequestDto.setEmail("newEmail@email.com");
        enterpriseRequestDto.setPassword("newPassword");
        enterpriseRequestDto.setRegistrationNumber("newRegistrationNumber");
        enterpriseRequestDto.setName("newName");
        enterpriseRequestDto.setAddress("newAddress");
        enterpriseRequestDto.setCeoName("newCeoName");

        ResultActions resultActions = this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", account.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value("newEmail@email.com"))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("registrationNumber").value("newRegistrationNumber"))
                .andExpect(jsonPath("name").value("newName"))
                .andExpect(jsonPath("address").value("newAddress"))
                .andExpect(jsonPath("ceoName").value("newCeoName"))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.delete-enterprise").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-enterprise",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("delete-enterprise").description("link to delete enterprise"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("수정할 기업 담당자가 사용할 이메일"),
                                fieldWithPath("password").description("수정할 기업 담당자가 사용할 패스워드"),
                                fieldWithPath("registrationNumber").description("수정할 사업자 등록 번호"),
                                fieldWithPath("name").description("수정할 회사 이름"),
                                fieldWithPath("address").description("수정할 회사 주소"),
                                fieldWithPath("ceoName").description("수정할 회사 사장 이름")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("수정할 기업 담당자 식별자"),
                                fieldWithPath("email").description("수정할 기업 담당자가 사용할 이메일"),
                                fieldWithPath("registrationNumber").description("수정할 사업자 등록 번호"),
                                fieldWithPath("name").description("수정할 회사 이름"),
                                fieldWithPath("address").description("수정할 회사 주소"),
                                fieldWithPath("ceoName").description("수정할 회사 사장 이름"),
                                fieldWithPath("account.id").description("수정할 사용자 식별자"),
                                fieldWithPath("account.email").description("수정할 사용자 이메일"),
                                fieldWithPath("account.nickName").description("수정할 회사 이름"),
                                fieldWithPath("account.favorite").ignored(),
                                fieldWithPath("account.userRole").description("수정할 사용자 권한"),
                                fieldWithPath("account.createdAt").description("수정할 사용자 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").ignored(),
                                fieldWithPath("account.image").ignored(),

                                fieldWithPath("account.enterprise.id").ignored(),
                                fieldWithPath("account.enterprise.registrationNumber").ignored(),
                                fieldWithPath("account.enterprise.name").ignored(),
                                fieldWithPath("account.enterprise.address").ignored(),
                                fieldWithPath("account.enterprise.ceoName").ignored(),
                                fieldWithPath("account.enterprise.account").ignored(),

                                fieldWithPath("account.experiences").ignored(),
                                fieldWithPath("account.licenses").ignored(),
                                fieldWithPath("account.prizes").ignored(),
                                fieldWithPath("account.projects").ignored(),
                                fieldWithPath("account.selfInterviews").ignored(),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        EnterpriseResource enterpriseResource = objectMapper.readValue(contentAsString, EnterpriseResource.class);
        EnterpriseResponseDto enterpriseResponseDto = enterpriseResource.getContent();
        Account modifiedAccount = accountRepository.findByEmail(enterpriseResponseDto.getEmail()).get();
        Enterprise modifiedEnterprise = enterpriseRepository.findById(enterpriseResponseDto.getId()).get();

        assertNotNull(modifiedAccount.getId());
        assertEquals(modifiedAccount.getEmail(), "newEmail@email.com");
        assertEquals(modifiedAccount.getNickName(), "newName");
        assertEquals(modifiedAccount.getUserRole(), UserRole.ENTERPRISE);
        assertNotNull(modifiedAccount.getCreatedAt());
        assertEquals(modifiedAccount.getEnterprise(), modifiedEnterprise);

        assertNotNull(modifiedEnterprise.getId());
        assertEquals(modifiedEnterprise.getRegistrationNumber(), "newRegistrationNumber");
        assertEquals(modifiedEnterprise.getName(), "newName");
        assertEquals(modifiedEnterprise.getAddress(), "newAddress");
        assertEquals(modifiedEnterprise.getCeoName(), "newCeoName");
    }

    @Test
    @DisplayName("수정시 존재 하지 않는 기업 담당자 수정 할 경우 Bad Request 반환")
    void update_non_enterprise() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        enterpriseRequestDto.setEmail("newEmail@email.com");
        enterpriseRequestDto.setPassword("newPassword");
        enterpriseRequestDto.setRegistrationNumber("newRegistrationNumber");
        enterpriseRequestDto.setName("newName");
        enterpriseRequestDto.setAddress("newAddress");
        enterpriseRequestDto.setCeoName("newCeoName");

        this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", -1)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @DisplayName("수정할 권한이 없는 기업 담당자 수정 할 경우 Bad Request 반환")
    void update_unAuthorized_enterprise() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        EnterpriseRequestDto anotherEnterpriseRequestDto = createAnotherEnterpriseDto();
        Optional<Account> anotherAccountOptional = accountRepository.findByEmail(anotherEnterpriseRequestDto.getEmail());
        Account anotherAccount = anotherAccountOptional.get();

        jwt = jwtProvider.generateToken(anotherAccount);

        enterpriseRequestDto.setEmail("newEmail@email.com");
        enterpriseRequestDto.setPassword("newPassword");
        enterpriseRequestDto.setRegistrationNumber("newRegistrationNumber");
        enterpriseRequestDto.setName("newName");
        enterpriseRequestDto.setAddress("newAddress");
        enterpriseRequestDto.setCeoName("newCeoName");

        this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", account.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)))
        ;
    }

    @Test
    @DisplayName("일반 유저가 기업 담당자 수정 할 경우 Forbidden")
    void update_enterprise_forbidden() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        enterpriseRequestDto.setEmail("newEmail@email.com");
        enterpriseRequestDto.setPassword("newPassword");
        enterpriseRequestDto.setRegistrationNumber("newRegistrationNumber");
        enterpriseRequestDto.setName("newName");
        enterpriseRequestDto.setAddress("newAddress");
        enterpriseRequestDto.setCeoName("newCeoName");

        jwt = createAccountJwt();

        this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", account.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @ParameterizedTest
    @DisplayName("기업 담당자 수정시 하나의 필드라도 공백이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForEmptyStringCheck")
    void check_update_empty_string_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        enterpriseRequestDto.setEmail(email);
        enterpriseRequestDto.setPassword(password);
        enterpriseRequestDto.setRegistrationNumber(registrationNumber);
        enterpriseRequestDto.setName(name);
        enterpriseRequestDto.setAddress(address);
        enterpriseRequestDto.setCeoName(ceoName);

        this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", account.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
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
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        enterpriseRequestDto.setEmail(email);
        enterpriseRequestDto.setPassword(password);
        enterpriseRequestDto.setRegistrationNumber(registrationNumber);
        enterpriseRequestDto.setName(name);
        enterpriseRequestDto.setAddress(address);
        enterpriseRequestDto.setCeoName(ceoName);

        this.mockMvc.perform(put(enterpriseURL + "{enterpriseId}", account.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseRequestDto)))
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
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        this.mockMvc.perform(delete(enterpriseURL + "{enterpriseId}", account.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.login-enterprise").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("delete-enterprise",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("login-enterprise").description("link to login"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("삭제할 기업 담당자 식별자"),
                                fieldWithPath("email").description("삭제할 기업 담당자가 사용할 이메일"),
                                fieldWithPath("registrationNumber").description("삭제할 사업자 등록 번호"),
                                fieldWithPath("name").description("삭제할 회사 이름"),
                                fieldWithPath("address").description("삭제할 회사 주소"),
                                fieldWithPath("ceoName").description("삭제할 회사 사장 이름"),
                                fieldWithPath("account.id").description("삭제할 사용자 식별자"),
                                fieldWithPath("account.email").description("삭제할 사용자 이메일"),
                                fieldWithPath("account.nickName").description("삭제할 회사 이름"),
                                fieldWithPath("account.userRole").description("삭제할 사용자 권한"),
                                fieldWithPath("account.createdAt").description("삭제할 사용자 생성 날짜")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 기업담당자 삭제 요청시 Bad Request 반환")
    void delete_non_enterprise() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = jwtProvider.generateToken(account);

        this.mockMvc.perform(delete(enterpriseURL + "{enterpriseId}", -1)
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }

    @Test
    @DisplayName("삭제할 권한이 없는 기업담당자 삭제 요청시 Bad Request 반환")
    void delete_unAuthorized_enterprise() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        EnterpriseRequestDto anotherEnterpriseRequestDto = createAnotherEnterpriseDto();
        Optional<Account> anotherAccountOptional = accountRepository.findByEmail(anotherEnterpriseRequestDto.getEmail());
        Account anotherAccount = anotherAccountOptional.get();

        jwt = jwtProvider.generateToken(anotherAccount);

        this.mockMvc.perform(delete(enterpriseURL + "{enterpriseId}", account.getId())
                .header(HttpHeaders.AUTHORIZATION, BEARER + jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)))
        ;
    }

    @Test
    @DisplayName("일반 유저가 기업담당자 삭제 요청시 Forbidden")
    void delete_enterprise_forbidden() throws Exception {
        EnterpriseRequestDto enterpriseRequestDto = createEnterpriseDto();
        Optional<Account> accountOptional = accountRepository.findByEmail(enterpriseRequestDto.getEmail());
        Account account = accountOptional.get();

        jwt = createAccountJwt();

        this.mockMvc.perform(delete(enterpriseURL + "{enterpriseId}", account.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isForbidden())
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