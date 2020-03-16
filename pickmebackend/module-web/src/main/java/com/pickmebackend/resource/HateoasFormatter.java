package com.pickmebackend.resource;

import com.pickmebackend.properties.RestDocsConstants;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static com.pickmebackend.properties.RestDocsConstants.*;

@Component
public class HateoasFormatter {

    public void addProfileRel(EntityModel<?> resource, String message) {
        resource.add(new Link("/docs/index.html#" + message).withRel(PROFILE.getValue()));
    }
}
