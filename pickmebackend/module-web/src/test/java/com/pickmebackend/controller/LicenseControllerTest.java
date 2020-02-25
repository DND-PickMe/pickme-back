package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.License;
import com.pickmebackend.domain.dto.license.LicenseRequestDto;
import com.pickmebackend.repository.LicenseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import static com.pickmebackend.error.ErrorMessageConstant.LICENSENOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;
import static org.hamcrest.Matchers.is;
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

class LicenseControllerTest extends BaseControllerTest {

    @Autowired
    LicenseRepository licenseRepository;

    private final String licenseUrl = "/api/licenses/";

    @AfterEach
    void setUp() {
        licenseRepository.deleteAll();
        accountRepository.deleteAll();
        enterpriseRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 자격증을 생성")
    void saveLicense() throws Exception {
        jwt = generateBearerToken();

        String name = "정보처리기사";
        String institution = "한국산업인력공단";
        String description = "2019년 8월 16일에 취득하였습니다.";
        LocalDate issuedDate = LocalDate.of(2019, 8, 16);

        LicenseRequestDto licenseRequestDto = LicenseRequestDto.builder()
                                            .name(name)
                                            .institution(institution)
                                            .description(description)
                                            .issuedDate(issuedDate)
                                            .build();

        mockMvc.perform(post(licenseUrl)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType( MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(licenseRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("institution", is(institution)))
                .andExpect(jsonPath("description", is(description)))
                .andExpect(jsonPath("issuedDate", is(issuedDate.toString())))
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-license").exists())
                .andExpect(jsonPath("_links.delete-license").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-license",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-license").description("link to update license"),
                                linkWithRel("delete-license").description("link to delete license"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("자격증 명"),
                                fieldWithPath("institution").description("자격증 발급 기관"),
                                fieldWithPath("issuedDate").description("자격증 발급 날짜"),
                                fieldWithPath("description").description("자격증 설명")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header"),
                                headerWithName(HttpHeaders.LOCATION).description("Location Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("자격증 식별자"),
                                fieldWithPath("name").description("자격증 명"),
                                fieldWithPath("institution").description("자격증 발급 기관"),
                                fieldWithPath("issuedDate").description("자격증 발급 날짜"),
                                fieldWithPath("description").description("자격증 설명"),
                                fieldWithPath("account.id").description("자격증 등록자 식별자"),
                                fieldWithPath("account.email").description("자격증 등록자 이메일"),
                                fieldWithPath("account.nickName").description("자격증 등록자 닉네임"),
                                fieldWithPath("account.technology").description("자격증 등록자 기술 스택"),
                                fieldWithPath("account.favorite").description("자격증 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("자격증 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("자격증 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("자격증 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("자격증 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("자격증 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("자격증 등록자의 경력 사항"),
                                fieldWithPath("account.licenses[*].*").ignored(),
                                fieldWithPath("account.prizes").description("자격증 등록자의 수상 내역"),
                                fieldWithPath("account.projects").description("자격증 등록자의 프로젝트"),
                                fieldWithPath("account.selfInterviews").description("자격증 등록자의 셀프 인터뷰"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기업담당자가 자격증을 생성할 시 Forbidden")
    void saveLicense_forbidden() throws Exception {
        jwt = createEnterpriseJwt();

        String name = "정보처리기사";
        String institution = "한국산업인력공단";
        String description = "2019년 8월 16일에 취득하였습니다.";
        LocalDate issuedDate = LocalDate.of(2019, 8, 16);

        LicenseRequestDto licenseRequestDto = LicenseRequestDto.builder()
                .name(name)
                .institution(institution)
                .description(description)
                .issuedDate(issuedDate)
                .build();

        mockMvc.perform(post(licenseUrl)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType( MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(licenseRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("정상적으로 자격증 수정")
    void updateLicense() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        License license = createLicense(newAccount);

        String updateDescription = "생각해보니 2018년 1월 1일에 취득했습니다.";
        LocalDate updateIssuedDate = LocalDate.of(2018, 1, 1);

        license.setDescription(updateDescription);
        license.setIssuedDate(updateIssuedDate);

        LicenseRequestDto licenseRequestDto = modelMapper.map(license, LicenseRequestDto.class);
        mockMvc.perform(put(licenseUrl + "{licenseId}", license.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(licenseRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("name").isNotEmpty())
                .andExpect(jsonPath("institution").isNotEmpty())
                .andExpect(jsonPath("description", is(updateDescription)))
                .andExpect(jsonPath("issuedDate", is(updateIssuedDate.toString())))
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.create-license").exists())
                .andExpect(jsonPath("_links.delete-license").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-license",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-license").description("link to create license"),
                                linkWithRel("delete-license").description("link to delete license"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 자격증 명"),
                                fieldWithPath("institution").description("수정할 발급 기관"),
                                fieldWithPath("issuedDate").description("수정할 발급 날짜"),
                                fieldWithPath("description").description("수정할 자격증 설명")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("수정된 자격증 식별자"),
                                fieldWithPath("name").description("수정된 자격증 명"),
                                fieldWithPath("institution").description("수정된 자격증 발급 기관"),
                                fieldWithPath("issuedDate").description("수정된 자격증 발급 날짜"),
                                fieldWithPath("description").description("수정된 자격증 설명"),
                                fieldWithPath("account.id").description("수정된 자격증 등록자 식별자"),
                                fieldWithPath("account.email").description("수정된 자격증 등록자 이메일"),
                                fieldWithPath("account.nickName").description("수정된 자격증 등록자 닉네임"),
                                fieldWithPath("account.technology").description("수정된 자격증 등록자 기술 스택"),
                                fieldWithPath("account.favorite").description("수정된 자격증 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("수정된 자격증 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("수정된 자격증 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("수정된 자격증 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("수정된 자격증 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("수정된 자격증 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("수정된 자격증 등록자의 경력 사항"),
                                fieldWithPath("account.licenses[*].*").ignored(),
                                fieldWithPath("account.prizes").description("수정된 자격증 등록자의 수상 내역"),
                                fieldWithPath("account.projects").description("수정된 자격증 등록자의 프로젝트"),
                                fieldWithPath("account.selfInterviews").description("수정된 자격증 등록자의 셀프 인터뷰"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("데이터베이스에 없는 자격증 수정 요청 시 Bad Request 반환")
    void updateLicense_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        License license = createLicense(newAccount);

        String updateDescription = "생각해보니 2018년 1월 1일에 취득했습니다.";
        LocalDate updateIssuedDate = LocalDate.of(2018, 1, 1);

        license.setDescription(updateDescription);
        license.setIssuedDate(updateIssuedDate);

        LicenseRequestDto licenseRequestDto = modelMapper.map(license, LicenseRequestDto.class);
        mockMvc.perform(put(licenseUrl + "{licenseId}", -1)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(licenseRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(LICENSENOTFOUND)));
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 자격증 수정을 요청할 때 Bad Request 반환")
    void updateLicense_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        License license = createLicense(newAccount);

        String updateDescription = "생각해보니 2018년 1월 1일에 취득했습니다.";
        LocalDate updateIssuedDate = LocalDate.of(2018, 1, 1);

        license.setDescription(updateDescription);
        license.setIssuedDate(updateIssuedDate);

        LicenseRequestDto licenseRequestDto = modelMapper.map(license, LicenseRequestDto.class);
        mockMvc.perform(put(licenseUrl + "{licenseId}", license.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(licenseRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)));
    }

    @Test
    @DisplayName("기업 담당자가 자격증 수정 요청 시 Forbidden")
    void updateLicense_forbidden() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        License license = createLicense(newAccount);

        jwt = createEnterpriseJwt();

        String updateDescription = "생각해보니 2018년 1월 1일에 취득했습니다.";
        LocalDate updateIssuedDate = LocalDate.of(2018, 1, 1);

        license.setDescription(updateDescription);
        license.setIssuedDate(updateIssuedDate);

        LicenseRequestDto licenseRequestDto = modelMapper.map(license, LicenseRequestDto.class);
        mockMvc.perform(put(licenseUrl + "{licenseId}", license.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(licenseRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("정상적으로 자격증 삭제")
    void deleteLicense() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        License license = createLicense(newAccount);

        mockMvc.perform(delete(licenseUrl + "{licenseId}", license.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.create-license").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("delete-license",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-license").description("link to create license"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("삭제된 자격증 식별자"),
                                fieldWithPath("name").description("삭제된 자격증 명"),
                                fieldWithPath("institution").description("삭제된 자격증 발급 기관"),
                                fieldWithPath("issuedDate").description("삭제된 자격증 발급 날짜"),
                                fieldWithPath("description").description("삭제된 자격증 설명"),
                                fieldWithPath("account.id").description("삭제된 자격증 등록자 식별자"),
                                fieldWithPath("account.email").description("삭제된 자격증 등록자 이메일"),
                                fieldWithPath("account.nickName").description("삭제된 자격증 등록자 닉네임"),
                                fieldWithPath("account.technology").description("삭제된 자격증 등록자 기술 스택"),
                                fieldWithPath("account.favorite").description("삭제된 자격증 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("삭제된 자격증 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("삭제된 자격증 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("삭제된 자격증 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("삭제된 자격증 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("삭제된 자격증 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("삭제된 자격증 등록자의 경력 사항"),
                                fieldWithPath("account.licenses[*].*").ignored(),
                                fieldWithPath("account.prizes").description("삭제된 자격증 등록자의 수상 내역"),
                                fieldWithPath("account.projects").description("삭제된 자격증 등록자의 프로젝트"),
                                fieldWithPath("account.selfInterviews").description("삭제된 자격증 등록자의 셀프 인터뷰"),
                                fieldWithPath("_links.*.*").ignored()
                        ))
                )
        ;
    }

    @Test
    @DisplayName("데이터베이스에 없는 자격증 삭제 요청 시 Bad Request 반환")
    void deleteLicense_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        createLicense(newAccount);

        mockMvc.perform(delete(licenseUrl + "{licenseId}", -1)
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(LICENSENOTFOUND)));
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 자격증 삭제를 요청할 때 Bad Request 반환")
    void deleteLicense_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        License license = createLicense(newAccount);

        mockMvc.perform(delete(licenseUrl + "{licenseId}", license.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)));
    }

    @Test
    @DisplayName("기업 담당자가 자격증 삭제 요청 시 Forbidden")
    void deleteLicense_forbidden() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        License license = createLicense(newAccount);

        jwt = createEnterpriseJwt();

        mockMvc.perform(delete(licenseUrl + "{licenseId}", license.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    private License createLicense(Account account) {
        String name = "정보처리기사";
        String institution = "한국산업인력공단";
        String description = "2019년 8월 16일에 취득하였습니다.";
        LocalDate issuedDate = LocalDate.of(2019, 8, 16);

        License license = License.builder()
                .name(name)
                .institution(institution)
                .description(description)
                .issuedDate(issuedDate)
                .account(account)
                .build();

        License newLicense = licenseRepository.save(license);

        assertNotNull(newLicense.getId());
        assertNotNull(newLicense.getName());
        assertNotNull(newLicense.getDescription());
        assertNotNull(newLicense.getInstitution());
        assertNotNull(newLicense.getIssuedDate());
        assertNotNull(newLicense.getAccount());

        return newLicense;
    }

    private String generateBearerToken() {
        Account newAccount = createAccount();
        return "Bearer " + jwtProvider.generateToken(newAccount);
    }

    private String generateBearerToken_need_account(Account newAccount) {
        return "Bearer " + jwtProvider.generateToken(newAccount);
    }
}