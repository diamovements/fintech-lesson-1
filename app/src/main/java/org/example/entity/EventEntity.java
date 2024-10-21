package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "events")
@Getter
@Setter
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "start_date")
    private Date fromDate;

    @Column(name = "end_date")
    private Date toDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", referencedColumnName = "id")
    private PlaceEntity place_id;
}
