package com.pickmebackend.repository;

import com.pickmebackend.domain.AccountTech;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountTechRepository extends JpaRepository<AccountTech, Long> {
    List<AccountTech> findAllByAccount_Id(Long accountId);
}
