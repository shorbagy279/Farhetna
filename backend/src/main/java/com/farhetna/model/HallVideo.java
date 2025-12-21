package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hall_videos")
@Data
@EqualsAndHashCode(callSuper = true)
public class HallVideo extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(nullable = false)
    private String videoUrl;

    private String thumbnailUrl;
    private Integer durationSeconds;
}
