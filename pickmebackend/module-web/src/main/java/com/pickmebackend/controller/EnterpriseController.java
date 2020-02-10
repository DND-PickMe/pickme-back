package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.EnterpriseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.service.EnterpriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import static com.pickmebackend.error.ErrorMessageConstant.DUPLICATEDUSER;
import static com.pickmebackend.error.ErrorMessageConstant.USERNOTFOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/enterprises", produces = MediaTypes.HAL_JSON_VALUE)
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @GetMapping("/{enterpriseId}")
    public ResponseEntity<?> loadEnterprise(@PathVariable Long enterpriseId, @CurrentUser Account currentUser)    {
        if(enterpriseService.isNonEnterprise(enterpriseId)) {
            return ResponseEntity.badRequest().body(new ErrorMessage(USERNOTFOUND));
        }
        return enterpriseService.loadEnterprise(enterpriseId, currentUser);
    }

    @PostMapping
    public ResponseEntity<?> saveEnterprise(@Valid @RequestBody EnterpriseDto enterpriseDto, Errors errors) {
        if(errors.hasErrors())  {
            return ResponseEntity.badRequest().body(errors);
        }
        if(enterpriseService.isDuplicatedEnterprise(enterpriseDto)) {
            return ResponseEntity.badRequest().body(new ErrorMessage(DUPLICATEDUSER));
        }
        return enterpriseService.saveEnterprise(enterpriseDto);
    }

    @PutMapping("/{enterpriseId}")
    public ResponseEntity<?> updateEnterprise(@PathVariable Long enterpriseId, @Valid @RequestBody EnterpriseDto enterpriseDto, Errors errors, @CurrentUser Account currentUser) {
        if(errors.hasErrors())  {
            return ResponseEntity.badRequest().body(errors);
        }
        if(enterpriseService.isNonEnterprise(enterpriseId)) {
            return ResponseEntity.badRequest().body(new ErrorMessage(USERNOTFOUND));
        }
        return enterpriseService.updateEnterprise(enterpriseId, enterpriseDto, currentUser);
    }

    @DeleteMapping("/{enterpriseId}")
    public ResponseEntity<?> deleteEnterprise(@PathVariable Long enterpriseId, @CurrentUser Account currentUser)    {
        if(enterpriseService.isNonEnterprise(enterpriseId)) {
            return ResponseEntity.badRequest().body(new ErrorMessage(USERNOTFOUND));
        }
        return enterpriseService.deleteEnterprise(enterpriseId, currentUser);
    }

}
