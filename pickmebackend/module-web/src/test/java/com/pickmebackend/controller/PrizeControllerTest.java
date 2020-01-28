package com.pickmebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Prize;
import com.pickmebackend.domain.dto.PrizeDto;
import com.pickmebackend.error.ErrorMessageConstant;
import com.pickmebackend.properties.AppProperties;
import com.pickmebackend.repository.AccountRepository;
import com.pickmebackend.repository.PrizeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static com.pickmebackend.error.ErrorMessageConstant.PRIZENOTFOUND;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PrizeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PrizeRepository prizeRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AppProperties appProperties;

    private String jwt;

    private final String prizeUrl = "/api/prizes/";

    @BeforeEach
    void setUp() {
        prizeRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 수상 내역 생성하기")
    void savePrize() throws Exception {
        jwt = generateBearerToken();

        PrizeDto prizeDto = new PrizeDto();

        String name = "ACM-ICPC 대상 수상";
        String description = "2019년 12월 31일에 수상했습니다.";
        String competition = "icpckorea";
        LocalDate issuedDate = LocalDate.of(2019, 12, 31);

        prizeDto.setName(name);
        prizeDto.setDescription(description);
        prizeDto.setCompetition(competition);
        prizeDto.setIssuedDate(issuedDate);

        mockMvc.perform(post(prizeUrl)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prizeDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("competition").value(competition))
                .andExpect(jsonPath("issuedDate").value(issuedDate.toString()));
    }

    @Test
    @DisplayName("정상적으로 수상 내역 수정하기")
    void updatePrize() throws Exception {
        jwt = generateBearerToken();
        Prize prize = createPrize();

        String updateDescription = "사실 수상 경력은 거짓말입니다.";
        prize.setDescription(updateDescription);
        PrizeDto prizeDto = modelMapper.map(prize, PrizeDto.class);

        mockMvc.perform(put(prizeUrl + "{prizeId}", prize.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prizeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").isNotEmpty())
                .andExpect(jsonPath("description").value(updateDescription))
                .andExpect(jsonPath("competition").isNotEmpty())
                .andExpect(jsonPath("issuedDate").isNotEmpty());
    }

    @Test
    @DisplayName("데이터베이스 저장되어 있지 않은 수상 내역의 수정을 요청")
    void updatePrize_not_found() throws Exception {
        jwt = generateBearerToken();
        Prize prize = createPrize();

        String updateDescription = "사실 수상 경력은 거짓말입니다.";
        prize.setDescription(updateDescription);
        PrizeDto prizeDto = modelMapper.map(prize, PrizeDto.class);

        mockMvc.perform(put(prizeUrl + "{prizeId}", -1)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prizeDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(ErrorMessageConstant.PRIZENOTFOUND)));
    }

    @Test
    @DisplayName("정상적으로 수상 내역 삭제하기")
    void deletePrize() throws Exception {
        jwt = generateBearerToken();
        Prize prize = createPrize();

        mockMvc.perform(delete(prizeUrl + "{prizeId}", prize.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("데이터베이스 저장되어 있지 않은 수상 내역의 삭제를 요청")
    void deletePrize_not_found() throws Exception {
        jwt = generateBearerToken();
        createPrize();

        mockMvc.perform(delete(prizeUrl + "{selfInterviewId}", -1)
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(PRIZENOTFOUND));
    }

    private Prize createPrize() {
        Prize prize = new Prize();

        String name = "ACM-ICPC 대상 수상";
        String description = "2019년 12월 31일에 수상했습니다.";
        String competition = "icpckorea";
        LocalDate issuedDate = LocalDate.of(2019, 12, 31);

        prize.setName(name);
        prize.setDescription(description);
        prize.setCompetition(competition);
        prize.setIssuedDate(issuedDate);

        Prize newPrize = prizeRepository.save(prize);
        
        assertNotNull(newPrize.getId());
        assertNotNull(newPrize.getCompetition());
        assertNotNull(newPrize.getDescription());
        assertNotNull(newPrize.getIssuedDate());
        
        return newPrize;
    }

    private String generateBearerToken() {
        Account newAccount = createAccount();
        return "Bearer " + jwtProvider.generateToken(newAccount);
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