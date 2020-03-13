package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.account.*;
import com.pickmebackend.domain.dto.verificationCode.SendCodeRequestDto;
import com.pickmebackend.domain.dto.verificationCode.VerifyCodeRequestDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.account.AccountRepository;
import com.pickmebackend.resource.AccountFavoriteFlagResource;
import com.pickmebackend.resource.AccountResource;
import com.pickmebackend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

import static com.pickmebackend.error.ErrorMessage.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/accounts", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final ErrorsFormatter errorsFormatter;

    private static final String PROFILE = "profile";

    @GetMapping("/profile")
    public ResponseEntity<?> loadProfile(@CurrentUser Account currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USER_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }
        Optional<Account> accountOptional = accountRepository.findById(currentUser.getId());
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USER_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }
        Account account = accountOptional.get();
        AccountResponseDto accountResponseDto = accountService.loadProfile(account);
        accountResponseDto.setFavoriteCount(account.getFavorite().size());
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(selfLinkBuilder.withRel("update-account"));
        accountResource.add(selfLinkBuilder.withRel("delete-account"));

        accountResource.add(new Link("/docs/index.html#resources-profile-load").withRel(PROFILE));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> loadAccount(@PathVariable Long accountId, @CurrentUser Account currentUser
            , HttpServletRequest request, HttpServletResponse response) {
        if (currentUser == null) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USER_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USER_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }
        Account account = accountOptional.get();
        AccountFavoriteFlagResponseDto accountResponseDto = accountService.loadAccount(accountId, account, request, response, currentUser);
        AccountFavoriteFlagResource accountResource = new AccountFavoriteFlagResource(accountResponseDto);
        accountResource.add(new Link("/docs/index.html#resources-account-load").withRel(PROFILE));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> loadAccountsWithFilter(@RequestParam(required = false) String nickName,
                                             @RequestParam(required = false) String oneLineIntroduce,
                                             @RequestParam(required = false) String career,
                                             @RequestParam(required = false) String positions,
                                             @RequestParam(required = false) String technology,
                                             @RequestParam(required = false) String orderBy,
                                             Pageable pageable,
                                             PagedResourcesAssembler<Account> assembler)    {

        AccountFilteringRequestDto accountFilteringRequestDto = AccountFilteringRequestDto.builder()
                .nickName(nickName)
                .oneLineIntroduce(oneLineIntroduce)
                .career(career)
                .position(positions)
                .technology(technology)
                .orderBy(orderBy)
                .build();

        Page<Account> filteredAccount = accountService.loadAccountsWithFilter(accountFilteringRequestDto, pageable);
        PagedModel<AccountResource> accountResources = assembler.toModel(filteredAccount, e -> new AccountResource(new AccountResponseDto(e)));
        accountResources.add(new Link("/docs/index.html#resources-accounts-load").withRel(PROFILE));

        return new ResponseEntity<>(accountResources, HttpStatus.OK);
    }

    @GetMapping("/{accountId}/favorite")
    public ResponseEntity<?> getFavoriteUsers(@PathVariable Long accountId) {
        return accountService.getFavoriteUsers(accountId);
    }

    @PostMapping("/sendCode")
    public ResponseEntity<?> sendVerificationCode(@RequestBody SendCodeRequestDto sendCodeRequestDto, Errors errors) {
        if(errors.hasErrors())  {
            return ResponseEntity.badRequest().body(errorsFormatter.formatErrors(errors));
        }
        if(accountService.isDuplicatedAccount(sendCodeRequestDto.getEmail()))  {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(DUPLICATED_USER.getValue()));
        }
        return accountService.sendVerificationCode(sendCodeRequestDto.getEmail());
    }

    @PostMapping
    public ResponseEntity<?> saveAccount(@Valid @RequestBody AccountInitialRequestDto accountDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatErrors(errors));
        }
        if(accountService.isVerifiedAccount(accountDto))  {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(UNVERIFIED_USER.getValue()));
        }
        AccountResponseDto accountResponseDto = accountService.saveAccount(accountDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(linkTo(LoginController.class).withRel("login-account"));
        accountResource.add(new Link("/docs/index.html#resources-account-create").withRel(PROFILE));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(accountResource);
    }

    @PostMapping("/{accountId}/favorite")
    public ResponseEntity<?> favorite(@PathVariable Long accountId, @CurrentUser Account currentUser) {
        return accountService.favorite(accountId, currentUser);
    }

    @PutMapping("/matchCode")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequestDto verifyCodeRequestDto, Errors errors)    {
        if(errors.hasErrors())  {
            return ResponseEntity.badRequest().body(errorsFormatter.formatErrors(errors));
        }
        if(accountService.isDuplicatedAccount(verifyCodeRequestDto.getEmail()))  {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(DUPLICATED_USER.getValue()));
        }
        return accountService.verifyCode(verifyCodeRequestDto);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<?> updateAccount(@PathVariable Long accountId, @Valid @RequestBody AccountRequestDto accountDto, Errors errors, @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatErrors(errors));
        }
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USER_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }

        if (!accountId.equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZED_USER.getValue()), HttpStatus.BAD_REQUEST);
        }
        AccountResponseDto accountResponseDto = accountService.updateAccount(accountOptional.get(), accountDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(selfLinkBuilder.withRel("delete-account"));
        accountResource.add(new Link("/docs/index.html#resources-account-update").withRel(PROFILE));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId, @CurrentUser Account currentUser) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USER_NOT_FOUND.getValue()), HttpStatus.BAD_REQUEST);
        }

        if (!accountId.equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZED_USER.getValue()), HttpStatus.BAD_REQUEST);
        }

        AccountResponseDto accountResponseDto = accountService.deleteAccount(accountOptional.get());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(linkTo(LoginController.class).withRel("login-account"));
        accountResource.add(new Link("/docs/index.html#resources-account-delete").withRel(PROFILE));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }
}