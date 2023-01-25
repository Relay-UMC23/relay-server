package com.example.relayRun.club.repository;

import com.example.relayRun.club.entity.MemberStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MemberStatusRepository extends JpaRepository<MemberStatusEntity, Long> {
    List<MemberStatusEntity> findByUserProfileIdx_UserProfileIdx(Long userProfileIdx);
    Optional<MemberStatusEntity> findByUserProfileIdx_UserProfileIdxAndApplyStatusIs(Long ProfileIdx, String applyStatus);
    @Query(value = "select member_status_idx from member_status where club_idx = :clubIdx", nativeQuery = true)
    List<Long> selectMemberStatusIdxList(@Param(value = "clubIdx") Long clubIdx);

    List<MemberStatusEntity> findByClubIdx_ClubIdx(Long clubIdx);
}
