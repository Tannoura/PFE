package com.example.demo.repository;

import com.example.demo.entité.Organisme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entité.Module;

import java.util.List;

@Repository
public interface organismeRepository extends JpaRepository<Organisme, Long> {
    boolean existsByNomOrganisme(String nomOrganisme);
    List<Organisme> findByModules(Module module);


}
