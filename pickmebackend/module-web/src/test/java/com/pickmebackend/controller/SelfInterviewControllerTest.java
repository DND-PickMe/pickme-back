package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.SelfInterview;
import com.pickmebackend.domain.dto.SelfInterviewDto;
import com.pickmebackend.repository.SelfInterviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static com.pickmebackend.error.ErrorMessageConstant.SELFINTERVIEWNOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SelfInterviewControllerTest extends BaseControllerTest {

    @Autowired
    SelfInterviewRepository selfInterviewRepository;

    private final String selfInterviewUrl = "/api/selfInterviews/";

    @AfterEach
    void setUp() {
        selfInterviewRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 셀프 인터뷰 생성하기")
    void saveSelfInterview() throws Exception {
        jwt = generateBearerToken();
        SelfInterviewDto selfInterviewDto = new SelfInterviewDto();
        String title = "사람, 워라벨, 업무만족도, 연봉 중 중요한 순서대로 나열한다면?";
        String content = "사람 > 업무만족도 > 연봉 > 워라벨";
        selfInterviewDto.setTitle(title);
        selfInterviewDto.setContent(content);

        mockMvc.perform(post(selfInterviewUrl)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(selfInterviewDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").value(title))
                .andExpect(jsonPath("content").value(content))
                .andExpect(jsonPath("account").isNotEmpty());
    }

    @Test
    @DisplayName("정상적으로 셀프 인터뷰 수정하기")
    void updateSelfInterview() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        SelfInterview selfInterview = createSelfInterview(newAccount);

        String updateContent = "워라벨이 가장 중요한 것 같습니다.";
        selfInterview.setContent(updateContent);

        SelfInterviewDto selfInterviewDto = modelMapper.map(selfInterview, SelfInterviewDto.class);
        mockMvc.perform(put(selfInterviewUrl + "{selfInterviewId}", selfInterview.getId())
                                        .accept(MediaTypes.HAL_JSON_VALUE)
                                        .header(HttpHeaders.AUTHORIZATION, jwt)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(selfInterviewDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("title").value("회사를 고를 때 가장 중요하게 생각하는 것은?"))
                .andExpect(jsonPath("content").value(updateContent))
                .andExpect(jsonPath("account").isNotEmpty());
    }

    @Test
    @DisplayName("데이터베이스 저장되어 있지 않은 셀프 인터뷰의 수정을 요청")
    void updateSelfInterview_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        SelfInterview selfInterview = createSelfInterview(newAccount);

        String updateContent = "워라벨이 가장 중요한 것 같습니다.";
        selfInterview.setContent(updateContent);

        SelfInterviewDto selfInterviewDto = modelMapper.map(selfInterview, SelfInterviewDto.class);
        mockMvc.perform(put(selfInterviewUrl + "{selfInterviewId}", -1)
                                        .accept(MediaTypes.HAL_JSON_VALUE)
                                        .header(HttpHeaders.AUTHORIZATION, jwt)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(selfInterviewDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(SELFINTERVIEWNOTFOUND));
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

        SelfInterviewDto selfInterviewDto = modelMapper.map(selfInterview, SelfInterviewDto.class);
        mockMvc.perform(put(selfInterviewUrl + "{selfInterviewId}", selfInterview.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(selfInterviewDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(UNAUTHORIZEDUSER));
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
                .andExpect(status().isOk());
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(SELFINTERVIEWNOTFOUND));
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(UNAUTHORIZEDUSER));
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