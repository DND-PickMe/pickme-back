package com.pickmebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.domain.Experience;
import com.pickmebackend.domain.dto.ExperienceDto;
import com.pickmebackend.repository.ExperienceRepository;
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

import static com.pickmebackend.error.ErrorMessageConstant.EXPERIENCENOTFOUND;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ExperienceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExperienceRepository experienceRepository;

    @Autowired
    ModelMapper modelMapper;

    private final String experienceUrl = "/api/experiences/";

    @BeforeEach
    void setUp() {
        experienceRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 경력 생성하기")
    void saveExperience() throws Exception {
        String companyName = "D&D 주식회사";
        String description = "D&D 주식회사에서 Spring Boot를 사용해 백엔드 개발을 맡았습니다.";
        String position = "백엔드 개발자";
        LocalDate joinedAt = LocalDate.of(2019, 12, 25);
        LocalDate retiredAt = LocalDate.of(2020, 1, 22);

        ExperienceDto experienceDto = ExperienceDto.builder()
                                                .companyName(companyName)
                                                .description(description)
                                                .position(position)
                                                .joinedAt(joinedAt)
                                                .retiredAt(retiredAt)
                                                .build();

        mockMvc.perform(post(experienceUrl)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(experienceDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("companyName").value(companyName))
                .andExpect(jsonPath("description").value(description))
                .andExpect(jsonPath("position").value(position))
                .andExpect(jsonPath("joinedAt").value(joinedAt.toString()))
                .andExpect(jsonPath("retiredAt").value(retiredAt.toString()));
    }

    @Test
    @DisplayName("정상적으로 경력 수정하기")
    void updateExperience() throws Exception {
        Experience experience = createExperience();
        String updateDescription = "사실 프론트엔드 개발자를 맡았었습니다.";
        String updatePosition = "프론트엔트 개발자";

        experience.setDescription(updateDescription);
        experience.setPosition(updatePosition);

        ExperienceDto experienceDto = modelMapper.map(experience, ExperienceDto.class);
        mockMvc.perform(put(experienceUrl + "{experienceId}", experience.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(experienceDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("companyName").isNotEmpty())
                .andExpect(jsonPath("description").value(updateDescription))
                .andExpect(jsonPath("position").value(updatePosition))
                .andExpect(jsonPath("joinedAt").isNotEmpty())
                .andExpect(jsonPath("retiredAt").isNotEmpty());
    }

    @Test
    @DisplayName("데이터베이스 저장되어 있지 않은 경력의 수정을 요청")
    void updateExperience_not_found() throws Exception {
        Experience experience = createExperience();
        String updateDescription = "사실 프론트엔드 개발자를 맡았었습니다.";
        String updatePosition = "프론트엔트 개발자";

        experience.setDescription(updateDescription);
        experience.setPosition(updatePosition);

        ExperienceDto experienceDto = modelMapper.map(experience, ExperienceDto.class);
        mockMvc.perform(put(experienceUrl + "{selfInterviewId}", -1)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(experienceDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(EXPERIENCENOTFOUND));
    }

    @Test
    @DisplayName("정상적으로 경력 삭제하기")
    void deleteExperience() throws Exception {
        Experience experience = createExperience();

        mockMvc.perform(delete(experienceUrl + "{selfInterviewId}", experience.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("데이터베이스 저장되어 있지 않은 경력의 삭제를 요청")
    void deleteExperience_not_found() throws Exception {
        createExperience();

        mockMvc.perform(delete(experienceUrl + "{selfInterviewId}", -1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(EXPERIENCENOTFOUND));
    }

    private Experience createExperience() {
        String companyName = "D&D 주식회사";
        String description = "D&D 주식회사에서 Spring Boot를 사용해 백엔드 개발을 맡았습니다.";
        String position = "백엔드 개발자";
        LocalDate joinedAt = LocalDate.of(2019, 12, 25);
        LocalDate retiredAt = LocalDate.of(2020, 1, 22);

        Experience experience = Experience.builder()
                .companyName(companyName)
                .description(description)
                .position(position)
                .joinedAt(joinedAt)
                .retiredAt(retiredAt)
                .build();

        Experience newExperience = experienceRepository.save(experience);

        assertNotNull(newExperience.getId());
        assertNotNull(newExperience.getCompanyName());
        assertNotNull(newExperience.getDescription());
        assertNotNull(newExperience.getPosition());
        assertNotNull(newExperience.getJoinedAt());
        assertNotNull(newExperience.getRetiredAt());

        return newExperience;
    }
}