package com.pickmebackend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccountTech is a Querydsl query type for AccountTech
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAccountTech extends EntityPathBase<AccountTech> {

    private static final long serialVersionUID = 666319033L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccountTech accountTech = new QAccountTech("accountTech");

    public final QAccount account;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QTechnology technology;

    public QAccountTech(String variable) {
        this(AccountTech.class, forVariable(variable), INITS);
    }

    public QAccountTech(Path<? extends AccountTech> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccountTech(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccountTech(PathMetadata metadata, PathInits inits) {
        this(AccountTech.class, metadata, inits);
    }

    public QAccountTech(Class<? extends AccountTech> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account"), inits.get("account")) : null;
        this.technology = inits.isInitialized("technology") ? new QTechnology(forProperty("technology")) : null;
    }

}

