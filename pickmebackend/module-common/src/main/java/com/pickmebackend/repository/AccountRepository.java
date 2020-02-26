package com.pickmebackend.repository;

import com.pickmebackend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT acc FROM Account acc ORDER BY acc.createdAt DESC")
    List<Account> findAllDesc();

    Optional<Account> findByEmail(String email);

}
