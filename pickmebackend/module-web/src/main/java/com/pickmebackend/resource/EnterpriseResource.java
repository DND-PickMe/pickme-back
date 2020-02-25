package com.pickmebackend.resource;

import com.pickmebackend.controller.EnterpriseController;
import com.pickmebackend.domain.dto.enterprise.EnterpriseResponseDto;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@NoArgsConstructor
public class EnterpriseResource extends EntityModel<EnterpriseResponseDto> {

    public EnterpriseResource(EnterpriseResponseDto enterpriseResponseDto, Link... links) {
        super(enterpriseResponseDto, links);
        add(linkTo(EnterpriseController.class).slash(enterpriseResponseDto.getId()).withSelfRel());
    }
}
