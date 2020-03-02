package com.pickmebackend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccount is a Querydsl query type for Account
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAccount extends EntityPathBase<Account> {

    private static final long serialVersionUID = 642710403L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccount account = new QAccount("account");

    public final SetPath<AccountTech, QAccountTech> accountTechSet = this.<AccountTech, QAccountTech>createSet("accountTechSet", AccountTech.class, QAccountTech.class, PathInits.DIRECT2);

    public final StringPath career = createString("career");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final QEnterprise enterprise;

    public final SetPath<Experience, QExperience> experiences = this.<Experience, QExperience>createSet("experiences", Experience.class, QExperience.class, PathInits.DIRECT2);

    public final ListPath<Account, QAccount> favorite = this.<Account, QAccount>createList("favorite", Account.class, QAccount.class, PathInits.DIRECT2);

    public final NumberPath<Long> favoriteCount = createNumber("favoriteCount", Long.class);

    public final NumberPath<Long> hits = createNumber("hits", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final SetPath<License, QLicense> licenses = this.<License, QLicense>createSet("licenses", License.class, QLicense.class, PathInits.DIRECT2);

    public final StringPath nickName = createString("nickName");

    public final StringPath oneLineIntroduce = createString("oneLineIntroduce");

    public final StringPath password = createString("password");

    public final SetPath<String, StringPath> positions = this.<String, StringPath>createSet("positions", String.class, StringPath.class, PathInits.DIRECT2);

    public final SetPath<Prize, QPrize> prizes = this.<Prize, QPrize>createSet("prizes", Prize.class, QPrize.class, PathInits.DIRECT2);

    public final SetPath<Project, QProject> projects = this.<Project, QProject>createSet("projects", Project.class, QProject.class, PathInits.DIRECT2);

    public final SetPath<SelfInterview, QSelfInterview> selfInterviews = this.<SelfInterview, QSelfInterview>createSet("selfInterviews", SelfInterview.class, QSelfInterview.class, PathInits.DIRECT2);

    public final StringPath socialLink = createString("socialLink");

    public final EnumPath<com.pickmebackend.domain.enums.UserRole> userRole = createEnum("userRole", com.pickmebackend.domain.enums.UserRole.class);

    public QAccount(String variable) {
        this(Account.class, forVariable(variable), INITS);
    }

    public QAccount(Path<? extends Account> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccount(PathMetadata metadata, PathInits inits) {
        this(Account.class, metadata, inits);
    }

    public QAccount(Class<? extends Account> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.enterprise = inits.isInitialized("enterprise") ? new QEnterprise(forProperty("enterprise"), inits.get("enterprise")) : null;
    }

}

