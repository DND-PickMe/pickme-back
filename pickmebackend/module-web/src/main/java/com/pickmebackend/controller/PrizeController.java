package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Prize;
import com.pickmebackend.domain.dto.prize.PrizeRequestDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.PrizeRepository;
import com.pickmebackend.resource.PrizeResource;
import com.pickmebackend.service.PrizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.pickmebackend.error.ErrorMessageConstant.PRIZENOTFOUND;
import static com.pickmebackend.error.ErrorMessageConstant.UNAUTHORIZEDUSER;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/prizes", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class PrizeController {

    private final PrizeService prizeService;

    private final PrizeRepository prizeRepository;

    @PostMapping
    ResponseEntity<?> savePrize(@RequestBody PrizeRequestDto prizeRequestDto, @CurrentUser Account currentUser) {
        Prize prize = prizeService.savePrize(prizeRequestDto, currentUser);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(PrizeController.class).slash(prize.getId());
        PrizeResource prizeResource = new PrizeResource(prize);
        prizeResource.add(selfLinkBuilder.withRel("update-prize"));
        prizeResource.add(selfLinkBuilder.withRel("delete-prize"));

        return new ResponseEntity<>(prizeResource, HttpStatus.CREATED);
    }

    @PutMapping("/{prizeId}")
    ResponseEntity<?> updatePrize(@PathVariable Long prizeId, @RequestBody PrizeRequestDto prizeRequestDto, @CurrentUser Account currentUser) {
        Optional<Prize> prizeOptional = this.prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorMessage(PRIZENOTFOUND));
        }

        Prize prize = prizeOptional.get();
        if (!prize.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(new ErrorMessage(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        Prize modifiedPrize = prizeService.updatePrize(prize, prizeRequestDto, currentUser);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(PrizeController.class).slash(modifiedPrize.getId());
        PrizeResource prizeResource = new PrizeResource(modifiedPrize);
        prizeResource.add(linkTo(PrizeController.class).withRel("create-prize"));
        prizeResource.add(selfLinkBuilder.withRel("delete-prize"));

        return new ResponseEntity<>(prizeResource, HttpStatus.OK);
    }

    @DeleteMapping("/{prizeId}")
    ResponseEntity<?> deletePrize(@PathVariable Long prizeId, @CurrentUser Account currentUser) {
        return prizeService.deletePrize(prizeId, currentUser);
    }
}
