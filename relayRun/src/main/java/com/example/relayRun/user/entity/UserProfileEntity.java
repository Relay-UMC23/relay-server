package com.example.relayRun.user.entity;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.entity.MemberStatusEntity;
import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.util.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
@Table(name = "user_profile")
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


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "hostIdx", orphanRemoval = true)
    private ClubEntity club;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "userProfileIdx", orphanRemoval = true)
    List<MemberStatusEntity> memberStatusEntities = new ArrayList<>();

    public void changeNickName(String nickName){
        this.nickName = nickName;
    }

    public void changeImgUrl(String imgURL){
        this.imgURL = imgURL;
    }

    public void changeStatusMsg(String statusMsg){
        this.statusMsg = statusMsg;
    }

    public void setIsAlarmOn (String isAlarmOn){
        this.isAlarmOn = isAlarmOn;
    }
}

