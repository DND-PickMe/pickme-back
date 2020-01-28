package com.pickmebackend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BeanConfigTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("PasswordEncoder 검증")
    void test_PasswordEncoder() {
        String password = "testPassword";
        String encodedPassword = passwordEncoder.encode(password);
        then(passwordEncoder.matches(password, encodedPassword)).isTrue();
    }
}
