package com.example.relayRun.user.repository;

import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
    Optional<UserProfileEntity> findByUserProfileIdx(Long userProfileIdx);
    Optional<UserProfileEntity> findByUserProfileIdxAndStatus(Long userProfileIdx, String status);
    List<UserProfileEntity> findAllByUserIdx(UserEntity user);
    List<UserProfileEntity> findAllByUserIdxAndStatus(UserEntity user, String status);
    Optional<UserProfileEntity> findByUserIdx(UserEntity user);
    UserProfileEntity findOneByUserProfileIdx(Long userProfileIdx);
}
