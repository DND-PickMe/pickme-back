package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.EnterpriseDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.repository.AccountRepository;
import com.pickmebackend.repository.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> loadEnterprise(Long enterpriseId) {
        Optional<Account> accountOptional = accountRepository.findById(enterpriseId);
        Account account = accountOptional.get();

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    public ResponseEntity<?> saveEnterprise(EnterpriseDto enterpriseDto) {
        Account account = modelMapper.map(enterpriseDto, Account.class);
        account.setPassword(passwordEncoder.encode(enterpriseDto.getPassword()));
        account.setNickName(enterpriseDto.getName());
        account.setCreatedAt(LocalDateTime.now());
        account.setUserRole(UserRole.ENTERPRISE);

        Enterprise enterprise = modelMapper.map(enterpriseDto, Enterprise.class);
        Enterprise savedEnterprise = enterpriseRepository.save(enterprise);

        account.setEnterprise(savedEnterprise);
        Account savedAccount = accountRepository.save(account);

        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateEnterprise(Long enterpriseId, EnterpriseDto enterpriseDto) {
        Optional<Account> accountOptional = accountRepository.findById(enterpriseId);
        Account account = accountOptional.get();
        modelMapper.map(enterpriseDto, account);
        account.setPassword(passwordEncoder.encode(enterpriseDto.getPassword()));
        account.setNickName(enterpriseDto.getName());

        Optional<Enterprise> enterpriseOptional = enterpriseRepository.findById(account.getEnterprise().getId());
        Enterprise enterprise = enterpriseOptional.get();
        modelMapper.map(enterpriseDto, enterprise);
        Enterprise savedEnterprise = enterpriseRepository.save(enterprise);

        account.setEnterprise(savedEnterprise);
        Account savedAccount = accountRepository.save(account);

        return new ResponseEntity<>(savedAccount, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteEnterprise(Long enterpriseId) {
        accountRepository.deleteById(enterpriseId);
        return ResponseEntity.ok().build();
    }

    public boolean isDuplicatedEnterprise(EnterpriseDto enterpriseDto) {
        return accountRepository.findByEmail(enterpriseDto.getEmail()).isPresent();
    }

    public boolean isNonEnterprise(Long enterpriseId) {
        return !accountRepository.findById(enterpriseId).isPresent();
    }
}
