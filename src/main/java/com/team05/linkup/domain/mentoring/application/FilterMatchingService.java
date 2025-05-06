package com.team05.linkup.domain.mentoring.application;

import com.team05.linkup.common.dto.UserPrincipal;
import com.team05.linkup.domain.community.domain.Community;
import com.team05.linkup.domain.community.domain.CommunityCategory;
import com.team05.linkup.domain.community.infrastructure.CommentRepository;
import com.team05.linkup.domain.community.infrastructure.CommunityRepository;
import com.team05.linkup.domain.enums.MentoringStatus;
import com.team05.linkup.domain.mentoring.domain.MentoringSessions;
import com.team05.linkup.domain.mentoring.dto.MentorProfileDTO;
import com.team05.linkup.domain.mentoring.infrastructure.MentoringRepository;
import com.team05.linkup.domain.user.domain.Sigungu;
import com.team05.linkup.domain.user.domain.SigunguId;
import com.team05.linkup.domain.user.domain.User;
import com.team05.linkup.domain.user.infrastructure.SigunguRepository;
import com.team05.linkup.domain.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterMatchingService {

    private static final Logger logger = LogManager.getLogger();
    private final UserRepository userRepository;
    private final MentoringRepository mentoringRepository;
    private final SigunguRepository sigunguRepository;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;

    public String createMatching(String nickname, UserPrincipal userPrincipal) {
        // 1. 닉네임 유효성 검증
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임이 유효하지 않습니다.");
        }

        // 2. 닉네임으로 멘토 조회
        User mentor = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 현재 사용자 조회
        String principalProvider = userPrincipal.provider();
        String principalProviderId = userPrincipal.providerId();
        User mentee = userRepository.findByProviderAndProviderId(principalProvider, principalProviderId)
                .orElseThrow(() -> new IllegalArgumentException("현재 사용자를 찾을 수 없습니다."));


        // 4. 멘토링 세션 생성
        MentoringSessions session = MentoringSessions.builder()
                .mentor(mentor)
                .mentee(mentee)
                .interest(mentor.getInterest())
                .status(MentoringStatus.IN_PROGRESS) // 상태: 진행 중
                .build();

        // 5. 저장
        mentoringRepository.save(session);
        logger.info("매칭이 성공적으로 생성되었습니다. 멘토: {}, 멘티: {}", mentor.getNickname(), mentee.getNickname());

        // 6. 멘토의 contactLink 반환
        return mentor.getContactLink();
    }

    @Transactional
    public MentorProfileDTO getMentor(User mentor) {
        // 1. Null 체크
        if (mentor.getArea() == null || mentor.getSigunguCode() == null) {
            throw new IllegalArgumentException("멘토의 지역 정보가 유효하지 않습니다.");
        }

        // 2. areaName 생성
        String areaName = mentor.getArea().getAreaName();

        // 3. sigunguName 생성
        String sigunguName = sigunguRepository.findById(SigunguId.builder()
                        .areacode(mentor.getArea().getAreacode())
                        .sigungucode(mentor.getSigunguCode())
                        .build())
                .map(Sigungu::getSigunguname)
                .orElseThrow(() -> new IllegalArgumentException("시군구 정보를 찾을 수 없습니다."));


        // 4. 커뮤니티 게시물 조회
        List<Community> communities = communityRepository
                .findByCategoryAndUserNicknameOrderByUpdatedAtDesc(CommunityCategory.TALENT, mentor.getNickname());

        if (communities.isEmpty()) {
            logger.info("멘토 {}의 커뮤니티 게시물이 없습니다.", mentor.getNickname());
        }
        logger.debug(1);
        // 5. 게시물 ID 목록 추출
        List<String> communityIds = communities.stream()
                .map(Community::getId)
                .collect(Collectors.toList());
        logger.debug(2);

        // 6. 댓글 수 조회
        Map<String, Long> commentCountMap = communityIds.isEmpty() ? Map.of() :
                commentRepository.countCommentsByCommunityIds(communityIds)
                        .stream()
                        .collect(Collectors.toMap(
                                arr -> (String) arr[0],
                                arr -> (Long) arr[1]
                        ));
        logger.debug(3);

        // 7. 커뮤니티 게시물 DTO 변환
        List<MentorProfileDTO.CommunityPost> communityPosts = communities.stream()
                .map(community -> {
                    Long commentCount = commentCountMap.getOrDefault(community.getId(), 0L);

                    return MentorProfileDTO.CommunityPost.builder()
                            .communityId(community.getId())
                            .title(community.getTitle())
                            .content(community.getContent())
                            .createdAt(community.getCreatedAt())
                            .likeCount(community.getLikeCount())
                            .comments(commentCount)
                            .build();
                })
                .collect(Collectors.toList());
        logger.debug(4);

        // 8. MentorProfileDTO 생성 및 반환
        return MentorProfileDTO.builder()
                .mentorId(mentor.getId())
                .nickname(mentor.getNickname())
                .interest(mentor.getInterest())
                .profileImageUrl(mentor.getProfileImageUrl())
                .area(areaName)
                .sigungu(sigunguName)
                .activityTime(mentor.getActivityTime())
                .introduction(mentor.getIntroduction())
                .communityPosts(communityPosts)
                .build();
    }
}
