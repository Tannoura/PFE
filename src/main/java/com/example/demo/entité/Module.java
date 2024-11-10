package com.example.demo.entité;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "Module", uniqueConstraints = {
        @UniqueConstraint(columnNames = "matiere")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private String matiere;
    private long duree;
    private long prix;

    //relation organisme avec module
    @JsonIgnore
    @ManyToMany(mappedBy = "modules")

    Set<Organisme> organismes;
}
