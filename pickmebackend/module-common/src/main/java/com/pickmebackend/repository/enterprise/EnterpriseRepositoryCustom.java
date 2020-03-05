package com.pickmebackend.repository.enterprise;

import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.enterprise.EnterpriseFilterRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnterpriseRepositoryCustom {
    Page<Enterprise> filterEnterprise(EnterpriseFilterRequestDto enterpriseFilterRequestDto, Pageable pageable);
}
