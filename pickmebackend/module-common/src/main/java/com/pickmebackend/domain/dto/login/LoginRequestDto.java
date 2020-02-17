package com.pickmebackend.domain.dto.login;

import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginRequestDto {

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8, max = 20)
    private String password;

}
