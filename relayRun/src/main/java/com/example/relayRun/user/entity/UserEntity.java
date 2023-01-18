package com.example.relayRun.user.entity;

import com.example.relayRun.util.BaseTimeEntity;
import lombok.AllArgsConstructor;
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
@Table(name = "User")
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String pwd;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @Column(name = "loginType", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userProfileIdx", orphanRemoval = true)
    private List<UserProfileEntity> userProfileEntities = new ArrayList<>();

}
