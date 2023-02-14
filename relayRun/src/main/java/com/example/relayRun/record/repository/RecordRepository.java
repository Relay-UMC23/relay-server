package com.example.relayRun.record.repository;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository <RunningRecordEntity, Long> {
    Optional<RunningRecordEntity> findByRunningRecordIdxAndStatus(Long idx, String status);
    Optional<RunningRecordEntity> findByMemberStatusIdx(MemberStatusEntity memberStatus);
    Optional<List<RunningRecordEntity>> findAllByMemberStatusIdx(MemberStatusEntity memberStatus);
}
