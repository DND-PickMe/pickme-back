package com.pickmebackend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSelfInterview is a Querydsl query type for SelfInterview
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSelfInterview extends EntityPathBase<SelfInterview> {

    private static final long serialVersionUID = 853160043L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSelfInterview selfInterview = new QSelfInterview("selfInterview");

    public final QAccount account;

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath title = createString("title");

    public QSelfInterview(String variable) {
        this(SelfInterview.class, forVariable(variable), INITS);
    }

    public QSelfInterview(Path<? extends SelfInterview> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSelfInterview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSelfInterview(PathMetadata metadata, PathInits inits) {
        this(SelfInterview.class, metadata, inits);
    }

    public QSelfInterview(Class<? extends SelfInterview> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account"), inits.get("account")) : null;
    }

}

