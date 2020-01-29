package com.pickmebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pickmebackend.config.jwt.JwtProvider;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.EnterpriseDto;
import com.pickmebackend.properties.AppProperties;
import com.pickmebackend.repository.EnterpriseRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.UUID;
import java.util.stream.Stream;
import static com.pickmebackend.error.ErrorMessageConstant.DUPLICATEDUSER;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class EnterpriseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EnterpriseRepository enterpriseRepository;

    @Autowired
    AppProperties appProperties;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    JwtProvider jwtProvider;

    private String jwt;

    // TODO POST 정상적으로 기업담담자 생성(Status : 201 Created, Body : 생성된 기업담당자 객체)
    @Test
    @DisplayName("정상적으로 기업담담자 생성")
    void save_enterprise() throws Exception {
        EnterpriseDto enterpriseDto = createEnterpriseDto();

        ResultActions actions = this.mockMvc.perform(post("/api/enterprises")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestCeoName()))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("registrationNumber").value(appProperties.getTestRegistrationNumber()))
                .andExpect(jsonPath("name").value(appProperties.getTestName()))
                .andExpect(jsonPath("address").value(appProperties.getTestAddress()))
                .andExpect(jsonPath("ceoName").value(appProperties.getTestCeoName()))
                .andExpect(jsonPath("createdAt").exists())
        ;
        String contentAsString = actions.andReturn().getResponse().getContentAsString();
        Enterprise enterprise = objectMapper.readValue(contentAsString, Enterprise.class);
        assertNotNull(enterprise.getId());
        assertEquals(enterprise.getEmail(), appProperties.getTestEmail());
        assertEquals(enterprise.getRegistrationNumber(), appProperties.getTestRegistrationNumber());
        assertEquals(enterprise.getName(), appProperties.getTestName());
        assertEquals(enterprise.getAddress(), appProperties.getTestAddress());
        assertEquals(enterprise.getCeoName(), appProperties.getTestCeoName());
    }

    // TODO POST 저장 시 동일한 email을 가진 유저가 존재할 경우(Status : 400 Bad Request, Body : ErrorMessage 객체)
    @Test
    @DisplayName("기업 담당자 저장 시 동일한 email을 가진 유저가 존재할 경우 Bad Request 반환")
    void check_duplicated_enterprise() throws Exception {
        Enterprise enterprise = createEnterprise();
        EnterpriseDto enterpriseDto = createEnterpriseDto();

        this.mockMvc.perform(post("/api/enterprises")
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", Matchers.is(DUPLICATEDUSER)))
        ;
    }


    // TODO POST 저장 시 필수로 들어와야할 필드값 중 하나라도 들어오지 않을 경우 - 빈 문자열 (Status : 400 Bad Request, Body : Errors 객체)
    @ParameterizedTest
    @DisplayName("기업 담당자 저장시 하나의 필드라도 공백이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForEmptyStringCheck")
    void check_save_empty_input_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        EnterpriseDto enterpriseDto = EnterpriseDto.builder()
                .email(email)
                .password(password)
                .registrationNumber(registrationNumber)
                .name(name)
                .address(address)
                .ceoName(ceoName)
                .build();

        this.mockMvc.perform(post("/api/enterprises")
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
        ;
    }



    // TODO POST 저장 시 필수로 들어와야할 필드값 중 하나라도 들어오지 않을 경우 - null (Status : 400 Bad Request, Body : Errors 객체)
    @ParameterizedTest
    @DisplayName("기업 담당자 저장시 하나의 필드라도 null이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForNullCheck")
    void check_save_null_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        EnterpriseDto enterpriseDto = EnterpriseDto.builder()
                .email(email)
                .password(password)
                .registrationNumber(registrationNumber)
                .name(name)
                .address(address)
                .ceoName(ceoName)
                .build();

        this.mockMvc.perform(post("/api/enterprises")
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
        ;
    }


    // TODO PUT 정상적으로 수정(Status : 200 OK, Body : 수정된 기업담당자 객체)
    @Test
    @DisplayName("정상적으로 기업 담당자 수정")
    void update_enterprise() throws Exception {
        Enterprise enterprise = createEnterprise();
//        jwt = jwtProvider.generateToken(enterprise);
        EnterpriseDto enterpriseDto = modelMapper.map(enterprise, EnterpriseDto.class);

        enterpriseDto.setEmail("newEmail@email.com");
        enterpriseDto.setPassword("newPassword");
        enterpriseDto.setRegistrationNumber("newRegistrationNumber");
        enterpriseDto.setName("newName");
        enterpriseDto.setAddress("newAddress");
        enterpriseDto.setCeoName("newCeoName");

        this.mockMvc.perform(put("/api/enterprises/", enterprise.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value("newEmail@email.com"))
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("registrationNumber").value("newRegistrationNumber"))
                .andExpect(jsonPath("name").value("newName"))
                .andExpect(jsonPath("address").value("newAddress"))
                .andExpect(jsonPath("ceoName").value("newCeoName"))
                .andExpect(jsonPath("createdAt").exists())
        ;
    }



    // TODO PUT 데이터베이스에 저장되어 있지 않은 기업담당자 수정 요청(Status : 400 Bad Request, Body : ErrorMessage 객체)
    @Test
    @DisplayName("수정시 존재 하지 않는 기업 담당자 수정 할 경우 Bad Request 반환")
    void update_non_enterprise() throws Exception {
        Enterprise enterprise = createEnterprise();
//        jwt = jwtProvider.generateToken(enterprise);
        EnterpriseDto enterpriseDto = modelMapper.map(enterprise, EnterpriseDto.class);

        enterpriseDto.setEmail("newEmail@email.com");
        enterpriseDto.setPassword("newPassword");
        enterpriseDto.setRegistrationNumber("newRegistrationNumber");
        enterpriseDto.setName("newName");
        enterpriseDto.setAddress("newAddress");
        enterpriseDto.setCeoName("newCeoName");

        this.mockMvc.perform(put("/api/enterprises/", UUID.randomUUID().toString())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }


    // TODO PUT 수정 시 필수로 들어와야할 필드값 중 하나라도 들어오지 않을 경우 - 빈 문자열 (Status : 400 Bad Request, Body : Errors 객체)
    @ParameterizedTest
    @DisplayName("기업 담당자 수정시 하나의 필드라도 공백이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForEmptyStringCheck")
    void check_update_empty_string_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        Enterprise enterprise = createEnterprise();
//        jwt = jwtProvider.generateToken(enterprise);
        EnterpriseDto enterpriseDto = modelMapper.map(enterprise, EnterpriseDto.class);

        enterpriseDto.setEmail(email);
        enterpriseDto.setPassword(password);
        enterpriseDto.setRegistrationNumber(registrationNumber);
        enterpriseDto.setName(name);
        enterpriseDto.setAddress(address);
        enterpriseDto.setCeoName(ceoName);

        this.mockMvc.perform(put("/api/enterprises/", enterprise.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
        ;
    }


    // TODO PUT 수정 시 필수로 들어와야할 필드값 중 하나라도 들어오지 않을 경우 - null (Status : 400 Bad Request, Body : Errors 객체)
    @ParameterizedTest
    @DisplayName("기업 담당자 수정시 하나의 필드라도 null이 들어올 경우 Bad Request 반환")
    @MethodSource("streamForNullCheck")
    void check_update_null_enterprise(String email, String password, String registrationNumber, String name, String address, String ceoName) throws Exception {
        Enterprise enterprise = createEnterprise();
//        jwt = jwtProvider.generateToken(enterprise);
        EnterpriseDto enterpriseDto = modelMapper.map(enterprise, EnterpriseDto.class);

        enterpriseDto.setEmail(email);
        enterpriseDto.setPassword(password);
        enterpriseDto.setRegistrationNumber(registrationNumber);
        enterpriseDto.setName(name);
        enterpriseDto.setAddress(address);
        enterpriseDto.setCeoName(ceoName);

        this.mockMvc.perform(put("/api/enterprises/", enterprise.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enterpriseDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("[*].defaultMessage").exists())
                .andExpect(jsonPath("[*].code").exists())
                .andExpect(jsonPath("[*].objectName").exists())
                .andExpect(jsonPath("[*].field").exists())
        ;
    }

    // TODO GET 요청 시 정상적으로 한명의 기업담당자 불러오기(Status : 200, Body : Enterprise 객체)
    @Test
    @DisplayName("정상적으로 한명의 기업담당자 불러오기")
    void load_enterprise() throws Exception {
        Enterprise enterprise = createEnterprise();
//        jwt = jwtProvider.generateToken(enterprise);

        this.mockMvc.perform(get("/api/enterprises/", enterprise.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }


    // TODO GET 요청 시 저장되어 있지 않은 기업담당자 불러 오기(Status : 400, Body : ErrorMessage 객체)
    @Test
    @DisplayName("저장되어 있지 않은 기업담당자 불러 오기")
    void load_non_enterprise() throws Exception {
        Enterprise enterprise = createEnterprise();
//        jwt = jwtProvider.generateToken(enterprise);

        this.mockMvc.perform(get("/api/enterprises/", UUID.randomUUID().toString())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }


    // TODO GET 요청 시 정상적으로 모든 기업 담당자 불러오기(Status : 200, Body : List<Enterprise> : 미정)



    // TODO 정상적으로 삭제(Status : 200 OK)
    @Test
    @DisplayName("정상적으로 기업 담당자 삭제")
    void delete_enterprise() throws Exception {
        Enterprise enterprise = createEnterprise();
//        jwt = jwtProvider.generateToken(enterprise);

        this.mockMvc.perform(delete("/api/enterprises/", enterprise.getId())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }


    // TODO 데이터베이스에 저장되어 있지 않은 기업담당자 삭제 요청(Status : 400 Bad Request, Body : ErrorMessage 객체)
    @Test
    @DisplayName("데이터베이스에 저장되어 있지 않은 기업담당자 삭제 요청시 Bad Request 반환")
    void delete_non_enterprise() throws Exception {
        Enterprise enterprise = createEnterprise();
//        jwt = jwtProvider.generateToken(enterprise);

        this.mockMvc.perform(delete("/api/enterprises/", UUID.randomUUID().toString())
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is(USERNOTFOUND)))
        ;
    }



    private EnterpriseDto createEnterpriseDto() {
        return EnterpriseDto.builder()
                .email(appProperties.getTestEmail())
                .password(appProperties.getTestPassword())
                .registrationNumber(appProperties.getTestRegistrationNumber())
                .name(appProperties.getTestName())
                .address(appProperties.getTestAddress())
                .ceoName(appProperties.getTestCeoName())
                .build();
    }

    private Enterprise createEnterprise()   {
        Enterprise enterprise = modelMapper.map(createEnterpriseDto(), Enterprise.class);
        return enterpriseRepository.save(enterprise);
    }

    private static Stream<Arguments> streamForEmptyStringCheck() {
        return Stream.of(
                Arguments.of("", "password", "registraionNumber", "name", "address", "ceoName"),
                Arguments.of("email", "", "registraionNumber", "name", "address", "ceoName"),
                Arguments.of("email", "password", "", "name", "address", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "", "address", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "name", "", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "name", "address", "")
        );
    }

    private static Stream<Arguments> streamForNullCheck() {
        return Stream.of(
                Arguments.of(null, "password", "registraionNumber", "name", "address", "ceoName"),
                Arguments.of("email", null, "registraionNumber", "name", "address", "ceoName"),
                Arguments.of("email", "password", null, "name", "address", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", null, "address", "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "name", null, "ceoName"),
                Arguments.of("email", "password", "registraionNumber", "name", "address", null)
        );
    }
}