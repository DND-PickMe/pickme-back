package com.pickmebackend;

import com.pickmebackend.domain.Account;
import com.pickmebackend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DummyData implements ApplicationRunner {

    private final AccountRepository accountRepository;

    private final PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        accountRepository.save(
                Account.builder()
                .email("test@email.com")
                .password(encoder.encode("password"))
                .nickName("testnickname")
                .createdAt(LocalDateTime.now())
                .build()
        );
    }
}
