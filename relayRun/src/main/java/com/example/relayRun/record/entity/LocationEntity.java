package com.example.relayRun.record.entity;

import lombok.*;

import java.time.LocalDateTime;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table(name = "location")

public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationIdx;

    @ManyToOne
    @JoinColumn(name = "recordIdx")
    private RunningRecordEntity recordIdx;

    @Column(nullable = false)
    private LocalDateTime time;
    
    @Column(nullable = false)
    private Point position;

    @Column(columnDefinition = "varchar(10) default 'running'")
    private String status;

    public void setRecordIdx(RunningRecordEntity record) {
        this.recordIdx = record;
    }
}
