package com.example.relayRun.user.entity;

import com.example.relayRun.util.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "UserProfile")
public class UserProfileEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProfileIdx;

    @ManyToOne
    @JoinColumn(name = "userIdx")
    private UserEntity userIdx;

    @Column(nullable = false, length = 20)
    private String nickName;

    @Column(columnDefinition = "text")
    private String imgURL;

    @Column(columnDefinition = "text")
    private String statusMsg;

    @Column(columnDefinition = "varchar(1) default 'y'")
    private String isAlarmOn;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;


}

