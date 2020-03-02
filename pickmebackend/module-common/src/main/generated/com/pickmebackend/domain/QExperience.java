package com.pickmebackend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QExperience is a Querydsl query type for Experience
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QExperience extends EntityPathBase<Experience> {

    private static final long serialVersionUID = 739270740L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QExperience experience = new QExperience("experience");

    public final QAccount account;

    public final StringPath companyName = createString("companyName");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> joinedAt = createDate("joinedAt", java.time.LocalDate.class);

    public final StringPath position = createString("position");

    public final DatePath<java.time.LocalDate> retiredAt = createDate("retiredAt", java.time.LocalDate.class);

    public QExperience(String variable) {
        this(Experience.class, forVariable(variable), INITS);
    }

    public QExperience(Path<? extends Experience> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QExperience(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QExperience(PathMetadata metadata, PathInits inits) {
        this(Experience.class, metadata, inits);
    }

    public QExperience(Class<? extends Experience> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account"), inits.get("account")) : null;
    }

}

