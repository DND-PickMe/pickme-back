package com.pickmebackend.domain.dto.enterprise;

import com.pickmebackend.domain.Account;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EnterpriseResponseDto {

    private Long id;

    private String email;

    private String registrationNumber;

    private String name;

    private String address;

    private String ceoName;

    private Account account;
}
