package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.AccountTech;
import com.pickmebackend.domain.Technology;
import com.pickmebackend.domain.dto.account.AccountListResponseDto;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.dto.account.AccountResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.AccountRepository;
import com.pickmebackend.repository.AccountTechRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;

@Service
@RequiredArgsConstructor
public class AccountService{

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    public AccountResponseDto loadProfile(Account account) {
        return modelMapper.map(account, AccountResponseDto.class);
    }
    private final AccountTechRepository accountTechRepository;

    public List<AccountResponseDto> getAllAccounts(Pageable pageable) {
        return this.accountRepository.findAllAccountsDesc(pageable)
                .stream()
                .map(AccountResponseDto::new)
                .collect(Collectors.toList());
    }

    public AccountResponseDto loadAccount(Account account) {
        return modelMapper.map(account, AccountResponseDto.class);
    }

    public Page<Account> loadAllAccounts(Pageable pageable) {
        return this.accountRepository.findAllAccountsDesc(pageable);
    }

    @Transactional
    public AccountResponseDto saveAccount(AccountRequestDto accountDto) {
        Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        account.setValue();
        Account savedAccount = this.accountRepository.save(account);

        List<Technology> technologyList = accountDto.getTechnologies();

        if (technologyList != null) {
            technologyList.forEach(tech -> savedAccount.getAccountTechSet().add(
                    accountTechRepository.save(AccountTech.builder()
                            .account(savedAccount)
                            .technology(tech)
                            .build()))
            );
        }

        AccountResponseDto accountResponseDto = modelMapper.map(savedAccount, AccountResponseDto.class);
        accountResponseDto.toTech(savedAccount);
        return accountResponseDto;
    }

    public AccountResponseDto updateAccount(Account account, AccountRequestDto accountDto) {
        modelMapper.map(accountDto, account);
        if (account.getAccountTechSet() != null) {
            account.getAccountTechSet().forEach(e -> accountTechRepository.deleteById(e.getId()));
            account.getAccountTechSet().clear();
        }
        if (accountDto.getTechnologies() != null) {
            accountDto.getTechnologies()
                    .forEach(tech -> account.getAccountTechSet().add(accountTechRepository.save(AccountTech.builder()
                            .account(account)
                            .technology(tech)
                            .build())));
        }
        Account modifiedAccount = this.accountRepository.save(account);

        AccountResponseDto accountResponseDto = modelMapper.map(modifiedAccount, AccountResponseDto.class);
        accountResponseDto.toTech(modifiedAccount);
        return accountResponseDto;
    }

    public AccountResponseDto deleteAccount(Account account) {
        AccountResponseDto accountResponseDto = modelMapper.map(account, AccountResponseDto.class);
        accountRepository.delete(account);

        return accountResponseDto;
    }

    public boolean isDuplicatedAccount(AccountRequestDto accountDto) {
        return accountRepository.findByEmail(accountDto.getEmail()).isPresent();
    }

    public ResponseEntity<?> favorite(Long accountId, Account currentUser) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Account favoritedAccount = accountOptional.get();
        favoritedAccount.addFavorite(currentUser);
        Account savedAccount = accountRepository.save(favoritedAccount);

        return new ResponseEntity<>(new AccountResponseDto(savedAccount), HttpStatus.OK);
    }

    public ResponseEntity<?> getFavoriteUsers(Long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.OK);
        }
        Account account = accountOptional.get();
        List<AccountListResponseDto> accountList = account.getFavorite().stream()
                .map(AccountListResponseDto::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(accountList, HttpStatus.OK);
    }
}
