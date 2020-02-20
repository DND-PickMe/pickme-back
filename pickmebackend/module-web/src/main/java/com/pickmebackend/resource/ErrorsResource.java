package com.pickmebackend.resource;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

public class ErrorsResource extends EntityModel<Errors>  {

    public ErrorsResource(Errors errors, String message,  Link... links) {
        super(errors, links);
//        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}