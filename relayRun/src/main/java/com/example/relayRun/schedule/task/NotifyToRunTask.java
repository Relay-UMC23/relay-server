package com.example.relayRun.schedule.task;

import com.example.relayRun.club.entity.TimeTableEntity;
import com.example.relayRun.event.NotifyEventPublisher;
import com.example.relayRun.event.TimeToRunEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotifyToRunTask implements Runnable{
    TimeTableEntity timeTable;
    private NotifyEventPublisher publisher;

    public NotifyToRunTask(TimeTableEntity timeTable, NotifyEventPublisher publisher) {
        this.timeTable = timeTable;
        this.publisher = publisher;
    }
    @Override
    public void run() {
        if (timeTable == null){
            log.error("time table null");
            return ;
        }
        if (publisher == null) {
            log.error("publisher null");
            return ;
        }
        log.info("publish time to run event");
        log.info("public {} {}", timeTable.getMemberStatusIdx().getMemberStatusIdx(), timeTable.getStart().toString());
        publisher.publishNotifyEvent(TimeToRunEvent.builder()
                .memberStatusIdx(timeTable.getMemberStatusIdx().getMemberStatusIdx())
                .start(timeTable.getStart())
                .build()
        );
    }
}
