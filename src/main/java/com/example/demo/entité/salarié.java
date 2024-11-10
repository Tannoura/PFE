package com.example.demo.entité;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@DiscriminatorValue("SALARIE")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class salarié extends User{


    @ManyToOne
    @JoinColumn(name = "poste_id")
    private Poste poste;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Membre> membres;

    public Role getRole() {
        return Role.SALARIE;
    }



}
