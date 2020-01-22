package com.pickmebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.domain.License;
import com.pickmebackend.domain.dto.LicenseDto;
import com.pickmebackend.error.ErrorMessageConstant;
import com.pickmebackend.repository.LicenseRepository;
import com.pickmebackend.repository.SelfInterviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.pickmebackend.error.ErrorMessageConstant.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LicenseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    ModelMapper modelMapper;

    private final String licenseUrl = "/api/licenses/";

    @BeforeEach
    void setUp() {
        licenseRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 자격증을 생성")
    void saveLicense() throws Exception {
        String name = "정보처리기사";
        String institution = "한국산업인력공단";
        String description = "2019년 8월 16일에 취득하였습니다.";
        LocalDate issuedDate = LocalDate.of(2019, 8, 16);

        LicenseDto licenseDto = LicenseDto.builder()
                                            .name(name)
                                            .institution(institution)
                                            .description(description)
                                            .issuedDate(issuedDate)
                                            .build();

        mockMvc.perform(post(licenseUrl)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType( MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(licenseDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("institution", is(institution)))
                .andExpect(jsonPath("description", is(description)))
                .andExpect(jsonPath("issuedDate", is(issuedDate.toString())));
    }

    @Test
    @DisplayName("정상적으로 자격증 수정")
    void updateLicense() throws Exception {
        License license = createLicense();
        String updateDescription = "생각해보니 2018년 1월 1일에 취득했습니다.";
        LocalDate updateIssuedDate = LocalDate.of(2018, 1, 1);

        license.setDescription(updateDescription);
        license.setIssuedDate(updateIssuedDate);

        LicenseDto licenseDto = modelMapper.map(license, LicenseDto.class);
        mockMvc.perform(put(licenseUrl + "{licenseId}", license.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(licenseDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("name").isNotEmpty())
                .andExpect(jsonPath("institution").isNotEmpty())
                .andExpect(jsonPath("description", is(updateDescription)))
                .andExpect(jsonPath("issuedDate", is(updateIssuedDate.toString())));
    }

    @Test
    @DisplayName("데이터베이스에 없는 자격증 수정 요청 시 Bad Request 반환")
    void updateLicense_not_found() throws Exception {
        License license = createLicense();
        String updateDescription = "생각해보니 2018년 1월 1일에 취득했습니다.";
        LocalDate updateIssuedDate = LocalDate.of(2018, 1, 1);

        license.setDescription(updateDescription);
        license.setIssuedDate(updateIssuedDate);

        LicenseDto licenseDto = modelMapper.map(license, LicenseDto.class);
        mockMvc.perform(put(licenseUrl + "{licenseId}", -1)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(licenseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(LICENSENOTFOUND)));
    }

    @Test
    @DisplayName("정상적으로 자격증 삭제")
    void deleteLicense() throws Exception {
        License license = createLicense();

        mockMvc.perform(delete(licenseUrl + "{licenseId}", license.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("데이터베이스에 없는 자격증 수정 요청 시 Bad Request 반환")
    void deleteLicense_not_found() throws Exception {
        createLicense();

        mockMvc.perform(delete(licenseUrl + "{licenseId}", -1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(LICENSENOTFOUND)));
    }

    private License createLicense() {
        String name = "정보처리기사";
        String institution = "한국산업인력공단";
        String description = "2019년 8월 16일에 취득하였습니다.";
        LocalDate issuedDate = LocalDate.of(2019, 8, 16);

        License license = License.builder()
                .name(name)
                .institution(institution)
                .description(description)
                .issuedDate(issuedDate)
                .build();

        License newLicense = licenseRepository.save(license);

        assertNotNull(newLicense.getId());
        assertNotNull(newLicense.getName());
        assertNotNull(newLicense.getDescription());
        assertNotNull(newLicense.getInstitution());
        assertNotNull(newLicense.getIssuedDate());

        return newLicense;
    }
}