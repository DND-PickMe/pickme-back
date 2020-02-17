package com.pickmebackend.service;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.domain.dto.account.AccountListResponseDto;
import com.pickmebackend.domain.dto.account.AccountResponseDto;
import com.pickmebackend.domain.enums.UserRole;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;

@Service
@RequiredArgsConstructor
public class AccountService{

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final Environment environment;

    public ResponseEntity<?> saveAccount(AccountDto accountDto) {
        Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        account.setUserRole(UserRole.USER);
        account.setCreatedAt(LocalDateTime.now());
        account.setImage(defaultImage());
        return new ResponseEntity<>(accountRepository.save(account), HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateAccount(Long accountId, AccountDto accountDto, Account currentUser) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }

        if (!accountId.equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        Account account = accountOptional.get();
        modelMapper.map(accountDto, account);
        return new ResponseEntity<>(accountRepository.save(account), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteAccount(Long accountId, Account currentUser) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }

        if (!accountId.equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        accountRepository.delete(accountOptional.get());
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getAccount(Account currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>(USERNOTFOUND, HttpStatus.BAD_REQUEST);
        }
        Optional<Account> accountOptional = accountRepository.findById(currentUser.getId());
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(accountOptional.get());
    }

    public boolean isDuplicatedAccount(AccountDto accountDto) {
        return accountRepository.findByEmail(accountDto.getEmail()).isPresent();
    }

    private String defaultImage() {
        final String port = environment.getProperty("local.server.port");
        final String USER_DEFAULT_IMG = "default_user.png";
        final String requestURI = "/api/images/";
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                .port(port)
                                                                .path(requestURI)
                                                                .path(USER_DEFAULT_IMG)
                                                                .toUriString();
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
