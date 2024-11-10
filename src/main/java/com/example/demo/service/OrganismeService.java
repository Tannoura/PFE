package com.example.demo.service;

import com.example.demo.entité.Organisme;
import com.example.demo.entité.Module;
import com.example.demo.repository.moduleRepository;
import com.example.demo.repository.organismeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrganismeService {
    @Autowired
    private organismeRepository organismerepository;

    @Autowired
    private moduleRepository moduleRepo;


    public void deleteOrganisme(long idO) {
        organismerepository.deleteById(idO);
    }
    public Organisme saveOrganisme(Organisme organisme) {
        return organismerepository.save(organisme);
    }

    public List<Organisme> getAllOrganismes() {
        return organismerepository.findAll();
    }

    private boolean isValidAddress(String address) {
        // Vérifier si l'adresse est valide
        return address.matches("^\\d+\\s[A-z]+\\s[A-z]++\\s[A-z]+");
    }
    @Transactional
    public Organisme addOrganismeWithModule(Organisme organisme, Long moduleId) {
        Optional<Module> module = moduleRepo.findById(moduleId);
        if (module.isPresent()) {
            if (organisme.getModules() == null) {
                organisme.setModules(new HashSet<>());
            }
            if (organismerepository.existsByNomOrganisme(organisme.getNomOrganisme())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organisme already exists");
            }

            organisme.getModules().add(module.get());
            return organismerepository.save(organisme);
        } else {
            throw new RuntimeException("Module not found with id: " + moduleId);
        }
    }

    public Organisme findById(Long id) {
        Optional<Organisme> organisme = organismerepository.findById(id);
        return organisme.orElse(null);
    }

    public void save(Organisme organisme) {
        organismerepository.save(organisme);
    }

    public List<Organisme> getOrganismesByModule(Module module) {
        // Implémentation pour récupérer les organismes liés à un module
        return organismerepository.findByModules(module);
    }
}
