package com.example.demo.entité;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity(name = "PlanningEntry")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlanningEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "HH:mm")

    private LocalTime debut;

    @JsonFormat(pattern = "HH:mm")

    private LocalTime fin;

    private DayOfWeek jour;



    @JsonIgnore
    @ManyToOne
    private Session session;
}
