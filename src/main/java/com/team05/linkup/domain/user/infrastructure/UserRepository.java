package com.team05.linkup.domain.user.infrastructure;

import com.team05.linkup.domain.enums.Interest;
import com.team05.linkup.domain.enums.Role;
import com.team05.linkup.domain.mentoring.dto.ProfileTagInterestDTO;
import com.team05.linkup.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findById(String id);

    // 닉네임으로 Profile을 찾는 메서드
    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u WHERE u.providerId = :providerId")
    Optional<User> findByProviderId(@Param("providerId") String providerId);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    @Modifying
    @Query("""
                UPDATE User u SET u.role = :role
                WHERE u.id = :id AND u.role = 'ROLE_TEMP'
          """)
    void updateUserRole(@Param("id") String id, @Param("role") Role role);

    @Query("""
        SELECT new com.team05.linkup.domain.mentoring.dto.ProfileTagInterestDTO(u.profileTag, u.interest)
        FROM User u
        WHERE u.providerId = :providerId AND u.provider = :provider
    """)
    ProfileTagInterestDTO findProfileTagAndInterestByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);

    @Query("""
        SELECT u.area.areacode,
                       area.areaName,
                       u.sigunguCode,
                       sigungu.sigunguname,
                       u.nickname,
                       u.profileTag,
                       u.profileImageUrl,
                       u.providerId
        FROM User u, Area area, Sigungu sigungu
        WHERE u.providerId <> :providerId AND u.provider = :provider AND u.interest = :interest
    """)
    List<Object[]> findOtherProfileTagsByProviderId(@Param("provider") String provider,
                                                    @Param("providerId") String providerId,
                                                    @Param("interest") Interest interest);

    // 닉네임을 기준으로 사용자와 지역 정보를 조회하는 쿼리
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.area WHERE u.nickname = :nickname")
    Optional<User> findUserWithAreaByNickname(@Param("nickname") String nickname);
}