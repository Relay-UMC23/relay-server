package com.example.relayRun.record.entity;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.util.BaseTimeEntity;
import com.example.relayRun.util.GoalType;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "running_record")
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

    @Column(nullable = true)
    private Float goal;

    @Column(name = "goalType", nullable = true)
    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    @Column(columnDefinition = "varchar(1) default 'n'")
    private String goalStatus;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="recordIdx", orphanRemoval = true)
    private List<LocationEntity> locations = new ArrayList<>();
}