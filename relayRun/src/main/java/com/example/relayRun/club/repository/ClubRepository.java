package com.example.relayRun.club.repository;

import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository <ClubEntity, Long> {
    List<GetClubListRes> findByOrderByRecruitStatusDesc();
    List<ClubEntity> findAll();
    List<GetClubListRes> findByNameContaining(String search);
    Optional<ClubEntity> findByHostIdx(UserProfileEntity hostIdx);
    Optional<List<ClubEntity>> findAllByHostIdx(UserProfileEntity userProfile);
    Optional<ClubEntity> findByClubIdxAndStatus(Long id, String status);
}
