package com.example.relayRun.util;

import com.example.relayRun.record.dto.GetDailyRes;
import com.example.relayRun.record.entity.RunningRecordEntity;

import java.time.LocalDate;
import java.util.List;

public class RecordDataHandler {
    public static GetDailyRes get_summary(List<RunningRecordEntity> list, LocalDate date) {
        float totalTime = 0;
        float totalDist = 0;
        float totalPace = 0;
        Long count = 0L;

        for (RunningRecordEntity rec : list) {
            totalTime += rec.getTime();
            totalDist += rec.getDistance();
            totalPace += rec.getPace();
            count++;
        }

        return GetDailyRes.builder()
                .date(date)
                .totalTime(totalTime)
                .totalDist(totalDist)
                .avgPace(totalPace/count)
                .build();
    }
}
