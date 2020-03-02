package com.pickmebackend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEnterprise is a Querydsl query type for Enterprise
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEnterprise extends EntityPathBase<Enterprise> {

    private static final long serialVersionUID = 22100555L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEnterprise enterprise = new QEnterprise("enterprise");

    public final QAccount account;

    public final StringPath address = createString("address");

    public final StringPath ceoName = createString("ceoName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath registrationNumber = createString("registrationNumber");

    public QEnterprise(String variable) {
        this(Enterprise.class, forVariable(variable), INITS);
    }

    public QEnterprise(Path<? extends Enterprise> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEnterprise(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEnterprise(PathMetadata metadata, PathInits inits) {
        this(Enterprise.class, metadata, inits);
    }

    public QEnterprise(Class<? extends Enterprise> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account"), inits.get("account")) : null;
    }

}

