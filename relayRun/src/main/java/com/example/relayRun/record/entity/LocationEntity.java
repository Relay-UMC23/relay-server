package com.example.relayRun.record.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table(name = "Location")

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

    @Column(columnDefinition = "varchar(10) default 'active'")
    private String status;

}
