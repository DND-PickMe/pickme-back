package com.pickmebackend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPrize is a Querydsl query type for Prize
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPrize extends EntityPathBase<Prize> {

    private static final long serialVersionUID = 814973672L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPrize prize = new QPrize("prize");

    public final QAccount account;

    public final StringPath competition = createString("competition");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> issuedDate = createDate("issuedDate", java.time.LocalDate.class);

    public final StringPath name = createString("name");

    public QPrize(String variable) {
        this(Prize.class, forVariable(variable), INITS);
    }

    public QPrize(Path<? extends Prize> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPrize(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPrize(PathMetadata metadata, PathInits inits) {
        this(Prize.class, metadata, inits);
    }

    public QPrize(Class<? extends Prize> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account"), inits.get("account")) : null;
    }

}

