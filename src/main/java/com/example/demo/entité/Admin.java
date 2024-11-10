package com.example.demo.entité;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
@Getter
@Setter
public class Admin extends User{
    public Role getRole() {
        return Role.ADMIN;
    }
}
