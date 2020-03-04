package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseResponseDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.repository.EnterpriseRepository;
import com.pickmebackend.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    public EnterpriseResponseDto loadProfile(Account account) {
        Enterprise enterprise = account.getEnterprise();
        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(enterprise, EnterpriseResponseDto.class);
        enterpriseResponseDto.setEmail(account.getEmail());

        return enterpriseResponseDto;
    }

    public EnterpriseResponseDto loadEnterprise(Account account) {
        Enterprise enterprise = account.getEnterprise();
        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(enterprise, EnterpriseResponseDto.class);
        enterpriseResponseDto.setEmail(account.getEmail());

        return enterpriseResponseDto;
    }

    public Page<Enterprise> loadEnterprisesWithFilter(String name, Pageable pageable) {
        if(StringUtils.isEmpty(name))
            return this.enterpriseRepository.findAllEnterprisesDesc(pageable);

        return this.enterpriseRepository.findAllByNameContainingOrderByName(name, pageable);

    }

    public EnterpriseResponseDto saveEnterprise(EnterpriseRequestDto enterpriseRequestDto) {
        Account account = modelMapper.map(enterpriseRequestDto, Account.class);
        account.setPassword(passwordEncoder.encode(enterpriseRequestDto.getPassword()));
        account.setNickName(enterpriseRequestDto.getName());
        account.setCreatedAt(LocalDateTime.now());
        account.setUserRole(UserRole.ENTERPRISE);

        Enterprise enterprise = modelMapper.map(enterpriseRequestDto, Enterprise.class);
        Enterprise savedEnterprise = this.enterpriseRepository.save(enterprise);

        account.setEnterprise(savedEnterprise);
        Account savedAccount = this.accountRepository.save(account);

        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(savedEnterprise, EnterpriseResponseDto.class);
        enterpriseResponseDto.setEmail(savedAccount.getEmail());
        enterpriseResponseDto.setAccount(savedAccount);

        return enterpriseResponseDto;
    }

    public EnterpriseResponseDto updateEnterprise(Account account, EnterpriseRequestDto enterpriseRequestDto) {
        modelMapper.map(enterpriseRequestDto, account);
        account.setPassword(passwordEncoder.encode(enterpriseRequestDto.getPassword()));
        account.setNickName(enterpriseRequestDto.getName());

        Optional<Enterprise> enterpriseOptional = this.enterpriseRepository.findById(account.getEnterprise().getId());
        Enterprise enterprise = enterpriseOptional.get();
        modelMapper.map(enterpriseRequestDto, enterprise);
        Enterprise modifiedEnterprise = this.enterpriseRepository.save(enterprise);

        account.setEnterprise(modifiedEnterprise);
        Account modifiedAccount = this.accountRepository.save(account);

        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(modifiedEnterprise, EnterpriseResponseDto.class);
        enterpriseResponseDto.setEmail(modifiedAccount.getEmail());
        enterpriseResponseDto.setAccount(modifiedAccount);

        return enterpriseResponseDto;
    }

    public EnterpriseResponseDto deleteEnterprise(Account account) {
        EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(account, EnterpriseResponseDto.class);
        this.accountRepository.delete(account);

        return enterpriseResponseDto;
    }

    public boolean isDuplicatedEnterprise(EnterpriseRequestDto enterpriseRequestDto) {
        return this.accountRepository.findByEmail(enterpriseRequestDto.getEmail()).isPresent();
    }

    public boolean isNonEnterprise(Long enterpriseId) {
        return !this.accountRepository.findById(enterpriseId).isPresent();
    }
}
