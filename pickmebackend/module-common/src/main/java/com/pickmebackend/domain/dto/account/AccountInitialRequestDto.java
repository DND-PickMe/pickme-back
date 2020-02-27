package com.pickmebackend.domain.dto.account;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountInitialRequestDto {

    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    private String nickName;

    @Nullable
    private String oneLineIntroduce;
}
