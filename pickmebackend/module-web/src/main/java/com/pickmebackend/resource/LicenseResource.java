package com.pickmebackend.resource;

import com.pickmebackend.controller.LicenseController;
import com.pickmebackend.domain.dto.license.LicenseResponseDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class LicenseResource extends EntityModel<LicenseResponseDto> {

    public LicenseResource(LicenseResponseDto licenseResponseDto, Link... links) {
        super(licenseResponseDto, links);
        add(linkTo(LicenseController.class).slash(licenseResponseDto.getId()).withSelfRel());
    }
}
