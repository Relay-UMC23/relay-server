package com.example.relayRun.record.entity;

import com.example.relayRun.group.entity.MemberStatusEntity;
import com.example.relayRun.util.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RunningRecord")
public class RunningRecordEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long runningRecordIdx;

    @ManyToOne
    @JoinColumn(name = "memberStatusIdx")
    private MemberStatusEntity memberStatusIdx;

    @Column(columnDefinition = "varchar(10) default 'running'")
    private String runningStatus;

    @Column(nullable = false)
    private Float distance;

    @Column(nullable = false)
    private Float pace;

    @Column(nullable = false)
    private Float time;

    @Column(columnDefinition = "varchar(10) default 'nogoal'")
    private String goalStatus;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "runningRecordIdx", orphanRemoval = true)
    private List<LocationEntity> locations = new ArrayList<>();
}