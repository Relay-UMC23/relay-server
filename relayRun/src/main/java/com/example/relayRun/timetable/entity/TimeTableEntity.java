package com.example.relayRun.timetable.entity;

import com.example.relayRun.group.entity.MemberStatusEntity;
import com.example.relayRun.util.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TimeTable")
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

    @Column(nullable = false)
    private Integer goalType;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

}
