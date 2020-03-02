package com.pickmebackend.repository.account;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.account.AccountFilteringRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountRepositoryCustom {
    Page<Account> filterAccount(AccountFilteringRequestDto requestDto, Pageable pageable);
}
