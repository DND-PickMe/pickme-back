package com.pickmebackend.repository.account;

import com.pickmebackend.domain.Account;
import com.pickmebackend.domain.dto.account.AccountFilteringRequestDto;
import com.pickmebackend.domain.enums.UserRole;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import static com.pickmebackend.domain.QAccount.account;

@Repository
public class AccountRepositoryImpl extends QuerydslRepositorySupport implements AccountRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public AccountRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Account.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Account> filterAccount(AccountFilteringRequestDto requestDto, Pageable pageable)  {
        QueryResults<Account> filteredAccounts = jpaQueryFactory
                .selectFrom(account)
                .where(
                        eqUser(),
                        eqNickName(requestDto.getNickName()),
                        eqOneLineIntroduce(requestDto.getOneLineIntroduce()),
                        eqCareer(requestDto.getCareer()),
                        eqPositions(requestDto.getPositions()),
                        eqTech(requestDto.getTechnology())
                )
                .orderBy(eqOrderBy(requestDto.getOrderBy()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(filteredAccounts.getResults(), pageable, filteredAccounts.getTotal());
    }

    private BooleanExpression eqUser() {
        return account.userRole.eq(UserRole.USER);
    }

    private BooleanExpression eqNickName(String nickName) {
        if(StringUtils.isEmpty(nickName))
            return null;
        return account.nickName.contains(nickName);
    }

    private BooleanExpression eqOneLineIntroduce(String oneLineIntroduce) {
        if(StringUtils.isEmpty(oneLineIntroduce))
            return null;
        return account.oneLineIntroduce.contains(oneLineIntroduce);
    }

    private BooleanExpression eqCareer(String career) {
        if(StringUtils.isEmpty(career))
            return null;
        return account.career.eq(career);
    }

    private BooleanExpression eqPositions(String positions) {
        if(StringUtils.isEmpty(positions))
            return null;
        return account.positions.contains(positions);
    }

    private BooleanExpression eqTech(String technology) {
        if (StringUtils.isEmpty(technology))
            return null;
        return account.accountTechSet.any().technology.name.eq(technology);
    }

    private OrderSpecifier<?> eqOrderBy(String orderBy) {
//        if (StringUtils.isEmpty(orderBy))
//            return account.createdAt.desc();
         if("favorite".equals(orderBy))
            return account.favoriteCount.desc();
        else if("hits".equals(orderBy))
            return account.hits.desc();
        return account.createdAt.desc();
    }
}
