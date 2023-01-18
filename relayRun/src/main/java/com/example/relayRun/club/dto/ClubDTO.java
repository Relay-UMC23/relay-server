package com.example.relayRun.club.dto;

import lombok.*;

@NoArgsConstructor
public class ClubDTO {

//    @Getter
//    @Setter
//    @AllArgsConstructor
////    @Builder
//    public class Club {
//        private Long clubIdx;
//        private String name;
//        private String content;
//        private String imgURL;
//        private Long hostIdx;
//
//
//    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ClubList {
        private Long clubIdx;
        private String name;
        private String content;
        private String imgURL;
        private String recruitStatus;
    }

}
