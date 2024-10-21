package org.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "places")
@Getter
@Setter
public class PlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug")
    @JsonProperty("slug")
    private String slug;

    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @OneToMany(mappedBy = "place_id", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EventEntity> events;
}
