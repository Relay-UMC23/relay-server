package com.example.relayRun.club.entity;

import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table(name = "Club")
public class ClubEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubIdx;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 50)
    private String content;

    @Column(nullable = false, columnDefinition = "text")
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

    @Column(name = "goalType", nullable = false, columnDefinition = "integer default 0")
    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    @Column(nullable = true)
    private Float goal;

    @Column(columnDefinition = "varchar(10) default 'recruiting'")
    private String recruitStatus;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

}
