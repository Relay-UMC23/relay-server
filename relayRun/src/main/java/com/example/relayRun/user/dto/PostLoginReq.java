package com.example.relayRun.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostLoginReq {
    private String email;
    private String pwd;
}
