package com.pickmebackend.domain.dto.verificationCode;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SendCodeResponseDto {

    private String email;

    private String code;

}
