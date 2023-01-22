package com.example.relayRun.club.repository;

import com.example.relayRun.club.entity.MemberStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberStatusRepository extends JpaRepository<MemberStatusEntity, Long> {
    List<MemberStatusEntity> findByUserProfileIdx_UserProfileIdx(Long userProfileIdx);
}
