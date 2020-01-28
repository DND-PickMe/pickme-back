package com.pickmebackend.controller;

import com.pickmebackend.domain.dto.AccountDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import static com.pickmebackend.error.ErrorMessageConstant.DUPLICATEDUSER;

@RestController
@RequestMapping(value = "/api/accounts", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    ResponseEntity<?> saveAccount(@Valid @RequestBody AccountDto accountDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        if(accountService.isDuplicatedAccount(accountDto))  {
            return ResponseEntity.badRequest().body(new ErrorMessage(DUPLICATEDUSER));
        }
        return accountService.saveAccount(accountDto);
    }

    @PutMapping("/{accountId}")
    ResponseEntity<?> updateAccount(@PathVariable Long accountId, @Valid @RequestBody AccountDto accountDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return accountService.updateAccount(accountId, accountDto);
    }

    @DeleteMapping("/{accountId}")
    ResponseEntity<?> deleteAccount(@PathVariable Long accountId) {
        return accountService.deleteAccount(accountId);
    }

    @GetMapping("/{accountId}")
    ResponseEntity<?> getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

}
