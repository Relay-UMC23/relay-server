package com.example.relayRun.club.repository;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.dto.ClubDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository <ClubEntity, Long> {
    List<ClubEntity> findAll();
    List<ClubEntity> findByNameContaining(String search);
}
