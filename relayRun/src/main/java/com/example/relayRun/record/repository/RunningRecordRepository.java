package com.example.relayRun.record.repository;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.record.dto.GetClubCalender;
import com.example.relayRun.record.dto.GetClubCalenderInterface;
import com.example.relayRun.record.entity.RunningRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RunningRecordRepository extends JpaRepository<RunningRecordEntity, Long> {
    Optional<RunningRecordEntity> findByRunningRecordIdxAndStatus(Long idx, String status);
    List<RunningRecordEntity> findByMemberStatusIdxAndCreatedAtBetweenAndRunningStatus(MemberStatusEntity member, LocalDateTime start, LocalDateTime end, String status);
    List<RunningRecordEntity> findByMemberStatusIdx_ClubIdxAndCreatedAtBetweenAndRunningStatus(ClubEntity club, LocalDateTime start, LocalDateTime end, String status);

    @Query("select r from RunningRecordEntity r where r.memberStatusIdx.memberStatusIdx = :memberStatusIdx and r.createdAt between :startDate and :endDate")
    List<RunningRecordEntity> selectByMemberStatusIdxAndDate(@Param("memberStatusIdx") Long memberStatusIdx, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "select * from running_record as r " +
            "where r.member_status_idx in :idxList and " +
            "YEAR(r.created_at) = :year and MONTH(r.created_at) = :month and " +
            "r.status = :status", nativeQuery = true)
    List<RunningRecordEntity> selectByMemberStatusAndYearAndMonthAndStatus(
            @Param("idxList") List<MemberStatusEntity> list,
            @Param("year") Integer Year,
            @Param("month") Integer month,
            @Param("status") String status);

    @Query(value = "select sum(r.time), sum(r.distance), avg(r.pace) from runningmen.running_record as r " +
            "where r.member_status_idx in :idxList and " +
            "YEAR(r.created_at) = :year and MONTH(r.created_at) = :month and " +
            "r.status = :status " +
            "", nativeQuery = true)
    List<Tuple> selectByMemberStatusAndYearAndMonthAndStatus_2(
            @Param("idxList") List<MemberStatusEntity> list,
            @Param("year") Integer Year,
            @Param("month") Integer month,
            @Param("status") String status);

    @Query(value = "select r.running_record_idx from running_record as r " +
            "where r.member_status_idx in :idxList and " +
            "Date(r.created_at) = :date and " +
            "r.status = :status", nativeQuery = true)
    Optional<Long> selectByMemberStatusAndDateAndStatus(
            @Param("idxList") List<MemberStatusEntity> list,
            @Param("date") LocalDate date,
            @Param("status") String status);

}
