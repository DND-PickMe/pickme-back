package com.pickmebackend.domain.dto.account;

import com.pickmebackend.domain.Account;
import lombok.Getter;

@Getter
public class AccountListResponseDto {

    private Long id;

    private String email;

    private String image;

    public AccountListResponseDto(Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.image = account.getImage();
    }
}
