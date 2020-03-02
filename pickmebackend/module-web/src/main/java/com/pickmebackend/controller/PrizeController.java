package com.pickmebackend.controller;

import com.pickmebackend.annotation.CurrentUser;
import com.pickmebackend.common.ErrorsFormatter;
import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.Prize;
import com.pickmebackend.domain.dto.prize.PrizeRequestDto;
import com.pickmebackend.domain.dto.prize.PrizeResponseDto;
import com.pickmebackend.error.ErrorMessage;
import com.pickmebackend.repository.PrizeRepository;
import com.pickmebackend.resource.PrizeResource;
import com.pickmebackend.service.PrizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
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

    private final ErrorsFormatter errorsFormatter;

    @PostMapping
    ResponseEntity<?> savePrize(@RequestBody PrizeRequestDto prizeRequestDto, @CurrentUser Account currentUser) {
        PrizeResponseDto prizeResponseDto = prizeService.savePrize(prizeRequestDto, currentUser);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(PrizeController.class).slash(prizeResponseDto.getId());
        PrizeResource prizeResource = new PrizeResource(prizeResponseDto);
        prizeResource.add(selfLinkBuilder.withRel("update-prize"));
        prizeResource.add(selfLinkBuilder.withRel("delete-prize"));
        prizeResource.add(new Link("/docs/index.html#resources-prizes-create").withRel("profile"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(prizeResource);
    }

    @PutMapping("/{prizeId}")
    ResponseEntity<?> updatePrize(@PathVariable Long prizeId, @RequestBody PrizeRequestDto prizeRequestDto, @CurrentUser Account currentUser) {
        Optional<Prize> prizeOptional = this.prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(PRIZENOTFOUND));
        }

        Prize prize = prizeOptional.get();
        if (!prize.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        PrizeResponseDto modifiedPrizeResponseDto = prizeService.updatePrize(prize, prizeRequestDto);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(PrizeController.class).slash(modifiedPrizeResponseDto.getId());
        PrizeResource prizeResource = new PrizeResource(modifiedPrizeResponseDto);
        prizeResource.add(linkTo(PrizeController.class).withRel("create-prize"));
        prizeResource.add(selfLinkBuilder.withRel("delete-prize"));
        prizeResource.add(new Link("/docs/index.html#resources-prizes-update").withRel("profile"));

        return new ResponseEntity<>(prizeResource, HttpStatus.OK);
    }

    @DeleteMapping("/{prizeId}")
    ResponseEntity<?> deletePrize(@PathVariable Long prizeId, @CurrentUser Account currentUser) {
        Optional<Prize> prizeOptional = this.prizeRepository.findById(prizeId);
        if (!prizeOptional.isPresent()) {
            return ResponseEntity.badRequest().body(errorsFormatter.formatAnError(PRIZENOTFOUND));
        }

        Prize prize = prizeOptional.get();
        if (!prize.getAccount().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(errorsFormatter.formatAnError(UNAUTHORIZEDUSER), HttpStatus.BAD_REQUEST);
        }

        PrizeResponseDto prizeResponseDto = prizeService.deletePrize(prize);
        PrizeResource prizeResource = new PrizeResource(prizeResponseDto);
        prizeResource.add(linkTo(PrizeController.class).withRel("create-prize"));
        prizeResource.add(new Link("/docs/index.html#resources-prizes-delete").withRel("profile"));

        return new ResponseEntity<>(prizeResource, HttpStatus.OK);
    }
}
