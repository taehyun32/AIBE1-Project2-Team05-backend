package com.team05.linkup.domain.mentoring.domain;

import com.team05.linkup.domain.baseEntity.BaseEntity;
import com.team05.linkup.domain.enums.MentoringStatus;
import com.team05.linkup.domain.user.domain.User;
import com.team05.linkup.domain.enums.Interest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mentoring_sessions", indexes = {
        @Index(name = "idx_mentoring_mentor", columnList = "mentor_user_id"),
        @Index(name = "idx_mentoring_mentee", columnList = "mentee_user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class MentoringSessions extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;  // 멘토링 세션 ID (UUID)

    @ManyToOne
    @JoinColumn(name = "mentor_user_id", nullable = false)
    private User mentor;  // 멘토 (User 엔티티와 다대일 관계)

    @ManyToOne
    @JoinColumn(name = "mentee_user_id", nullable = false)
    private User mentee;  // 멘티 (User 엔티티와 다대일 관계)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Interest interest;  // 멘토링 관심 분야 (ex: 프로그래밍/웹개발, 디자인 등)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private MentoringStatus status;  // 멘토링 진행 상태 (ex: '진행중', '완료')
}
