package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Prize;
import com.pickmebackend.domain.dto.prize.PrizeRequestDto;
import com.pickmebackend.repository.PrizeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import static com.pickmebackend.error.ErrorMessageConstant.*;
import static com.pickmebackend.error.ErrorMessageConstant.PRIZENOTFOUND;
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

class PrizeControllerTest extends BaseControllerTest {

    @Autowired
    PrizeRepository prizeRepository;

    private final String prizeUrl = "/api/prizes/";

    @BeforeEach
    void cleanUp() {
        prizeRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @AfterEach
    void setUp() {
        prizeRepository.deleteAll();
        accountRepository.deleteAll();
        enterpriseRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 수상 내역 생성하기")
    void savePrize() throws Exception {
        jwt = generateBearerToken();

        PrizeRequestDto prizeRequestDto = new PrizeRequestDto();

        String name = "ACM-ICPC 대상 수상";
        String description = "2019년 12월 31일에 수상했습니다.";
        String competition = "icpckorea";
        LocalDate issuedDate = LocalDate.of(2019, 12, 31);

        prizeRequestDto.setName(name);
        prizeRequestDto.setDescription(description);
        prizeRequestDto.setCompetition(competition);
        prizeRequestDto.setIssuedDate(issuedDate);

        mockMvc.perform(post(prizeUrl)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prizeRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("competition").value(competition))
                .andExpect(jsonPath("issuedDate").value(issuedDate.toString()))
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-prize").exists())
                .andExpect(jsonPath("_links.delete-prize").exists())
                .andDo(document("create-prize",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-prize").description("link to update prize"),
                                linkWithRel("delete-prize").description("link to delete prize")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("competition").description("수상 대회명"),
                                fieldWithPath("name").description("수상 내역"),
                                fieldWithPath("issuedDate").description("수상 날짜"),
                                fieldWithPath("description").description("수상 내역 설명")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header"),
                                headerWithName(HttpHeaders.LOCATION).description("Location Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("수상 내역 식별자"),
                                fieldWithPath("competition").description("수상 대회 명"),
                                fieldWithPath("name").description("수상 내역"),
                                fieldWithPath("issuedDate").description("수상 날짜"),
                                fieldWithPath("description").description("수상 내역 설명"),
                                fieldWithPath("account.id").description("수상 내역 등록자 식별자"),
                                fieldWithPath("account.email").description("수상 내역 등록자 이메일"),
                                fieldWithPath("account.nickName").description("수상 내역 등록자 닉네임"),
                                fieldWithPath("account.technology").description("수상 내역 등록자 기술 스택"),
                                fieldWithPath("account.favorite").description("수상 내역 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("수상 내역 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("수상 내역 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("수상 내역 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("수상 내역 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("수상 내역 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("수상 내역 등록자의 경력 사항"),
                                fieldWithPath("account.licenses").description("수상 내역 등록자의 자격증"),
                                fieldWithPath("account.prizes[*].*").ignored(),
                                fieldWithPath("account.projects").description("수상 내역 등록자의 프로젝트"),
                                fieldWithPath("account.selfInterviews").description("수상 내역 등록자의 셀프 인터뷰"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("기업담당자가 수상 내역 생성할 시 Forbidden")
    void savePrize_forbidden() throws Exception {
        jwt = createEnterpriseJwt();

        PrizeRequestDto prizeRequestDto = new PrizeRequestDto();

        String name = "ACM-ICPC 대상 수상";
        String description = "2019년 12월 31일에 수상했습니다.";
        String competition = "icpckorea";
        LocalDate issuedDate = LocalDate.of(2019, 12, 31);

        prizeRequestDto.setName(name);
        prizeRequestDto.setDescription(description);
        prizeRequestDto.setCompetition(competition);
        prizeRequestDto.setIssuedDate(issuedDate);

        mockMvc.perform(post(prizeUrl)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prizeRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("정상적으로 수상 내역 수정하기")
    void updatePrize() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        Prize prize = createPrize(newAccount);

        String updateDescription = "사실 수상 경력은 거짓말입니다.";
        prize.setDescription(updateDescription);
        PrizeRequestDto prizeRequestDto = modelMapper.map(prize, PrizeRequestDto.class);

        mockMvc.perform(put(prizeUrl + "{prizeId}", prize.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prizeRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").isNotEmpty())
                .andExpect(jsonPath("description").value(updateDescription))
                .andExpect(jsonPath("competition").isNotEmpty())
                .andExpect(jsonPath("issuedDate").isNotEmpty())
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.create-prize").exists())
                .andExpect(jsonPath("_links.delete-prize").exists())
                .andDo(document("update-prize",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-prize").description("link to create prize"),
                                linkWithRel("delete-prize").description("link to delete prize")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("competition").description("수정할 수상대회 명"),
                                fieldWithPath("name").description("수정할 수상 내역"),
                                fieldWithPath("issuedDate").description("수정할 수상 날짜"),
                                fieldWithPath("description").description("수정할 수상 내역 설명")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("수정된 프로젝트 식별자"),
                                fieldWithPath("competition").description("수정된 수상대회 명"),
                                fieldWithPath("name").description("수정된 수상 내역"),
                                fieldWithPath("issuedDate").description("수정된 수상 날짜"),
                                fieldWithPath("description").description("수정된 수상 내역 설명"),
                                fieldWithPath("account.id").description("수정된 수상 내역 등록자 식별자"),
                                fieldWithPath("account.email").description("수정된 수상 내역 등록자 이메일"),
                                fieldWithPath("account.nickName").description("수정된 수상 내역 등록자 닉네임"),
                                fieldWithPath("account.technology").description("수정된 수상 내역 등록자 기술 스택"),
                                fieldWithPath("account.favorite").description("수정된 수상 내역 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("수정된 수상 내역 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("수정된 수상 내역 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("수정된 수상 내역 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("수정된 수상 내역 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("수정된 수상 내역 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("수정된 수상 내역 등록자의 경력 사항"),
                                fieldWithPath("account.licenses").description("수정된 수상 내역 등록자의 자격증"),
                                fieldWithPath("account.prizes[*].*").ignored(),
                                fieldWithPath("account.projects").description("수정된 수상 내역 등록자의 수상 내역"),
                                fieldWithPath("account.selfInterviews").description("수정된 수상 내역 등록자의 셀프 인터뷰"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("데이터베이스 저장되어 있지 않은 수상 내역의 수정을 요청")
    void updatePrize_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        Prize prize = createPrize(newAccount);

        String updateDescription = "사실 수상 경력은 거짓말입니다.";
        prize.setDescription(updateDescription);
        PrizeRequestDto prizeRequestDto = modelMapper.map(prize, PrizeRequestDto.class);

        mockMvc.perform(put(prizeUrl + "{prizeId}", -1)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prizeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(PRIZENOTFOUND)));
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 수상 경력 수정을 요청할 때 Bad Request 반환")
    void updatePrize_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        Prize prize = createPrize(newAccount);

        String updateDescription = "사실 수상 경력은 거짓말입니다.";
        prize.setDescription(updateDescription);
        PrizeRequestDto prizeRequestDto = modelMapper.map(prize, PrizeRequestDto.class);

        mockMvc.perform(put(prizeUrl + "{prizeId}", prize.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prizeRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)));
    }

    @Test
    @DisplayName("기업 담당자가 수상 경력 수정을 요청할 때 Forbidden")
    void updatePrize_forbidden() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        Prize prize = createPrize(newAccount);

        String updateDescription = "사실 수상 경력은 거짓말입니다.";
        prize.setDescription(updateDescription);
        PrizeRequestDto prizeRequestDto = modelMapper.map(prize, PrizeRequestDto.class);

        jwt = createEnterpriseJwt();

        mockMvc.perform(put(prizeUrl + "{prizeId}", prize.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prizeRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("정상적으로 수상 내역 삭제하기")
    void deletePrize() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        Prize prize = createPrize(newAccount);

        mockMvc.perform(delete(prizeUrl + "{prizeId}", prize.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.create-prize").exists())
                .andDo(document("delete-prize",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-prize").description("link to create prize")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("삭제된 프로젝트 식별자"),
                                fieldWithPath("competition").description("삭제된 수상대회 명"),
                                fieldWithPath("name").description("삭제된 수상 내역"),
                                fieldWithPath("issuedDate").description("삭제된 수상 날짜"),
                                fieldWithPath("description").description("삭제된 수상 내역 설명"),
                                fieldWithPath("account.id").description("삭제된 수상 내역 등록자 식별자"),
                                fieldWithPath("account.email").description("삭제된 수상 내역 등록자 이메일"),
                                fieldWithPath("account.nickName").description("삭제된 수상 내역 등록자 닉네임"),
                                fieldWithPath("account.technology").description("삭제된 수상 내역 등록자 기술 스택"),
                                fieldWithPath("account.favorite").description("삭제된 수상 내역 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("삭제된 수상 내역 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("삭제된 수상 내역 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("삭제된 수상 내역 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("삭제된 수상 내역 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("삭제된 수상 내역 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("삭제된 수상 내역 등록자의 경력 사항"),
                                fieldWithPath("account.licenses").description("삭제된 수상 내역 등록자의 자격증"),
                                fieldWithPath("account.prizes[*].*").ignored(),
                                fieldWithPath("account.projects").description("삭제된 수상 내역 등록자의 수상 내역"),
                                fieldWithPath("account.selfInterviews").description("삭제된 수상 내역 등록자의 셀프 인터뷰"),
                                fieldWithPath("_links.*.*").ignored()
                                ))
                )

        ;
    }

    @Test
    @DisplayName("데이터베이스 저장되어 있지 않은 수상 내역의 삭제를 요청")
    void deletePrize_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        createPrize(newAccount);

        mockMvc.perform(delete(prizeUrl + "{prizeId}", -1)
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(PRIZENOTFOUND));
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 수상 경력 삭제를 요청할 때 Bad Request 반환")
    void deletePrize_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        Prize prize = createPrize(newAccount);

        mockMvc.perform(delete(prizeUrl + "{prizeId}", prize.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(UNAUTHORIZEDUSER));
    }

    @Test
    @DisplayName("기업 담당자가 수상 경력 삭제를 요청할 때 Forbidden")
    void deletePrize_forbidden() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        Prize prize = createPrize(newAccount);

        jwt = createEnterpriseJwt();

        mockMvc.perform(delete(prizeUrl + "{prizeId}", prize.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    private Prize createPrize(Account account) {
        Prize prize = new Prize();

        String name = "ACM-ICPC 대상 수상";
        String description = "2019년 12월 31일에 수상했습니다.";
        String competition = "icpckorea";
        LocalDate issuedDate = LocalDate.of(2019, 12, 31);

        prize.setName(name);
        prize.setDescription(description);
        prize.setCompetition(competition);
        prize.setIssuedDate(issuedDate);
        prize.setAccount(account);

        Prize newPrize = prizeRepository.save(prize);

        assertNotNull(newPrize.getId());
        assertNotNull(newPrize.getCompetition());
        assertNotNull(newPrize.getDescription());
        assertNotNull(newPrize.getIssuedDate());
        assertNotNull(newPrize.getAccount());

        return newPrize;
    }

    private String generateBearerToken() {
        Account newAccount = createAccount();
        return "Bearer " + jwtProvider.generateToken(newAccount);
    }

    private String generateBearerToken_need_account(Account newAccount) {
        return "Bearer " + jwtProvider.generateToken(newAccount);
    }
}