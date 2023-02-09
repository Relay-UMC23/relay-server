package com.example.relayRun.club.repository;

import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.entity.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository <ClubEntity, Long> {
    List<GetClubListRes> findByStatusOrderByCreatedAtDesc(String status);
    List<GetClubListRes> findByNameContainingAndStatusOrderByCreatedAtDesc(String search, String status);
    Optional<ClubEntity> findByClubIdxAndRecruitStatusAndStatus(Long id, String recruitStatus, String status);
    Optional<ClubEntity> findByClubIdxAndStatus(Long id, String status);
}
