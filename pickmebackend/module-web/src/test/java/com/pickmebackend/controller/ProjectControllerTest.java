package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Project;
import com.pickmebackend.domain.dto.ProjectDto;
import com.pickmebackend.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import static com.pickmebackend.error.ErrorMessageConstant.PROJECTNOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest extends BaseControllerTest {

    @Autowired
    ProjectRepository projectRepository;

    private final String projectUrl = "/api/projects/";

    @AfterEach
    void setUp() {
        projectRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 수상 내역 생성하기")
    void saveProject() throws Exception {
        jwt = generateBearerToken();
        String name = "픽미 프로젝트";
        String description = "부산 IT 연합 동아리 D&D에 참가하여 개발";
        String role = "백엔드 개발";
        String projectLink = "https://github.com/DND-PickMe";
        LocalDate startedAt = LocalDate.of(2019, 12, 21);
        LocalDate endedAt = LocalDate.of(2020, 2, 25);

        ProjectDto projectDto = ProjectDto.builder()
                                            .name(name)
                                            .description(description)
                                            .role(role)
                                            .projectLink(projectLink)
                                            .startedAt(startedAt)
                                            .endedAt(endedAt)
                                            .build();

        mockMvc.perform(post(projectUrl)
                                    .accept(MediaTypes.HAL_JSON_VALUE)
                                    .header(HttpHeaders.AUTHORIZATION, jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(projectDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("description", is(description)))
                .andExpect(jsonPath("role", is(role)))
                .andExpect(jsonPath("projectLink", is(projectLink)))
                .andExpect(jsonPath("startedAt", is(startedAt.toString())))
                .andExpect(jsonPath("endedAt", is(endedAt.toString())))
                .andExpect(jsonPath("account").isNotEmpty());
    }

    @Test
    @DisplayName("프로젝트 정상적으로 수정하기")
    void updateProject() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        Project project = createProject(newAccount);

        String updateRole = "프론트 엔드 개발을 맡았었음.";
        project.setRole(updateRole);

        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);

        mockMvc.perform(put(projectUrl + "{projectId}", project.getId())
                                    .accept(MediaTypes.HAL_JSON_VALUE)
                                    .header(HttpHeaders.AUTHORIZATION, jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(projectDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").isNotEmpty())
                .andExpect(jsonPath("description").isNotEmpty())
                .andExpect(jsonPath("role", is(updateRole)))
                .andExpect(jsonPath("projectLink").isNotEmpty())
                .andExpect(jsonPath("startedAt").isNotEmpty())
                .andExpect(jsonPath("endedAt").isNotEmpty());
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 프로젝트의 수정을 요청")
    void updateProject_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        Project project = createProject(newAccount);

        String updateRole = "프론트 엔드 개발을 맡았었음.";
        project.setRole(updateRole);

        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);

        mockMvc.perform(put(projectUrl + "{projectId}", -1)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(PROJECTNOTFOUND)));
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 프로젝트 수정을 요청할 때 Bad Request 반환")
    void updateProject_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        Project project = createProject(newAccount);

        String updateRole = "프론트 엔드 개발을 맡았었음.";
        project.setRole(updateRole);

        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);

        mockMvc.perform(put(projectUrl + "{projectId}", project.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)));
    }

    @Test
    @DisplayName("정상적으로 프로젝트 삭제하기")
    void deleteProject() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        Project project = createProject(newAccount);

        mockMvc.perform(delete(projectUrl + "{projectId}", project.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 프로젝트의 삭제를 요청")
    void deleteProject_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        createProject(newAccount);

        mockMvc.perform(delete(projectUrl + "{projectId}", -1)
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(PROJECTNOTFOUND)));
    }

    @Test
    @DisplayName("권한이 없는 유저가 다른 유저 프로젝트 삭제를 요청할 때 Bad Request 반환")
    void deleteProject_invalid_user() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        Project project = createProject(newAccount);

        mockMvc.perform(delete(projectUrl + "{projectId}", project.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)));
    }

    private Project createProject(Account account) {
        String name = "픽미 프로젝트";
        String description = "부산 IT 연합 동아리 D&D에 참가하여 개발";
        String role = "백엔드 개발";
        String projectLink = "https://github.com/DND-PickMe";
        LocalDate startedAt = LocalDate.of(2019, 12, 21);
        LocalDate endedAt = LocalDate.of(2020, 2, 25);

        Project project = Project.builder()
                .name(name)
                .description(description)
                .role(role)
                .projectLink(projectLink)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .account(account)
                .build();

        Project newProject = projectRepository.save(project);

        assertNotNull(newProject.getId());
        assertNotNull(newProject.getName());
        assertNotNull(newProject.getDescription());
        assertNotNull(newProject.getProjectLink());
        assertNotNull(newProject.getRole());
        assertNotNull(newProject.getStartedAt());
        assertNotNull(newProject.getEndedAt());
        assertNotNull(newProject.getAccount());

        return newProject;
    }

    private String generateBearerToken() {
        Account newAccount = createAccount();
        return "Bearer " + jwtProvider.generateToken(newAccount);
    }

    private String generateBearerToken_need_account(Account newAccount) {
        return "Bearer " + jwtProvider.generateToken(newAccount);
    }
}