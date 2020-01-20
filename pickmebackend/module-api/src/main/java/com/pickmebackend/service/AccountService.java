package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.error.ErrorMessageConstant;
import com.pickmebackend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.pickmebackend.error.ErrorMessageConstant.*;

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

    public ResponseEntity<?> updateAccount(Long accountId, AccountDto accountDto) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Account account = accountOptional.get();
        modelMapper.map(accountDto, account);
        return new ResponseEntity<>(accountRepository.save(account), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteAccount(Long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        accountRepository.delete(accountOptional.get());
        return ResponseEntity.ok().build();
    }
}
