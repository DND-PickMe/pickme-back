package com.pickmebackend.domain.dto.enterprise;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SuggestionDto {

    private Enterprise enterprise;

    private Account account;
}
