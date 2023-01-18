package com.example.relayRun.club.service;

import com.example.relayRun.club.entity.ClubEntity;
import com.example.relayRun.club.repository.ClubRepository;
import com.example.relayRun.club.dto.ClubDTO;
import com.example.relayRun.util.BaseException;
import com.example.relayRun.util.BaseResponseStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClubService {
    private ClubRepository clubRepository;

    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public List<ClubDTO.ClubList> getClubs() throws BaseException {
        try {
            List<ClubEntity> clubs = clubRepository.findAll();
            return convertToClubList(clubs);
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<ClubDTO.ClubList> getClubsByName(String search) throws BaseException {
        try {
            List<ClubEntity> clubs = clubRepository.findByNameContaining(search);
            return convertToClubList(clubs);
        } catch (Exception e) {
            System.out.println(e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<ClubDTO.ClubList> convertToClubList(List<ClubEntity> clubs) {
        List<ClubDTO.ClubList> clubList = new ArrayList<>();

        for (ClubEntity c : clubs) {
            ClubDTO.ClubList club = new ClubDTO.ClubList();
            club.setClubIdx(c.getClubIdx());
            club.setContent(c.getContent());
            club.setName(c.getName());
            club.setImgURL(c.getImgURL());
            club.setRecruitStatus(c.getRecruitStatus());
            clubList.add(club);
        }
        return clubList;
    }
}
