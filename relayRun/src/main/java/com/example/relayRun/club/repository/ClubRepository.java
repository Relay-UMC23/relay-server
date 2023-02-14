package com.example.relayRun.club.repository;

import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository <ClubEntity, Long> {
    Optional<ClubEntity> findByClubIdx(Long clubIdx);
    List<GetClubListRes> findByOrderByRecruitStatusDesc();
    List<ClubEntity> findByStatusOrderByCreatedAtDesc(String Status);
    List<ClubEntity> findAll();
    List<GetClubListRes> findByNameContaining(String search);
    Optional<ClubEntity> findByHostIdx(UserProfileEntity hostIdx);
    Optional<List<ClubEntity>> findAllByHostIdx(UserProfileEntity userProfile);
//    List<GetClubListRes> findByNameContaining(String search);
    List<ClubEntity> findByNameContainingAndStatus(String search, String status);
    Optional<ClubEntity> findByClubIdxAndStatus(Long id, String status);

}
