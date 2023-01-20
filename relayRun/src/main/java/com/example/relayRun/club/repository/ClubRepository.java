package com.example.relayRun.club.repository;

import com.example.relayRun.club.dto.GetClubListRes;
import com.example.relayRun.club.entity.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository <ClubEntity, Long> {
    List<GetClubListRes> findByOrderByRecruitStatusDesc();
    List<ClubEntity> findAll();
    List<GetClubListRes> findByNameContaining(String search);
}
