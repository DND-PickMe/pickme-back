package com.pickmebackend.domain.dto.verificationCode;

import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SendCodeRequestDto {

    @Email(message = "이메일 형식이 아닙니다.") @NotBlank(message = "이메일은 반드시 입력되어야 합니다.")
    private String email;

}
