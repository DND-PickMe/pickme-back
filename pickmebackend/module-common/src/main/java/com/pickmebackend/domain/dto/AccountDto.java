package com.pickmebackend.domain.dto;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccountDto {

    private String email;

    private String password;

    private String nickName;
}
