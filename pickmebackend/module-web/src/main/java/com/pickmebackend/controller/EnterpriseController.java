package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.account.AccountRepository;
import com.pickmebackend.resource.EnterpriseResource;
import com.pickmebackend.service.EnterpriseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper modelMapper;

    @GetMapping("/profile")
    public ResponseEntity<?> loadProfile(@CurrentUser Account currentUser)   {
        if (currentUser == null) {
            return new ResponseEntity<>(USERNOTFOUND, HttpStatus.BAD_REQUEST);
        }
        Optional<Account> accountOptional = accountRepository.findById(currentUser.getId());
        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>(new ErrorMessage(USERNOTFOUND), HttpStatus.BAD_REQUEST);
        }
        Account account = accountOptional.get();
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.loadProfile(account);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EnterpriseController.class).slash(enterpriseResponseDto.getId());
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(selfLinkBuilder.withRel("update-enterprise"));
        enterpriseResource.add(selfLinkBuilder.withRel("delete-enterprise"));
        enterpriseResource.add(new Link("/docs/index.html#resources-profile-load").withRel("profile"));

        return new ResponseEntity<>(enterpriseResource, HttpStatus.OK);
    }

    @GetMapping("/{enterpriseId}")
    public ResponseEntity<?> loadEnterprise(@PathVariable Long enterpriseId, @CurrentUser Account currentUser)    {
        if (currentUser == null) {
            return new ResponseEntity<>(USERNOTFOUND, HttpStatus.BAD_REQUEST);
        }
        if(enterpriseService.isNonEnterprise(enterpriseId)) {
            return ResponseEntity.badRequest().body(new ErrorMessage(USERNOTFOUND));
        }
        Optional<Account> accountOptional = this.accountRepository.findById(enterpriseId);
        Account account = accountOptional.get();
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.loadEnterprise(account);
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(new Link("/docs/index.html#resources-enterprise-load").withRel("profile"));

        return new ResponseEntity<>(enterpriseResource, HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<?> loadAllEnterprises(Pageable pageable, PagedResourcesAssembler<Enterprise> assembler)  {
        Page<Enterprise> all = enterpriseService.loadAllEnterprises(pageable);
        PagedModel<EnterpriseResource> enterpriseResources = getEnterpriseResources(pageable, assembler, all);
        enterpriseResources.add(new Link("/docs/index.html#resources-allEnterprises-load").withRel("profile"));

        return new ResponseEntity<>(enterpriseResources, HttpStatus.OK);
    }

    @GetMapping("/filter")
    ResponseEntity<?> loadFilteredEnterprises(@RequestParam String name, Pageable pageable, PagedResourcesAssembler<Enterprise> assembler)    {
        Page<Enterprise> filteredEnterprises = enterpriseService.filterEnterprise(name, pageable);
        PagedModel<EnterpriseResource> enterpriseResources = getEnterpriseResources(pageable, assembler, filteredEnterprises);
        enterpriseResources.add(new Link("/docs/index.html#resources-filteredEnterprises-load").withRel("profile"));

        return new ResponseEntity<>(enterpriseResources, HttpStatus.OK);
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
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EnterpriseController.class).slash(enterpriseResponseDto.getId());
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(linkTo(LoginController.class).withRel("login-enterprise"));
        enterpriseResource.add(new Link("/docs/index.html#resources-enterprise-create").withRel("profile"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(enterpriseResource);
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
        enterpriseResource.add(new Link("/docs/index.html#resources-enterprise-update").withRel("profile"));

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
        enterpriseResource.add(linkTo(LoginController.class).withRel("login-enterprise"));
        enterpriseResource.add(new Link("/docs/index.html#resources-enterprise-delete").withRel("profile"));

        return new ResponseEntity<>(enterpriseResource, HttpStatus.OK);
    }

    private PagedModel<EnterpriseResource> getEnterpriseResources(Pageable pageable, PagedResourcesAssembler<Enterprise> assembler, Page<Enterprise> filteredEnterprises) {
        return assembler
                .toModel(filteredEnterprises, e -> {
                    EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(e, EnterpriseResponseDto.class);
                    enterpriseResponseDto.setEmail(e.getAccount().getEmail());
                    return new EnterpriseResource(enterpriseResponseDto);
                });
    }

}
