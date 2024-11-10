package com.example.demo.repository;

import com.example.demo.entité.Poste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface posteRepository extends JpaRepository<Poste, Long> {
}
