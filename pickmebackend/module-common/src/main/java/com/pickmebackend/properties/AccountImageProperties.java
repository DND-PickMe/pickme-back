package com.pickmebackend.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "image")
@Component
@Getter @Setter
public class AccountImageProperties {

    private String location;
}
