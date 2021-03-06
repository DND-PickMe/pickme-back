package com.pickmebackend.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app-property")
@Component
@Getter @Setter
public class AppProperties {

    private String testEmail;

    private String testPassword;

    private String testNickname;

    private String testAnotherEmail;

    private String testAnotherNickname;

    private String testRegistrationNumber;

    private String testName;

    private String testAddress;

    private String testCeoName;
}
