package com.pickmebackend.resource;

import com.pickmebackend.controller.LicenseController;
import com.pickmebackend.domain.License;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class LicenseResource extends EntityModel<License> {

    public LicenseResource(License license, Link... links) {
        super(license, links);
        add(linkTo(LicenseController.class).slash(license.getId()).withSelfRel());
    }
}
