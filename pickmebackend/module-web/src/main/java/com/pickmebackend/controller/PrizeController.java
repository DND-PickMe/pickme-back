package com.pickmebackend.controller;

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
    ResponseEntity<?> savePrize(@RequestBody PrizeDto prizeDto) {
        return prizeService.saveSelfInterview(prizeDto);
    }

    @PutMapping("/{prizeId}")
    ResponseEntity<?> updatePrize(@PathVariable Long prizeId, @RequestBody PrizeDto prizeDto) {
        return prizeService.updateSelfInterview(prizeId, prizeDto);
    }

    @DeleteMapping("/{prizeId}")
    ResponseEntity<?> deletePrize(@PathVariable Long prizeId) {
        return prizeService.deleteSelfInterview(prizeId);
    }
}
