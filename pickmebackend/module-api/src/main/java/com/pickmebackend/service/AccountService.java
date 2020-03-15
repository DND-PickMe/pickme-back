package com.pickmebackend.service;

import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.AccountTech;
import com.pickmebackend.domain.VerificationCode;
import com.pickmebackend.domain.dto.account.*;
import com.pickmebackend.domain.dto.verificationCode.SendCodeResponseDto;
import com.pickmebackend.domain.dto.verificationCode.VerifyCodeRequestDto;
import com.pickmebackend.domain.dto.verificationCode.VerifyCodeResponseDto;
import com.pickmebackend.repository.VerificationCodeRepository;
import com.pickmebackend.repository.account.AccountRepository;
import com.pickmebackend.repository.account.AccountTechRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static com.pickmebackend.error.ErrorMessage.UNVERIFIED_USER;
import static com.pickmebackend.error.ErrorMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AccountService{

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final AccountTechRepository accountTechRepository;

    private final ErrorsFormatter errorsFormatter;

    private final JavaMailSender javaMailSender;

    private final TemplateEngine templateEngine;

    private final VerificationCodeRepository verificationCodeRepository;

    public AccountResponseDto loadProfile(Account account) {
        return new AccountResponseDto(account);
    }

    @Transactional
    public AccountFavoriteFlagResponseDto loadAccount(Long accountId, Account account, HttpServletRequest request, HttpServletResponse response, Account currentUser) {
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

        return new AccountFavoriteFlagResponseDto(account, currentUser);
    }

    public Page<Account> loadAccountsWithFilter(AccountFilteringRequestDto requestDto, Pageable pageable) {
        return this.accountRepository.filterAccount(requestDto, pageable);
    }

    public ResponseEntity<?> sendVerificationCode(String email) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < 6; i++)  {
            code.append(random.nextInt(10));
        }
        String content = this.build(code.toString());

        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[PickMe] 인증 번호가 도착했습니다!");
            mimeMessageHelper.setText(content, true);
        };
        this.javaMailSender.send(mimeMessagePreparator);

        SendCodeResponseDto sendCodeResponseDto = SendCodeResponseDto.builder()
                .email(email)
                .code(code.toString())
                .build();

        VerificationCode verificationCode = modelMapper.map(sendCodeResponseDto, VerificationCode.class);
        this.verificationCodeRepository.save(verificationCode);

        return new ResponseEntity<>(sendCodeResponseDto, HttpStatus.CREATED);
    }

    //@Transactional
    public ResponseEntity<?> verifyCode(VerifyCodeRequestDto verifyCodeRequestDto) {
        Optional<VerificationCode> optionalVerificationCode = this.verificationCodeRepository.findByEmail(verifyCodeRequestDto.getEmail());
        if(!optionalVerificationCode.isPresent())   {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        VerificationCode verificationCode = optionalVerificationCode.get();
        if(!verifyCodeRequestDto.getCode().trim().equals(verificationCode.getCode())) {
            verificationCodeRepository.delete(verificationCode);
            return errorsFormatter.badRequest(UNVERIFIED_USER.getValue());
        }
        verificationCodeRepository.delete(verificationCode);
        verificationCode.setVerified(true);

        return new ResponseEntity<>(modelMapper.map(verificationCode, VerifyCodeResponseDto.class), HttpStatus.OK);
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

        List<AccountTech> allAccount = accountTechRepository.findAllByAccount_Id(account.getId());
        account.setAccountTechSet(new HashSet<>(allAccount));
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
        accountRepository.delete(account);
        return modelMapper.map(account, AccountResponseDto.class);
    }

    public boolean isDuplicatedAccount(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public ResponseEntity<?> favorite(Long accountId, Account currentUser) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        Account favoritedAccount = accountOptional.get();
        favoritedAccount.addFavorite(currentUser);

        return new ResponseEntity<>(new AccountFavoriteFlagResponseDto(favoritedAccount, currentUser), HttpStatus.OK);
    }

    public ResponseEntity<?> getFavoriteUsers(Long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return errorsFormatter.badRequest(USER_NOT_FOUND.getValue());
        }
        Account account = accountOptional.get();
        List<AccountListResponseDto> accountList = account.getFavorite().stream()
                .map(AccountListResponseDto::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(accountList, HttpStatus.OK);
    }

    private String build(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("html/code.html", context);
    }
}
