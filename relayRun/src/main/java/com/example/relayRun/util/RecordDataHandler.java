package com.example.relayRun.util;

import com.example.relayRun.record.dto.GetDailyRes;
import com.example.relayRun.record.dto.locationDTO;
import com.example.relayRun.record.entity.LocationEntity;
import com.example.relayRun.record.entity.RunningRecordEntity;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    public static Float toSecond(LocalTime time) {
        return time.getHour() * 3600 +
                time.getMinute() * 60 +
                time.getSecond() +
                time.getNano() / 1000f;
    }
    public static Point toPoint(Float longitude, Float latitude) throws ParseException {
        String pointWKT = String.format("POINT(%s %s)", longitude, latitude);
        Point point = (Point) new WKTReader().read(pointWKT);
        return point;
    }

    public static List<LocationEntity> toEntityList(List<locationDTO> locations) throws ParseException {
        ArrayList<LocationEntity> entities = new ArrayList<>();
        for (locationDTO location : locations) {
            LocationEntity entity = LocationEntity.builder()
                    .time(location.getTime())
                    .position(toPoint(location.getLongitude(), location.getLatitude()))
                    .status(location.getStatus())
                    .build();
            entities.add(entity);
        }
        return entities;
    }

    public static int toIntDay(DayOfWeek dayOfWeek) {
        switch(dayOfWeek) {
            case MONDAY: return 1;
            case TUESDAY: return 2;
            case WEDNESDAY: return 3;
            case THURSDAY: return 4;
            case FRIDAY: return 5;
            case SATURDAY: return 6;
            case SUNDAY: return 7;
        }
        return 0;
    }

    public static String isSuccess(GoalType type, Float goal, Float time, Float pace, Float distance) {
        switch(type) {
            case NOGOAL: return "y";
            case PACE:
                return pace >= goal ? "y":"n";
            case TIME:
                return time >= goal ? "y":"n";
            case DISTANCE:
                return distance >= goal ? "y":"n";
        }
        return "y";
    }
}
