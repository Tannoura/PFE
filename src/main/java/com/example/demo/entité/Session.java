package com.example.demo.entité;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity(name = "Session")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private LocalDate datedebut;
    private LocalDate datefin;

    @Enumerated(EnumType.STRING)
    private StatutSession statutSession;
    private long cout;
    private String salle;
    private long capacite;
    @Enumerated(EnumType.STRING)
    private PlanningType planningType;


    @JsonIgnore
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanningEntry> planningEntries = new ArrayList<>();

    @ManyToOne
    private Module module;

    @ManyToOne
    private Organisme organisme;

    @JsonIgnore
    @OneToMany(mappedBy = "session")
    private Set<Membre> membres;


    public List<LocalDate> getExactPlanningDates(List<PlanningEntry> planningEntries) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate startDate = this.datedebut;
        LocalDate endDate = this.datefin;

        if (planningEntries == null || planningEntries.isEmpty()) {
            return dates; // Retourner une liste vide si aucune entrée de planning
        }

        for (PlanningEntry planningEntry : planningEntries) {
            switch (this.planningType) {
                case EVERYDAY:
                    dates.addAll(startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList()));
                    break;

                case EVERYWEEK :
                    LocalDate firstOccurrence = startDate.with(planningEntry.getJour());
                    if (firstOccurrence.isBefore(startDate)) {
                        firstOccurrence = firstOccurrence.plusWeeks(1);
                    }

                    while (!firstOccurrence.isAfter(endDate)) {
                        dates.add(firstOccurrence);
                        firstOccurrence = firstOccurrence.plusWeeks(1);
                    }
                    break;

                case MOINS7:
                    LocalDate first = startDate.with(planningEntry.getJour());
                    if (first.isBefore(startDate)) {
                        first = first.plusWeeks(1);
                    }

                    while (!first.isAfter(endDate)) {
                        dates.add(first);
                        first = first.plusWeeks(1);
                    }
                    break;
            }
        }

        // Éliminer les doublons et trier les dates
        return dates.stream().distinct().sorted().collect(Collectors.toList());
    }


}
