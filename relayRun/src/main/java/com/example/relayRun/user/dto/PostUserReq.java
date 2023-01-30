package com.example.relayRun.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.*;

import javax.management.relation.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {
    @ApiModelProperty(hidden = true)
    private Long userIdx;

    private String name;

    private String email;

    private String pwd;

    @ApiModelProperty(hidden = true)
    private Role role;

    @Builder
    public PostUserReq(Long userIdx, String name, String email, String pwd){
        this.userIdx = userIdx;
        this.name = name;
        this.email = email;
        this.pwd = pwd;
    }
}
