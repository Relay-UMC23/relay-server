package com.example.relayRun.club.entity;

import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "member_status")
public class MemberStatusEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberStatusIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clubIdx")
    private ClubEntity clubIdx;

    @ManyToOne
    @JoinColumn(name = "userProfileIdx")
    private UserProfileEntity userProfileIdx;

    @Column(nullable = false, columnDefinition = "varchar(10) default 'ACCEPTED'")
    private String applyStatus;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "memberStatusIdx", orphanRemoval = true)
    List<RunningRecordEntity> runningRecords = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "memberStatusIdx", orphanRemoval = true)
    List<TimeTableEntity> timeTables = new ArrayList<>();

    @Builder
    public MemberStatusEntity(ClubEntity clubIdx, UserProfileEntity userProfileIdx) {
        this.clubIdx = clubIdx;
        this.userProfileIdx = userProfileIdx;
    }
}
