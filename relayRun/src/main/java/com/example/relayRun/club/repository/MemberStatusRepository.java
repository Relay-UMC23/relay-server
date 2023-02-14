package com.example.relayRun.club.repository;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MemberStatusRepository extends JpaRepository<MemberStatusEntity, Long> {
    List<MemberStatusEntity> findByUserProfileIdx_UserProfileIdxAndStatus(Long userProfileIdx, String status);
    Optional<MemberStatusEntity> findByUserProfileIdx_UserProfileIdxAndApplyStatusIs(Long userProfileIdx, String applyStatus);
    Optional<MemberStatusEntity> findByUserProfileIdx_UserProfileIdxAndApplyStatusAndStatus(Long userProfileIdx, String applyStatus, String status);
    @Query(value = "select member_status_idx from member_status where club_idx = :clubIdx", nativeQuery = true)
    List<Long> selectMemberStatusIdxList(@Param(value = "clubIdx") Long clubIdx);

    List<MemberStatusEntity> findByClubIdx_ClubIdx(Long clubIdx);

    List<MemberStatusEntity> findAllByClubIdx_ClubIdxAndApplyStatusAndStatus(Long clubIdx, String applyStatus, String status);

    @Query(value = "select * from member_status where user_profile_idx = :userProfileIdx limit 1", nativeQuery = true)
    Optional<MemberStatusEntity> findByUserProfileIdx(Long userProfileIdx);
    Optional<List<MemberStatusEntity>> findAllByUserProfileIdx(UserProfileEntity userProfile);
    Optional<List<MemberStatusEntity>> findAllByClubIdx(ClubEntity clubEntity);
    //MemberStatusEntity findByUserProfileIdx(Long userProfileIdx);

    List<MemberStatusEntity> findByClubIdxAndStatus(ClubEntity club, String status);

    
    Optional<MemberStatusEntity> findByUserProfileIdx_UserProfileIdxAndClubIdx_ClubIdxAndApplyStatusAndStatus(Long userProfileIdx, Long clubIdx, String applyStatus, String status);

    @Query(value = "select count(member_status_idx) as num from member_status where club_idx = :clubIdx", nativeQuery = true)
    Long findByClubIdx(Long clubIdx);
}