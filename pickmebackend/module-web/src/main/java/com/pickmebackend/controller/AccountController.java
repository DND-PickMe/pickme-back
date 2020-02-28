package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.account.AccountInitialRequestDto;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.dto.account.AccountResponseDto;
import com.pickmebackend.repository.AccountRepository;
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

import static com.pickmebackend.error.ErrorMessageConstant.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/accounts", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final ErrorsFormatter errorsFormatter;

    @GetMapping("/profile")
    ResponseEntity<?> loadProfile(@CurrentUser Account currentUser) {
        if (currentUser == null) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Optional<Account> accountOptional = accountRepository.findById(currentUser.getId());
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Account account = accountOptional.get();
        AccountResponseDto accountResponseDto = accountService.loadProfile(account);
        accountResponseDto.setFavoriteCount(account.getFavorite().size());
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(selfLinkBuilder.withRel("update-account"));
        accountResource.add(selfLinkBuilder.withRel("delete-account"));
        accountResource.add(new Link("/docs/index.html#resources-profile-load").withRel("profile"));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    ResponseEntity<?> loadAccount(@PathVariable Long accountId, @CurrentUser Account currentUser, HttpServletRequest request, HttpServletResponse response) {
        if (currentUser == null) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Account account = accountOptional.get();
        AccountResponseDto accountResponseDto = accountService.loadAccount(accountId, account, request, response);
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(new Link("/docs/index.html#resources-account-load").withRel("profile"));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<?> loadAllAccounts(Pageable pageable, PagedResourcesAssembler<Account> assembler,
                                      @RequestParam(value = "orderBy", required = false) String orderBy)  {
        Page<Account> all = accountService.loadAllAccounts(pageable, orderBy);
        PagedModel<AccountResource> accountResources = assembler.toModel(all, e -> new AccountResource(new AccountResponseDto(e)));
        accountResources.add(new Link("/docs/index.html#resources-allAccounts-load").withRel("profile"));

        return new ResponseEntity<>(accountResources, HttpStatus.OK);
    }

    @GetMapping("/{accountId}/favorite")
    ResponseEntity<?> getFavoriteUsers(@PathVariable Long accountId) {
        return accountService.getFavoriteUsers(accountId);
    }

    @PostMapping
    ResponseEntity<?> saveAccount(@Valid @RequestBody AccountInitialRequestDto accountDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatErrors(errors));
        }
        if(accountService.isDuplicatedAccount(accountDto))  {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(DUPLICATEDUSER));
        }
        AccountResponseDto accountResponseDto = accountService.saveAccount(accountDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(linkTo(LoginController.class).withRel("login-account"));
        accountResource.add(new Link("/docs/index.html#resources-account-create").withRel("profile"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(accountResource);
    }

    @PostMapping("/{accountId}/favorite")
    ResponseEntity<?> favorite(@PathVariable Long accountId, @CurrentUser Account currentUser) {
        return accountService.favorite(accountId, currentUser);
    }

    @PutMapping("/{accountId}")
    ResponseEntity<?> updateAccount(@PathVariable Long accountId, @Valid @RequestBody AccountRequestDto accountDto, Errors errors, @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatErrors(errors));
        }
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }

        if (!accountId.equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }
        AccountResponseDto accountResponseDto = accountService.updateAccount(accountOptional.get(), accountDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(selfLinkBuilder.withRel("delete-account"));
        accountResource.add(new Link("/docs/index.html#resources-account-update").withRel("profile"));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    ResponseEntity<?> deleteAccount(@PathVariable Long accountId, @CurrentUser Account currentUser) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }

        if (!accountId.equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        AccountResponseDto accountResponseDto = accountService.deleteAccount(accountOptional.get());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(linkTo(LoginController.class).withRel("login-account"));
        accountResource.add(new Link("/docs/index.html#resources-account-delete").withRel("profile"));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }
}