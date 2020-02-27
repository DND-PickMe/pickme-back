package com.pickmebackend.repository;

import com.pickmebackend.domain.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT acc FROM Account acc WHERE acc.userRole = 'USER' ORDER BY acc.createdAt DESC")
    Page<Account> findAllAccountsDesc(Pageable pageable);

    Optional<Account> findByEmail(String email);

    @Query("SELECT acc FROM Account acc WHERE acc.userRole = 'USER' ORDER BY acc.favoriteCount DESC")
    Page<Account> findAllAccountsDescAndOrderBy(Pageable pageable);
}
