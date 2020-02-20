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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("institution", is(institution)))
                .andExpect(jsonPath("description", is(description)))
                .andExpect(jsonPath("issuedDate", is(issuedDate.toString())))
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-license").exists())
                .andExpect(jsonPath("_links.delete-license").exists())
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
                .andExpect(status().isOk());
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