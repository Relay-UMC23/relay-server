package com.example.relayRun.group.entity;

import com.example.relayRun.record.entity.RunningRecordEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
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
@Table(name = "MemberStatus")
public class MemberStatusEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberStatusIdx;

    @ManyToOne
    @JoinColumn(name = "groupIdx")
    private GroupEntity groupIdx;

    @ManyToOne
    @JoinColumn(name = "userProfileIdx")
    private UserProfileEntity userProfileIdx;

    @Column(length = 50)
    private String comment;

    @Column(nullable = false, columnDefinition = "varchar(10) default 'ACCEPTED'")
    private String applyStatus;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "memberStatusIdx", orphanRemoval = true)
    List<RunningRecordEntity> runningRecords = new ArrayList<>();
}
