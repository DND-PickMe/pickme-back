package com.pickmebackend.repository.enterprise;

import com.pickmebackend.domain.Enterprise;
import com.pickmebackend.domain.dto.enterprise.EnterpriseFilterRequestDto;
import com.pickmebackend.domain.enums.UserRole;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import static com.pickmebackend.domain.QEnterprise.enterprise;

@Repository
public class EnterpriseRepositoryImpl extends QuerydslRepositorySupport implements EnterpriseRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public EnterpriseRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Enterprise.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Enterprise> filterEnterprise(EnterpriseFilterRequestDto enterpriseFilterRequestDto, Pageable pageable) {
        QueryResults<Enterprise> filteredEnterprises = jpaQueryFactory
                .selectFrom(enterprise)
                .where(
                        eqUser(),
                        eqName(enterpriseFilterRequestDto.getName()),
                        eqAddress(enterpriseFilterRequestDto.getAddress())
                )
                .orderBy(enterprise.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(filteredEnterprises.getResults(), pageable, filteredEnterprises.getTotal());
    }

    private BooleanExpression eqUser() {
        return enterprise.account.userRole.eq(UserRole.ENTERPRISE);
    }

    private BooleanExpression eqName(String name) {
        if(StringUtils.isEmpty(name))
            return null;
        return enterprise.name.contains(name);
    }

    private BooleanExpression eqAddress(String address) {
        if (StringUtils.isEmpty(address))
            return null;
        return enterprise.address.contains(address);
    }
}
