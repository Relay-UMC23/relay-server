package com.example.relayRun.record.entity;

import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.util.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
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

    @Column(columnDefinition = "varchar(10) default 'fail'")
    private String goalStatus;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="recordIdx", orphanRemoval = true)
    private List<LocationEntity> locations = new ArrayList<>();
}