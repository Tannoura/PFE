package com.example.demo.entité;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name ="Organisme")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Organisme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private String nomOrganisme;
    private String adresseOrganisme;
    private long numeroOrganisme;

    @ManyToMany
    @JoinTable(name = "organisme_module", joinColumns = @JoinColumn(name = "organisme_id")

            ,inverseJoinColumns = @JoinColumn(name = "module_id"))
    @JsonIgnore
    Set<Module> modules = new HashSet<>();

    @OneToOne
    private Image image;
}
