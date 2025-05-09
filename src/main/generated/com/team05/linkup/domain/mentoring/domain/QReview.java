package com.team05.linkup.domain.mentoring.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = 1031428741L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final com.team05.linkup.domain.baseEntity.QBaseEntity _super = new com.team05.linkup.domain.baseEntity.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> createdAt = _super.createdAt;

    public final StringPath id = createString("id");

    public final EnumPath<com.team05.linkup.domain.enums.Interest> interest = createEnum("interest", com.team05.linkup.domain.enums.Interest.class);

    public final QMentoringSessions mentoringSession;

    public final NumberPath<java.math.BigDecimal> star = createNumber("star", java.math.BigDecimal.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.ZonedDateTime> updatedAt = _super.updatedAt;

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.mentoringSession = inits.isInitialized("mentoringSession") ? new QMentoringSessions(forProperty("mentoringSession"), inits.get("mentoringSession")) : null;
    }

}

