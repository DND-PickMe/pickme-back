package com.pickmebackend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QTechnology is a Querydsl query type for Technology
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTechnology extends EntityPathBase<Technology> {

    private static final long serialVersionUID = -854488074L;

    public static final QTechnology technology = new QTechnology("technology");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QTechnology(String variable) {
        super(Technology.class, forVariable(variable));
    }

    public QTechnology(Path<? extends Technology> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTechnology(PathMetadata metadata) {
        super(Technology.class, metadata);
    }

}

