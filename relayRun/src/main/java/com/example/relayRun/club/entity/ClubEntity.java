package com.example.relayRun.club.entity;

import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table(name = "club")
public class ClubEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubIdx;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 50)
    private String content;

    @Column(columnDefinition = "text")
    private String imgURL;

    @OneToOne
    @JoinColumn(name = "userIdx")
    private UserProfileEntity hostIdx;

    @Column(nullable = false, columnDefinition = "varchar(1) default 'Y'")
    private String acceptAll;

    @Column(nullable = false, columnDefinition = "integer default 8")
    private Integer maxNum;

    @Column(nullable = false)
    private Integer level;

    @Column(name = "goalType", nullable = false)
    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    @Column()
    private Float goal;

    @Column(columnDefinition = "varchar(10) default 'recruiting'")
    private String recruitStatus;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @Builder
    public ClubEntity(Long clubIdx, String name, String content, UserProfileEntity hostIdx,
                      Integer level, GoalType goalType, Float goal){
        this.clubIdx = clubIdx;
        this.name = name;
        this.content = content;
        this.hostIdx = hostIdx;
        this.level = level;
        this.goalType = goalType;
        this.goal = goal;
    }

}
