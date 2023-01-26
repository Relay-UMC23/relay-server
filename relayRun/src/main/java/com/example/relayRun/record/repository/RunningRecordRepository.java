package com.example.relayRun.record.repository;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RunningRecordRepository extends JpaRepository<RunningRecordEntity, Long> {
    Optional<RunningRecordEntity> findByRunningRecordIdxAndStatus(Long idx, String status);
    List<RunningRecordEntity> findByMemberStatusIdxAndCreatedAtBetween(MemberStatusEntity member, LocalDateTime start, LocalDateTime end);
}
