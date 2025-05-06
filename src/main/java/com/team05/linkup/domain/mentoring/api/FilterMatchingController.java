package com.team05.linkup.domain.mentoring.api;

import com.team05.linkup.common.dto.ApiResponse;
import com.team05.linkup.common.dto.UserPrincipal;
import com.team05.linkup.common.enums.ResponseCode;
import com.team05.linkup.domain.mentoring.application.FilterMatchingService;
import com.team05.linkup.domain.mentoring.dto.MentorProfileDTO;
import com.team05.linkup.domain.user.domain.User;
import com.team05.linkup.domain.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/matching")
@RequiredArgsConstructor
public class FilterMatchingController {
    private static final Logger logger = LogManager.getLogger();
    private final UserRepository userRepository;
    private final FilterMatchingService filterMatchingService;

    @PostMapping("/{nickname}")
    @PreAuthorize("hasAuthority('ROLE_MENTEE')")
    public ResponseEntity<ApiResponse> createMatching(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String nickname) {
        Optional<User> userOpt = userRepository.findByProviderAndProviderId(
                userPrincipal.provider(), userPrincipal.providerId());
        if (userOpt.isEmpty())
            return ResponseEntity.ok(ApiResponse.error(ResponseCode.ENTITY_NOT_FOUND, "프로필을 찾을 수 없습니다."));

        try {
            String contactLink = filterMatchingService.createMatching(nickname, userPrincipal);
            return ResponseEntity.ok(ApiResponse.created(contactLink));
        } catch (IllegalArgumentException ex) {
            // 잘못된 요청 처리
            return ResponseEntity.ok(ApiResponse.error(ResponseCode.INVALID_INPUT_VALUE, ex.getMessage()));
        } catch (Exception ex) {
            logger.error("매칭 생성 중 알 수 없는 오류 발생: {}", ex.getMessage());
            return ResponseEntity.ok(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
        }
    }

    @GetMapping("/{nickname}")
    @PreAuthorize("hasAuthority('ROLE_MENTEE')")
    public ResponseEntity<ApiResponse<MentorProfileDTO>> getMentorProfile(@PathVariable String nickname) {
        Optional<User> userOpt = userRepository.findUserWithAreaByNickname(nickname);
        if (userOpt.isEmpty())
            return ResponseEntity.ok(ApiResponse.error(ResponseCode.ENTITY_NOT_FOUND, "멘토 프로필을 찾을 수 없습니다."));

        try {
            MentorProfileDTO mentorProfile = filterMatchingService.getMentor(userOpt.get());
            return ResponseEntity.ok(ApiResponse.success(mentorProfile));
        } catch (IllegalArgumentException ex) {
            // 잘못된 요청 처리
            return ResponseEntity.ok(ApiResponse.error(ResponseCode.INVALID_INPUT_VALUE, ex.getMessage()));
        } catch (Exception ex) {
            logger.error("멘토 프로필 조회 중 알 수 없는 오류 발생: {}", ex.getMessage());
            return ResponseEntity.ok(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
        }
    }
}
