package com.pickmebackend.repository;

import com.pickmebackend.domain.Enterprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    @Query("SELECT ent FROM Enterprise ent ORDER BY ent.name")
    Page<Enterprise> findAllEnterprisesDesc(Pageable pageable);
}
