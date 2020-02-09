package com.pickmebackend.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class AccountImagePropertiesTest {

    @Autowired
    AccountImageProperties accountImageProperties;

    @Test
    void accountImagePropertiesTest() {
        assertNotNull(accountImageProperties.getLocation());
        assertEquals(accountImageProperties.getLocation(), "./pickme-images");
    }
}