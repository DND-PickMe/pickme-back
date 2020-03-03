package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.SelfInterview;
import com.pickmebackend.domain.dto.selfInterview.SelfInterviewRequestDto;
import com.pickmebackend.repository.SelfInterviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SelfInterviewControllerTest extends BaseControllerTest {

    @Autowired
    SelfInterviewRepository selfInterviewRepository;

    private final String selfInterviewUrl = "/api/selfInterviews/";

    @AfterEach
    void setUp() {
        selfInterviewRepository.deleteAll();
        accountRepository.deleteAll();
        enterpriseRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 셀프 인터뷰 생성하기")
    void saveSelfInterview() throws Exception {
        jwt = generateBearerToken();
        SelfInterviewRequestDto selfInterviewRequestDto = new SelfInterviewRequestDto();
        String title = "사람, 워라벨, 업무만족도, 연봉 중 중요한 순서대로 나열한다면?";
        String content = "사람 > 업무만족도 > 연봉 > 워라벨";
        selfInterviewRequestDto.setTitle(title);
        selfInterviewRequestDto.setContent(content);

        mockMvc.perform(post(selfInterviewUrl)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(selfInterviewRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").value(title))
                .andExpect(jsonPath("content").value(content))
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-selfInterview").exists())
                .andExpect(jsonPath("_links.delete-selfInterview").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-selfInterview",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-selfInterview").description("link to update self interview"),
                                linkWithRel("delete-selfInterview").description("link to delete self interview"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("title").description("사용할 셀프 인터뷰 질문"),
                                fieldWithPath("content").description("사용할 셀프 인터뷰 답변")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header"),
                                headerWithName(HttpHeaders.LOCATION).description("Location Header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("셀프 인터뷰 식별자"),
                                fieldWithPath("title").description("셀프 인터뷰 제목"),
                                fieldWithPath("content").description("셀프 인터뷰 질문"),
                                fieldWithPath("account.id").description("셀프 인터뷰 등록자 식별자"),
                                fieldWithPath("account.email").description("셀프 인터뷰 등록자 이메일"),
                                fieldWithPath("account.nickName").description("셀프 인터뷰 등록자 닉네임"),
                                fieldWithPath("account.favorite").description("셀프 인터뷰 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("셀프 인터뷰 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("셀프 인터뷰 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("셀프 인터뷰 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("셀프 인터뷰 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("셀프 인터뷰 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("셀프 인터뷰 등록자의 경력 사항"),
                                fieldWithPath("account.licenses").description("셀프 인터뷰 등록자의 자격증"),
                                fieldWithPath("account.prizes").description("셀프 인터뷰 등록자의 수상 내역"),
                                fieldWithPath("account.projects").description("셀프 인터뷰 등록자의 프로젝트"),
                                fieldWithPath("account.selfInterviews[*].*").ignored(),
                                fieldWithPath("_links.*.*").ignored()
                        )
                        ))
        ;
    }

    @Test
    @DisplayName("기업 담당자가 셀프 인터뷰 생성할 시 Forbidden")
    void saveSelfInterview_forbidden() throws Exception {
        jwt = createEnterpriseJwt();
        SelfInterviewRequestDto selfInterviewRequestDto = new SelfInterviewRequestDto();
        String title = "사람, 워라벨, 업무만족도, 연봉 중 중요한 순서대로 나열한다면?";
        String content = "사람 > 업무만족도 > 연봉 > 워라벨";
        selfInterviewRequestDto.setTitle(title);
        selfInterviewRequestDto.setContent(content);

        mockMvc.perform(post(selfInterviewUrl)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(selfInterviewRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("정상적으로 셀프 인터뷰 수정하기")
    void updateSelfInterview() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        SelfInterview selfInterview = createSelfInterview(newAccount);

        String updateContent = "워라벨이 가장 중요한 것 같습니다.";
        selfInterview.setContent(updateContent);

        SelfInterviewRequestDto selfInterviewRequestDto = modelMapper.map(selfInterview, SelfInterviewRequestDto.class);
        mockMvc.perform(put(selfInterviewUrl + "{selfInterviewId}", selfInterview.getId())
                                        .accept(MediaTypes.HAL_JSON_VALUE)
                                        .header(HttpHeaders.AUTHORIZATION, jwt)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(selfInterviewRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").value("회사를 고를 때 가장 중요하게 생각하는 것은?"))
                .andExpect(jsonPath("content").value(updateContent))
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.create-selfInterview").exists())
                .andExpect(jsonPath("_links.delete-selfInterview").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-selfInterview",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-selfInterview").description("link to create self interview"),
                                linkWithRel("delete-selfInterview").description("link to delete self interview"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("title").description("수정할 셀프 인터뷰 질문"),
                                fieldWithPath("content").description("수정할 셀프 인터뷰 답변")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("수정된 셀프 인터뷰 식별자"),
                                fieldWithPath("title").description("수정된 셀프 인터뷰 제목"),
                                fieldWithPath("content").description("수정된 셀프 인터뷰 질문"),
                                fieldWithPath("account.id").description("수정된 셀프 인터뷰 등록자 식별자"),
                                fieldWithPath("account.email").description("수정된 셀프 인터뷰 등록자 이메일"),
                                fieldWithPath("account.nickName").description("수정된 셀프 인터뷰 등록자 닉네임"),
                                fieldWithPath("account.favorite").description("수정된 셀프 인터뷰 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("수정된 셀프 인터뷰 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("수정된 셀프 인터뷰 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("수정된 셀프 인터뷰 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("수정된 셀프 인터뷰 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("수정된 셀프 인터뷰 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("수정된 셀프 인터뷰 등록자의 경력 사항"),
                                fieldWithPath("account.licenses").description("수정된 셀프 인터뷰 등록자의 자격증"),
                                fieldWithPath("account.prizes").description("수정된 셀프 인터뷰 등록자의 수상 내역"),
                                fieldWithPath("account.projects").description("수정된 셀프 인터뷰 등록자의 프로젝트"),
                                fieldWithPath("account.selfInterviews[*].*").ignored(),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("데이터베이스 저장되어 있지 않은 셀프 인터뷰의 수정을 요청")
    void updateSelfInterview_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        SelfInterview selfInterview = createSelfInterview(newAccount);

        String updateContent = "워라벨이 가장 중요한 것 같습니다.";
        selfInterview.setContent(updateContent);

        SelfInterviewRequestDto selfInterviewRequestDto = modelMapper.map(selfInterview, SelfInterviewRequestDto.class);
        mockMvc.perform(put(selfInterviewUrl + "{selfInterviewId}", -1)
                                        .accept(MediaTypes.HAL_JSON_VALUE)
                                        .header(HttpHeaders.AUTHORIZATION, jwt)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(selfInterviewRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 셀프인터뷰 수정을 요청할 때 Bad Request 반환")
    void updateSelfInterview_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        SelfInterview selfInterview = createSelfInterview(newAccount);

        String updateContent = "워라벨이 가장 중요한 것 같습니다.";
        selfInterview.setContent(updateContent);

        SelfInterviewRequestDto selfInterviewRequestDto = modelMapper.map(selfInterview, SelfInterviewRequestDto.class);
        mockMvc.perform(put(selfInterviewUrl + "{selfInterviewId}", selfInterview.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(selfInterviewRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기업 담당자가 셀프인터뷰 수정을 요청할 때 Forbidden")
    void updateSelfInterview_forbidden() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        SelfInterview selfInterview = createSelfInterview(newAccount);

        jwt = createEnterpriseJwt();

        String updateContent = "워라벨이 가장 중요한 것 같습니다.";
        selfInterview.setContent(updateContent);

        SelfInterviewRequestDto selfInterviewRequestDto = modelMapper.map(selfInterview, SelfInterviewRequestDto.class);
        mockMvc.perform(put(selfInterviewUrl + "{selfInterviewId}", selfInterview.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(selfInterviewRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("정상적으로 셀프 인터뷰 삭제하기")
    void deleteSelfInterview() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        SelfInterview selfInterview = createSelfInterview(newAccount);

        mockMvc.perform(delete(selfInterviewUrl + "{selfInterviewId}", selfInterview.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.create-selfInterview").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("delete-selfInterview",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-selfInterview").description("link to create self interview"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("삭제된 셀프 인터뷰 식별자"),
                                fieldWithPath("title").description("삭제된 셀프 인터뷰 제목"),
                                fieldWithPath("content").description("삭제된 셀프 인터뷰 질문"),
                                fieldWithPath("account.id").description("삭제된 셀프 인터뷰 등록자 식별자"),
                                fieldWithPath("account.email").description("삭제된 셀프 인터뷰 등록자 이메일"),
                                fieldWithPath("account.nickName").description("삭제된 셀프 인터뷰 등록자 닉네임"),
                                fieldWithPath("account.favorite").description("삭제된 셀프 인터뷰 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("삭제된 셀프 인터뷰 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("삭제된 셀프 인터뷰 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("삭제된 셀프 인터뷰 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("삭제된 셀프 인터뷰 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("삭제된 셀프 인터뷰 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("삭제된 셀프 인터뷰 등록자의 경력 사항"),
                                fieldWithPath("account.licenses").description("삭제된 셀프 인터뷰 등록자의 자격증"),
                                fieldWithPath("account.prizes").description("삭제된 셀프 인터뷰 등록자의 수상 내역"),
                                fieldWithPath("account.projects").description("삭제된 셀프 인터뷰 등록자의 프로젝트"),
                                fieldWithPath("account.selfInterviews[*].*").ignored(),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("데이터베이스 저장되어 있지 않은 셀프 인터뷰의 삭제를 요청")
    void deleteSelfInterview_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        createSelfInterview(newAccount);

        mockMvc.perform(delete(selfInterviewUrl + "{selfInterviewId}", -1)
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 셀프인터뷰 삭제를 요청할 때 Bad Request 반환")
    void deleteSelfInterview_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        SelfInterview selfInterview = createSelfInterview(newAccount);

        mockMvc.perform(delete(selfInterviewUrl + "{selfInterviewId}", selfInterview.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기업 담당자가 셀프인터뷰 삭제를 요청할 때 Forbidden")
    void deleteSelfInterview_forbidden() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        SelfInterview selfInterview = createSelfInterview(newAccount);

        jwt = createEnterpriseJwt();

        mockMvc.perform(delete(selfInterviewUrl + "{selfInterviewId}", selfInterview.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    private SelfInterview createSelfInterview(Account account) {
        SelfInterview selfInterview = new SelfInterview();
        selfInterview.setTitle("회사를 고를 때 가장 중요하게 생각하는 것은?");
        selfInterview.setContent("배울 것이 많은 직장");
        selfInterview.setAccount(account);

        SelfInterview newSelfInterview = selfInterviewRepository.save(selfInterview);

        assertNotNull(newSelfInterview.getId());
        assertNotNull(newSelfInterview.getTitle());
        assertNotNull(newSelfInterview.getContent());

        return newSelfInterview;
    }

    private String generateBearerToken() {
        Account newAccount = createAccount();
        return "Bearer " + jwtProvider.generateToken(newAccount);
    }

    private String generateBearerToken_need_account(Account newAccount) {
        return "Bearer " + jwtProvider.generateToken(newAccount);
    }
}