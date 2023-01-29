package com.example.relayRun.user.entity;

import com.example.relayRun.user.repository.UserProfileRepository;
import com.example.relayRun.util.BaseTimeEntity;
import com.example.relayRun.util.Role;
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
@Table(name = "user")
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userIdx;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String pwd;

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

    @Column(name = "loginType")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "userProfileIdx", orphanRemoval = true)
    private List<UserProfileEntity> userProfileEntities = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public UserEntity(String name, String email, String pwd, LoginType loginType, String status, Role role){
            this.name = name;
            this.email = email;
            this.pwd = pwd;
            this.loginType = loginType;
            this.status = status;
            this.role = role;
        }
    public void changePwd(String pwd){
        this.pwd = pwd;
        }
}
