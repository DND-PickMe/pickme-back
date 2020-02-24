package com.pickmebackend.controller;

import com.pickmebackend.controller.common.BaseControllerTest;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Project;
import com.pickmebackend.domain.dto.project.ProjectRequestDto;
import com.pickmebackend.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerTest extends BaseControllerTest {

    @Autowired
    ProjectRepository projectRepository;

    private final String projectUrl = "/api/projects/";

    @BeforeEach
    void cleanUp() {
        projectRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @AfterEach
    void setUp() {
        projectRepository.deleteAll();
        accountRepository.deleteAll();
        enterpriseRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 프로젝트 생성하기")
    void saveProject() throws Exception {
        jwt = generateBearerToken();
        String name = "픽미 프로젝트";
        String description = "부산 IT 연합 동아리 D&D에 참가하여 개발";
        String role = "백엔드 개발";
        String projectLink = "https://github.com/DND-PickMe";
        LocalDate startedAt = LocalDate.of(2019, 12, 21);
        LocalDate endedAt = LocalDate.of(2020, 2, 25);

        ProjectRequestDto projectRequestDto = ProjectRequestDto.builder()
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
                                    .content(objectMapper.writeValueAsString(projectRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name", is(name)))
                .andExpect(jsonPath("description", is(description)))
                .andExpect(jsonPath("role", is(role)))
                .andExpect(jsonPath("projectLink", is(projectLink)))
                .andExpect(jsonPath("startedAt", is(startedAt.toString())))
                .andExpect(jsonPath("endedAt", is(endedAt.toString())))
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-project").exists())
                .andExpect(jsonPath("_links.delete-project").exists())
                .andDo(document("create-project",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-project").description("link to update project"),
                                linkWithRel("delete-project").description("link to delete project")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("프로젝트 이름"),
                                fieldWithPath("role").description("프로젝트에서의 역할"),
                                fieldWithPath("description").description("프로젝트 설명"),
                                fieldWithPath("startedAt").description("프로젝트 시작 날짜"),
                                fieldWithPath("endedAt").description("프로젝트 완료 날짜"),
                                fieldWithPath("projectLink").description("프로젝트 관련 주소")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header"),
                                headerWithName(HttpHeaders.LOCATION).description("Location Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("프로젝트 식별자"),
                                fieldWithPath("name").description("프로젝트 이름"),
                                fieldWithPath("role").description("프로젝트에서의 역할"),
                                fieldWithPath("description").description("프로젝트 설명"),
                                fieldWithPath("startedAt").description("프로젝트 시작 날짜"),
                                fieldWithPath("endedAt").description("프로젝트 완료 날짜"),
                                fieldWithPath("projectLink").description("프로젝트 관련 주소"),
                                fieldWithPath("account.id").description("프로젝트 등록자 식별자"),
                                fieldWithPath("account.email").description("프로젝트 등록자 이메일"),
                                fieldWithPath("account.nickName").description("프로젝트 등록자 닉네임"),
                                fieldWithPath("account.technology").description("프로젝트 등록자 기술 스택"),
                                fieldWithPath("account.favorite").description("프로젝트 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("프로젝트 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("프로젝트 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("프로젝트 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("프로젝트 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("프로젝트 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("프로젝트 등록자의 경력 사항"),
                                fieldWithPath("account.licenses").description("프로젝트 등록자의 자격증"),
                                fieldWithPath("account.prizes").description("프로젝트 등록자의 수상 내역"),
                                fieldWithPath("account.projects[*].*").ignored(),
                                fieldWithPath("account.selfInterviews").description("프로젝트 등록자의 셀프 인터뷰"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                        ))
        ;
    }

    @Test
    @DisplayName("기업 담당자가 프로젝트 생성할 시 Forbidden")
    void saveProject_forbidden() throws Exception {
        jwt = createEnterpriseJwt();

        String name = "픽미 프로젝트";
        String description = "부산 IT 연합 동아리 D&D에 참가하여 개발";
        String role = "백엔드 개발";
        String projectLink = "https://github.com/DND-PickMe";
        LocalDate startedAt = LocalDate.of(2019, 12, 21);
        LocalDate endedAt = LocalDate.of(2020, 2, 25);

        ProjectRequestDto projectRequestDto = ProjectRequestDto.builder()
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
                .content(objectMapper.writeValueAsString(projectRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    @DisplayName("프로젝트 정상적으로 수정하기")
    void updateProject() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        Project project = createProject(newAccount);

        String updateRole = "프론트 엔드 개발을 맡았었음.";
        project.setRole(updateRole);

        ProjectRequestDto projectRequestDto = modelMapper.map(project, ProjectRequestDto.class);

        mockMvc.perform(put(projectUrl + "{projectId}", project.getId())
                                    .accept(MediaTypes.HAL_JSON_VALUE)
                                    .header(HttpHeaders.AUTHORIZATION, jwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(projectRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").isNotEmpty())
                .andExpect(jsonPath("description").isNotEmpty())
                .andExpect(jsonPath("role", is(updateRole)))
                .andExpect(jsonPath("projectLink").isNotEmpty())
                .andExpect(jsonPath("startedAt").isNotEmpty())
                .andExpect(jsonPath("endedAt").isNotEmpty())
                .andExpect(jsonPath("account").isNotEmpty())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.create-project").exists())
                .andExpect(jsonPath("_links.delete-project").exists())
                .andDo(document("update-project",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-project").description("link to create project"),
                                linkWithRel("delete-project").description("link to delete project")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("Accept Header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 프로젝트 이름"),
                                fieldWithPath("role").description("수정할 프로젝트에서의 역할"),
                                fieldWithPath("description").description("수정할 프로젝트 설명"),
                                fieldWithPath("startedAt").description("수정할 프로젝트 시작 날짜"),
                                fieldWithPath("endedAt").description("수정할 프로젝트 완료 날짜"),
                                fieldWithPath("projectLink").description("수정할 프로젝트 관련 주소")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("수정된 프로젝트 식별자"),
                                fieldWithPath("name").description("수정된 프로젝트 이름"),
                                fieldWithPath("role").description("수정된 프로젝트에서의 역할"),
                                fieldWithPath("description").description("수정된 프로젝트 설명"),
                                fieldWithPath("startedAt").description("수정된 프로젝트 시작 날짜"),
                                fieldWithPath("endedAt").description("수정된 프로젝트 완료 날짜"),
                                fieldWithPath("projectLink").description("수정된 프로젝트 관련 주소"),
                                fieldWithPath("account.id").description("수정된 프로젝트 등록자 식별자"),
                                fieldWithPath("account.email").description("수정된 프로젝트 등록자 이메일"),
                                fieldWithPath("account.nickName").description("수정된 프로젝트 등록자 닉네임"),
                                fieldWithPath("account.technology").description("수정된 프로젝트 등록자 기술 스택"),
                                fieldWithPath("account.favorite").description("수정된 프로젝트 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("수정된 프로젝트 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("수정된 프로젝트 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("수정된 프로젝트 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("수정된 프로젝트 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("수정된 프로젝트 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("수정된 프로젝트 등록자의 경력 사항"),
                                fieldWithPath("account.licenses").description("수정된 프로젝트 등록자의 자격증"),
                                fieldWithPath("account.prizes").description("수정된 프로젝트 등록자의 수상 내역"),
                                fieldWithPath("account.projects[*].*").ignored(),
                                fieldWithPath("account.selfInterviews").description("수정된 프로젝트 등록자의 셀프 인터뷰"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 프로젝트의 수정을 요청")
    void updateProject_not_found() throws Exception {
        Account newAccount = createAccount();
        jwt = generateBearerToken_need_account(newAccount);
        Project project = createProject(newAccount);

        String updateRole = "프론트 엔드 개발을 맡았었음.";
        project.setRole(updateRole);

        ProjectRequestDto projectRequestDto = modelMapper.map(project, ProjectRequestDto.class);

        mockMvc.perform(put(projectUrl + "{projectId}", -1)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequestDto)))
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

        ProjectRequestDto projectRequestDto = modelMapper.map(project, ProjectRequestDto.class);

        mockMvc.perform(put(projectUrl + "{projectId}", project.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(UNAUTHORIZEDUSER)));
    }

    @Test
    @DisplayName("기업 담당자가 프로젝트 수정을 요청할 때 Forbidden")
    void updateProject_forbidden() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        Project project = createProject(newAccount);

        jwt = createEnterpriseJwt();

        String updateRole = "프론트 엔드 개발을 맡았었음.";
        project.setRole(updateRole);

        ProjectRequestDto projectRequestDto = modelMapper.map(project, ProjectRequestDto.class);

        mockMvc.perform(put(projectUrl + "{projectId}", project.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.create-project").exists())
                .andDo(document("delete-project",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-project").description("link to create project")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization Header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type Header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("삭제된 프로젝트 식별자"),
                                fieldWithPath("name").description("삭제된 프로젝트 이름"),
                                fieldWithPath("role").description("삭제된 프로젝트에서의 역할"),
                                fieldWithPath("description").description("삭제된 프로젝트 설명"),
                                fieldWithPath("startedAt").description("삭제된 프로젝트 시작 날짜"),
                                fieldWithPath("endedAt").description("삭제된 프로젝트 완료 날짜"),
                                fieldWithPath("projectLink").description("삭제된 프로젝트 관련 주소"),
                                fieldWithPath("account.id").description("삭제된 프로젝트 등록자 식별자"),
                                fieldWithPath("account.email").description("삭제된 프로젝트 등록자 이메일"),
                                fieldWithPath("account.nickName").description("삭제된 프로젝트 등록자 닉네임"),
                                fieldWithPath("account.technology").description("삭제된 프로젝트 등록자 기술 스택"),
                                fieldWithPath("account.favorite").description("삭제된 프로젝트 등록자를 좋아요한 사용자 목록"),
                                fieldWithPath("account.userRole").description("삭제된 프로젝트 등록자의 권한"),
                                fieldWithPath("account.createdAt").description("삭제된 프로젝트 등록자의 생성 날짜"),
                                fieldWithPath("account.oneLineIntroduce").description("삭제된 프로젝트 등록자의 한 줄 소개"),
                                fieldWithPath("account.image").description("삭제된 프로젝트 등록자의 프로필 이미지"),
                                fieldWithPath("account.enterprise").description("삭제된 프로젝트 등록자의 기업 정보"),
                                fieldWithPath("account.experiences").description("삭제된 프로젝트 등록자의 경력 사항"),
                                fieldWithPath("account.licenses").description("삭제된 프로젝트 등록자의 자격증"),
                                fieldWithPath("account.prizes").description("삭제된 프로젝트 등록자의 수상 내역"),
                                fieldWithPath("account.projects[*].*").ignored(),
                                fieldWithPath("account.selfInterviews").description("삭제된 프로젝트 등록자의 셀프 인터뷰"),
                                fieldWithPath("_links.*.*").ignored()
                        )
                ))
        ;
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

    @Test
    @DisplayName("기업 담당자가 프로젝트 삭제를 요청할 때 Forbidden")
    void deleteProject_forbidden() throws Exception {
        Account newAccount = createAccount();
        Account anotherAccount = createAnotherAccount();
        jwt = generateBearerToken_need_account(anotherAccount);
        Project project = createProject(newAccount);

        jwt = createEnterpriseJwt();

        mockMvc.perform(delete(projectUrl + "{projectId}", project.getId())
                .header(HttpHeaders.AUTHORIZATION, jwt))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
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