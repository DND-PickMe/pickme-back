package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.AccountRepository;
import com.pickmebackend.resource.EnterpriseResource;
import com.pickmebackend.service.EnterpriseService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/enterprises", produces = MediaTypes.HAL_JSON_VALUE)
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    private final AccountRepository accountRepository;

    @GetMapping("/{enterpriseId}")
    public ResponseEntity<?> loadEnterprise(@PathVariable Long enterpriseId, @CurrentUser Account currentUser)    {
        if(enterpriseService.isNonEnterprise(enterpriseId)) {
            return ResponseEntity.badRequest().body(new ErrorMessage(USERNOTFOUND));
        }
        return enterpriseService.loadEnterprise(enterpriseId, currentUser);
    }

    @PostMapping
    public ResponseEntity<?> saveEnterprise(@Valid @RequestBody EnterpriseRequestDto enterpriseRequestDto, Errors errors) {
        if(errors.hasErrors())  {
            return ResponseEntity.badRequest().body(errors);
        }
        if(enterpriseService.isDuplicatedEnterprise(enterpriseRequestDto)) {
            return ResponseEntity.badRequest().body(new ErrorMessage(DUPLICATEDUSER));
        }
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.saveEnterprise(enterpriseRequestDto);
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(linkTo(LoginController.class).withRel("login"));

        return new ResponseEntity<>(enterpriseResource, HttpStatus.CREATED);
    }

    @PutMapping("/{enterpriseId}")
    public ResponseEntity<?> updateEnterprise(@PathVariable Long enterpriseId, @Valid @RequestBody EnterpriseRequestDto enterpriseRequestDto, Errors errors, @CurrentUser Account currentUser) {
        if(errors.hasErrors())  {
            return ResponseEntity.badRequest().body(errors);
        }
        if(enterpriseService.isNonEnterprise(enterpriseId)) {
            return ResponseEntity.badRequest().body(new ErrorMessage(USERNOTFOUND));
        }
        Optional<Account> accountOptional = this.accountRepository.findById(enterpriseId);
        if (!enterpriseId.equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.updateEnterprise(accountOptional.get(), enterpriseRequestDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EnterpriseController.class).slash(enterpriseResponseDto.getId());
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(selfLinkBuilder.withRel("delete-enterprise"));

        return new ResponseEntity<>(enterpriseResource, HttpStatus.OK);
    }

    @DeleteMapping("/{enterpriseId}")
    public ResponseEntity<?> deleteEnterprise(@PathVariable Long enterpriseId, @CurrentUser Account currentUser)    {
        if(enterpriseService.isNonEnterprise(enterpriseId)) {
            return ResponseEntity.badRequest().body(new ErrorMessage(USERNOTFOUND));
        }
        if (!enterpriseId.equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }
        Optional<Account> optionalAccount = this.accountRepository.findById(enterpriseId);
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.deleteEnterprise(optionalAccount.get());
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(linkTo(LoginController.class).withRel("login"));

        return new ResponseEntity<>(enterpriseResource, HttpStatus.OK);
    }

}
