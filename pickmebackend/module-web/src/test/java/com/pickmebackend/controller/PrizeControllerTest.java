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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("competition").value(competition))
                .andExpect(jsonPath("issuedDate").value(issuedDate.toString()))
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-prize").exists())
                .andExpect(jsonPath("_links.delete-prize").exists())
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
                .andExpect(status().isOk());
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