package com.pickmebackend.domain.dto.verificationCode;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VerifyCodeResponseDto {

    private String email;

    private String code;

    private boolean isVerified = false;

}
