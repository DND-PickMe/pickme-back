package com.pickmebackend.service;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.AccountTech;
import com.pickmebackend.domain.Technology;
import com.pickmebackend.domain.dto.account.AccountInitialRequestDto;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
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

    private final AccountTechRepository accountTechRepository;

    private final ErrorsFormatter errorsFormatter;

    public AccountResponseDto loadProfile(Account account) {
        return new AccountResponseDto(account);
    }

    @Transactional
    public AccountResponseDto loadAccount(Long accountId, Account account, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        Cookie checkCookie = null;

        if (cookies != null && cookies.length > 0) {
            for (Cookie newCookie : cookies) {
                if (newCookie.getName().equals("cookie" + accountId)) {
                    checkCookie = newCookie;
                }
            }
        }

        if (checkCookie == null) {
            Cookie cookie = new Cookie("cookie" + accountId, "|" + accountId + "|");
            response.addCookie(cookie);
            account.setHits(account.getHits() + 1L);
            accountRepository.save(account);
        }

        return new AccountResponseDto(account);
    }

    public Page<Account> loadAllAccounts(Pageable pageable, String orderBy) {
        if ("favorite".equals(orderBy)) {
            return accountRepository.findAllAccountsDescAndOrderByFavorite(pageable);
        } else if ("hits".equals(orderBy)) {
            return accountRepository.findAllAccountsDescAndOrderByHits(pageable);
        }
        return this.accountRepository.findAllAccountsDesc(pageable);
    }

    @Transactional
    public AccountResponseDto saveAccount(AccountInitialRequestDto accountDto) {
        Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        account.setValue();
        Account savedAccount = this.accountRepository.save(account);
        return new AccountResponseDto(savedAccount);
    }

    public AccountResponseDto updateAccount(Account account, AccountRequestDto accountDto) {
        modelMapper.map(accountDto, account);

        updateTechnologies(account, accountDto);
        updatePositions(account, accountDto);

        List<AccountTech> allByAccount_id = accountTechRepository.findAllByAccount_Id(account.getId());
        account.setAccountTechSet(new HashSet<>(allByAccount_id));
        Account modifiedAccount = this.accountRepository.save(account);

        return new AccountResponseDto(modifiedAccount);
    }

    private void updatePositions(Account account, AccountRequestDto accountDto) {
        if (account.getPositions() != null) {
            account.getPositions().clear();
        }
        if (accountDto.getPositions() != null) {
            accountDto.getPositions()
                    .forEach(e -> account.getPositions().add(e));
        }
    }

    private void updateTechnologies(Account account, AccountRequestDto accountDto) {
        if (account.getAccountTechSet() != null) {
            account.getAccountTechSet().forEach(e -> accountTechRepository.deleteById(e.getId()));
            account.getAccountTechSet().clear();
        }
        if (accountDto.getTechnologies() != null) {
            accountDto.getTechnologies()
                    .forEach(tech -> account.getAccountTechSet().add(accountTechRepository.save(
                                                                        AccountTech.builder()
                                                                                .account(account)
                                                                                .technology(tech)
                                                                                .build())));
        }
    }

    public AccountResponseDto deleteAccount(Account account) {
        AccountResponseDto accountResponseDto = modelMapper.map(account, AccountResponseDto.class);
        accountRepository.delete(account);

        return accountResponseDto;
    }

    public boolean isDuplicatedAccount(AccountInitialRequestDto accountDto) {
        return accountRepository.findByEmail(accountDto.getEmail()).isPresent();
    }

    public ResponseEntity<?> favorite(Long accountId, Account currentUser) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Account favoritedAccount = accountOptional.get();
        favoritedAccount.addFavorite(currentUser);
        Account savedAccount = accountRepository.save(favoritedAccount);

        return new ResponseEntity<>(new AccountResponseDto(savedAccount), HttpStatus.OK);
    }

    public ResponseEntity<?> getFavoriteUsers(Long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USERNOTFOUND), HttpStatus.OK);
        }
        Account account = accountOptional.get();
        List<AccountListResponseDto> accountList = account.getFavorite().stream()
                .map(AccountListResponseDto::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(accountList, HttpStatus.OK);
    }
}
