package com.pickmebackend.domain.dto.enterprise;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
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

    public EnterpriseResponseDto(Enterprise enterprise) {
        this.id = enterprise.getId();
        this.email = enterprise.getAccount().getEmail();
        this.registrationNumber = enterprise.getRegistrationNumber();
        this.name = enterprise.getName();
        this.address = enterprise.getAddress();
        this.ceoName = enterprise.getCeoName();
        this.account = enterprise.getAccount();
    }
}
