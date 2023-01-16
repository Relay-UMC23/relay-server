package com.example.relayRun.group.entity;

import com.example.relayRun.user.entity.UserEntity;
import com.example.relayRun.user.entity.UserProfileEntity;
import com.example.relayRun.util.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Group")
public class GroupEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupIdx;

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
    private Character acceptAll;

    @Column(nullable = false, columnDefinition = "integer default 8")
    private Integer maxNum;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer goalType;

    @Column(nullable = false)
    private Float goal;

    @Column(columnDefinition = "varchar(10) default 'recruiting'")
    private String recruitStatus;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

}
