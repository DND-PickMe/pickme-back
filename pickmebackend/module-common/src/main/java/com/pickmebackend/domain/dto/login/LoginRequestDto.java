package com.pickmebackend.domain.dto.login;

import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginRequestDto {

    @NotBlank(message = "이메일은 반드시 입력되어야 합니다.") @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 반드시 입력되어야 합니다.") @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 미만이어야 합니다.")
    private String password;

}
