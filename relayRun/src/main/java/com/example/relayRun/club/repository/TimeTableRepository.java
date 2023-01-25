package com.example.relayRun.club.repository;

import com.example.relayRun.club.entity.TimeTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTableEntity, Long> {
    @Query(value = "select time_table_idx from time_table left join member_status on time_table.member_status_idx = member_status.member_status_idx where member_status.club_idx = :clubIdx and time_table.day = :day and ((:start between time_table.start and time_table.end) or (:end between time_table.start and time_table.end) or (:start <= time_table.start and :end >= time_table.end))", nativeQuery = true)
    List<Long> selectDuplicateTimeTable(@Param(value = "clubIdx") Long clubIdx,
                                        @Param(value = "day") Integer day,
                                        @Param(value = "start") LocalDateTime start,
                                        @Param(value = "end") LocalDateTime end);

    List<TimeTableEntity> findByMemberStatusIdx_MemberStatusIdx(Long memberStatusIdx);
}