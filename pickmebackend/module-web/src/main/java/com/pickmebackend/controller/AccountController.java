package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.account.AccountRequestDto;
import com.pickmebackend.domain.dto.account.AccountResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.AccountRepository;
import com.pickmebackend.resource.AccountResource;
import com.pickmebackend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    ResponseEntity<?> saveAccount(@Valid @RequestBody AccountRequestDto accountDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        if(accountService.isDuplicatedAccount(accountDto))  {
            return ResponseEntity.badRequest().body(new ErrorMessage(DUPLICATEDUSER));
        }
        AccountResponseDto accountResponseDto = accountService.saveAccount(accountDto);
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(linkTo(LoginController.class).withRel("login"));

        return new ResponseEntity<>(accountResource, HttpStatus.CREATED);
    }

    @PutMapping("/{accountId}")
    ResponseEntity<?> updateAccount(@PathVariable Long accountId, @Valid @RequestBody AccountRequestDto accountDto, Errors errors, @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        if (!accountId.equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        AccountResponseDto accountResponseDto = accountService.updateAccount(accountOptional.get(), accountDto, currentUser);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountResponseDto.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(selfLinkBuilder.withRel("delete-account"));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    ResponseEntity<?> deleteAccount(@PathVariable Long accountId, @CurrentUser Account currentUser) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }

        if (!accountId.equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }
        AccountResponseDto accountResponseDto = accountService.deleteAccount(accountOptional.get(), currentUser);
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(linkTo(LoginController.class).withRel("login"));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<?> getAccount(@CurrentUser Account currentUser) {
        return accountService.getAccount(currentUser);
    }

    @PostMapping("/{accountId}/favorite")
    ResponseEntity<?> favorite(@PathVariable Long accountId, @CurrentUser Account currentUser) {
        return accountService.favorite(accountId, currentUser);
    }

    @GetMapping("/{accountId}/favorite")
    ResponseEntity<?> getFavoriteUsers(@PathVariable Long accountId) {
        return accountService.getFavoriteUsers(accountId);
    }
}