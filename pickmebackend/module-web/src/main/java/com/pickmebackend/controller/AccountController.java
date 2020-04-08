package com.pickmebackend.controller;

import com.pickmebackend.annotation.account.AccountValidation;
import com.pickmebackend.annotation.account.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.account.*;
import com.pickmebackend.domain.dto.verificationCode.SendCodeRequestDto;
import com.pickmebackend.domain.dto.verificationCode.VerifyCodeRequestDto;
import com.pickmebackend.exception.CodeNotExist;
import com.pickmebackend.exception.UserNotFoundException;
import com.pickmebackend.repository.account.AccountRepository;
import com.pickmebackend.resource.AccountFavoriteFlagResource;
import com.pickmebackend.resource.AccountResource;
import com.pickmebackend.resource.HateoasFormatter;
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

import static com.pickmebackend.properties.RestDocsConstants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/accounts", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    private final HateoasFormatter hateoasFormatter;

    @GetMapping("/profile")
    @AccountValidation
    public ResponseEntity<?> loadProfile(@CurrentUser Account currentUser) throws UserNotFoundException {
        Optional<Account> accountOptional = accountRepository.findById(currentUser.getId());
        Account account = accountOptional.orElseThrow(UserNotFoundException::new);
        AccountResponseDto accountResponseDto = accountService.loadProfile(account);
        accountResponseDto.setFavoriteCount(account.getFavorite().size());

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(selfLinkBuilder.withRel(UPDATE_ACCOUNT.getValue()));
        accountResource.add(selfLinkBuilder.withRel(DELETE_ACCOUNT.getValue()));
        accountResource.add(new Link("/docs/index.html#resources-profile-load").withRel(PROFILE.getValue()));

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @GetMapping("/{accountId}")
    @AccountValidation
    public ResponseEntity<?> loadAccount(@PathVariable Long accountId, @CurrentUser Account currentUser,
                                         HttpServletRequest request, HttpServletResponse response) throws UserNotFoundException {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        Account account = accountOptional.orElseThrow(UserNotFoundException::new);
        AccountFavoriteFlagResponseDto accountResponseDto = accountService.loadAccount(accountId, account, request, response, currentUser);

        AccountFavoriteFlagResource accountResource = new AccountFavoriteFlagResource(accountResponseDto);
        hateoasFormatter.addProfileRel(accountResource, "resources-account-load");

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
        accountResources.add(new Link("/docs/index.html#resources-accounts-load").withRel(PROFILE.getValue()));

        return new ResponseEntity<>(accountResources, HttpStatus.OK);
    }

    @GetMapping("/{accountId}/favorite")
    public ResponseEntity<?> getFavoriteUsers(@PathVariable Long accountId) throws UserNotFoundException {
        return accountService.getFavoriteUsers(accountId);
    }

    @PostMapping("/sendCode")
    @AccountValidation
    public ResponseEntity<?> sendVerificationCode(@RequestBody SendCodeRequestDto sendCodeRequestDto, Errors errors) {
        return accountService.sendVerificationCode(sendCodeRequestDto.getEmail());
    }

    @PostMapping
    @AccountValidation
    public ResponseEntity<?> saveAccount(@Valid @RequestBody AccountInitialRequestDto accountDto, Errors errors) {
        AccountResponseDto accountResponseDto = accountService.saveAccount(accountDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(linkTo(LoginController.class).withRel(LOGIN_ACCOUNT.getValue()));
        hateoasFormatter.addProfileRel(accountResource, "resources-account-create");

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(accountResource);
    }

    @PostMapping("/{accountId}/favorite")
    public ResponseEntity<?> favorite(@PathVariable Long accountId, @CurrentUser Account currentUser) throws UserNotFoundException {
        return accountService.favorite(accountId, currentUser);
    }

    @PutMapping("/matchCode")
    @AccountValidation
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequestDto verifyCodeRequestDto, Errors errors) throws CodeNotExist {
        return accountService.verifyCode(verifyCodeRequestDto);
    }

    @PutMapping("/{accountId}")
    @AccountValidation
    public ResponseEntity<?> updateAccount(@PathVariable Long accountId, @Valid @RequestBody AccountRequestDto accountDto, Errors errors,
                                           @CurrentUser Account currentUser) throws UserNotFoundException {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        AccountResponseDto accountResponseDto = accountService.updateAccount(accountOptional.orElseThrow(UserNotFoundException::new), accountDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(accountResponseDto.getId());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(selfLinkBuilder.withRel(DELETE_ACCOUNT.getValue()));
        hateoasFormatter.addProfileRel(accountResource, "resources-account-update");

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @DeleteMapping("/{accountId}")
    @AccountValidation
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId, @CurrentUser Account currentUser) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        AccountResponseDto accountResponseDto = accountService.deleteAccount(accountOptional.get());
        AccountResource accountResource = new AccountResource(accountResponseDto);
        accountResource.add(linkTo(LoginController.class).withRel(LOGIN_ACCOUNT.getValue()));
        hateoasFormatter.addProfileRel(accountResource, "resources-account-delete");

        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }
}