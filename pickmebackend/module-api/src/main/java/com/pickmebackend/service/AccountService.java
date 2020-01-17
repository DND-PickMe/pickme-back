package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    public ResponseEntity<?> saveAccount(AccountDto accountDto) {
        Account account = modelMapper.map(accountDto, Account.class);
        account.setCreatedAt(LocalDateTime.now());
        return new ResponseEntity<>(accountRepository.save(account), HttpStatus.CREATED);
    }
}
