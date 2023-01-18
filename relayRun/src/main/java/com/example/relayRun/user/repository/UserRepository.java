package com.example.relayRun.user.repository;

import com.example.relayRun.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <UserEntity, Long> {
    UserEntity findAllByUserIdx(Long userIdx);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
}
