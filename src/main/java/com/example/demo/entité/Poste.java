package com.example.demo.entité;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "Poste")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Poste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private String specialite;
}
