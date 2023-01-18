package com.example.relayRun.timetable.entity;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table(name = "time_table")
public class TimeTableEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeTableIdx;

    @ManyToOne
    @JoinColumn(name = "memberStatusIdx")
    private MemberStatusEntity memberStatusIdx;

    @Column(nullable = false)
    private Integer day;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime end;

    @Column(nullable = false)
    private Float goal;

    @Column(name = "goalType", nullable = false)
    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

}
