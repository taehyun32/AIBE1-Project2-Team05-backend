package com.team05.linkup.domain.mentoring.dto;

import com.team05.linkup.domain.enums.ActivityTime;
import com.team05.linkup.domain.enums.Interest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
public class MentorProfileDTO {
    private String mentorId; // 멘토 ID
    private String nickname; // 닉네임
    private Interest interest; // 관심사
    private String profileImageUrl; // 프로필 이미지 URL
    private String area; // 지역
    private String sigungu; // 시군구
    private ActivityTime activityTime; // 활동 시간
    private String introduction; // 자기소개
    private List<CommunityPost> communityPosts; // 커뮤니티 게시글 리스트

    @Getter
    @Builder
    public static class CommunityPost {
        private String communityId; // 커뮤니티 ID
        private String title; // 제목
        private String content; // 내용
        private ZonedDateTime createdAt; // 생성 시간
        private Long likeCount; // 좋아요 수
        private Long comments; // 댓글 수
    }
}