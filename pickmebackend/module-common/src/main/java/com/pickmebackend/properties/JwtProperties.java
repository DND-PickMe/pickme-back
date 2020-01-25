package com.pickmebackend.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt-property")
@Component
@Getter @Setter
public class JwtProperties {

    private String secret;
}
