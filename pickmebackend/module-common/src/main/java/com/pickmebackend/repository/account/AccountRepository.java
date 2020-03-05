package com.pickmebackend.repository.account;

import com.pickmebackend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom {
    Optional<Account> findByEmail(String email);
}
