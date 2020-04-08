package com.pickmebackend.controller;

import com.pickmebackend.annotation.account.AccountValidation;
import com.pickmebackend.annotation.account.CurrentUser;
import com.pickmebackend.annotation.enterprise.EnterpriseValidation;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.enterprise.EnterpriseFilterRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseRequestDto;
import com.pickmebackend.domain.dto.enterprise.EnterpriseResponseDto;
import com.pickmebackend.exception.UserNotFoundException;
import com.pickmebackend.repository.account.AccountRepository;
import com.pickmebackend.resource.EnterpriseResource;
import com.pickmebackend.resource.HateoasFormatter;
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

import static com.pickmebackend.properties.RestDocsConstants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/enterprises", produces = MediaTypes.HAL_JSON_VALUE)
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final HateoasFormatter hateoasFormatter;

    @GetMapping("/profile")
    @AccountValidation
    public ResponseEntity<?> loadProfile(@CurrentUser Account currentUser) throws UserNotFoundException {
        Optional<Account> accountOptional = accountRepository.findById(currentUser.getId());
        Account account = accountOptional.orElseThrow(UserNotFoundException::new);
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.loadProfile(account);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EnterpriseController.class).slash(enterpriseResponseDto.getId());
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(selfLinkBuilder.withRel(UPDATE_ENTERPRISE.getValue()));
        enterpriseResource.add(selfLinkBuilder.withRel(DELETE_ENTERPRISE.getValue()));
        hateoasFormatter.addProfileRel(enterpriseResource, "resources-profile-load");
        return new ResponseEntity<>(enterpriseResource, HttpStatus.OK);
    }

    @GetMapping("/{enterpriseId}")
    @EnterpriseValidation
    public ResponseEntity<?> loadEnterprise(@PathVariable Long enterpriseId) {
        Optional<Account> accountOptional = this.accountRepository.findById(enterpriseId);
        Account account = accountOptional.get();
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.loadEnterprise(account);
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        hateoasFormatter.addProfileRel(enterpriseResource, "resources-enterprise-load");

        return new ResponseEntity<>(enterpriseResource, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> loadEnterprisesWithFilter(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String address,
                                              Pageable pageable,
                                              PagedResourcesAssembler<Enterprise> assembler)    {

        EnterpriseFilterRequestDto enterpriseFilterRequestDto = EnterpriseFilterRequestDto
                .builder()
                .name(name)
                .address(address)
                .build();

        Page<Enterprise> filteredEnterprises = enterpriseService.loadEnterprisesWithFilter(enterpriseFilterRequestDto, pageable);
        PagedModel<EnterpriseResource> enterpriseResources = getEnterpriseResources(assembler, filteredEnterprises);
        enterpriseResources.add(new Link("/docs/index.html#resources-enterprises-load").withRel(PROFILE.getValue()));

        return new ResponseEntity<>(enterpriseResources, HttpStatus.OK);
    }

    @PostMapping
    @EnterpriseValidation
    public ResponseEntity<?> saveEnterprise(@Valid @RequestBody EnterpriseRequestDto enterpriseRequestDto, Errors errors) {
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.saveEnterprise(enterpriseRequestDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EnterpriseController.class).slash(enterpriseResponseDto.getId());
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(linkTo(LoginController.class).withRel(LOGIN_ENTERPRISE.getValue()));
        hateoasFormatter.addProfileRel(enterpriseResource, "resources-enterprise-create");

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(enterpriseResource);
    }

    @PutMapping("/{enterpriseId}")
    @EnterpriseValidation
    public ResponseEntity<?> updateEnterprise(@PathVariable Long enterpriseId, @Valid @RequestBody EnterpriseRequestDto enterpriseRequestDto,
                                              Errors errors, @CurrentUser Account currentUser) {
        Optional<Account> accountOptional = this.accountRepository.findById(enterpriseId);
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.updateEnterprise(accountOptional.get(), enterpriseRequestDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EnterpriseController.class).slash(enterpriseResponseDto.getId());
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(selfLinkBuilder.withRel(DELETE_ENTERPRISE.getValue()));
        hateoasFormatter.addProfileRel(enterpriseResource, "resources-enterprise-update");

        return new ResponseEntity<>(enterpriseResource, HttpStatus.OK);
    }

    @DeleteMapping("/{enterpriseId}")
    @EnterpriseValidation
    public ResponseEntity<?> deleteEnterprise(@PathVariable Long enterpriseId, @CurrentUser Account currentUser)    {
        Optional<Account> optionalAccount = this.accountRepository.findById(enterpriseId);
        EnterpriseResponseDto enterpriseResponseDto = enterpriseService.deleteEnterprise(optionalAccount.get());
        EnterpriseResource enterpriseResource = new EnterpriseResource(enterpriseResponseDto);
        enterpriseResource.add(linkTo(LoginController.class).withRel(LOGIN_ENTERPRISE.getValue()));
        hateoasFormatter.addProfileRel(enterpriseResource, "resources-enterprise-delete");

        return new ResponseEntity<>(enterpriseResource, HttpStatus.OK);
    }

    @GetMapping("/suggestion")
    public ResponseEntity<?> sendSuggestion(@RequestParam(value = "accountId") Long accountId, @CurrentUser Account currentUser) {
        return enterpriseService.sendSuggestion(accountId, currentUser);
    }

    private PagedModel<EnterpriseResource> getEnterpriseResources(PagedResourcesAssembler<Enterprise> assembler, Page<Enterprise> filteredEnterprises) {
        return assembler
                .toModel(filteredEnterprises, e -> {
                    EnterpriseResponseDto enterpriseResponseDto = modelMapper.map(e, EnterpriseResponseDto.class);
                    enterpriseResponseDto.setEmail(e.getAccount().getEmail());
                    return new EnterpriseResource(enterpriseResponseDto);
                });
    }
}
