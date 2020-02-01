package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.PrizeDto;
import com.pickmebackend.service.PrizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/prizes", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class PrizeController {

    private final PrizeService prizeService;

    @PostMapping
    ResponseEntity<?> savePrize(@RequestBody PrizeDto prizeDto, @CurrentUser Account currentUser) {
        return prizeService.savePrize(prizeDto, currentUser);
    }

    @PutMapping("/{prizeId}")
    ResponseEntity<?> updatePrize(@PathVariable Long prizeId, @RequestBody PrizeDto prizeDto, @CurrentUser Account currentUser) {
        return prizeService.updatePrize(prizeId, prizeDto, currentUser);
    }

    @DeleteMapping("/{prizeId}")
    ResponseEntity<?> deletePrize(@PathVariable Long prizeId, @CurrentUser Account currentUser) {
        return prizeService.deletePrize(prizeId, currentUser);
    }
}
