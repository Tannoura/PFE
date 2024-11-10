package com.example.demo.service;

import com.example.demo.entité.Organisme;
import com.example.demo.entité.salarié;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.moduleRepository;
import com.example.demo.repository.organismeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.demo.entité.Module;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ModuleService {
    @Autowired
    private moduleRepository moduleRepo;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private organismeRepository OrganismeRepository;

    @PersistenceContext
    private EntityManager entityManager; // Injecter l'EntityManager

    public Set<Module> getModulesByOrganismeId(Long organismeId) {
        Organisme organisme = OrganismeRepository.findById(organismeId)
                .orElseThrow(() -> new RuntimeException("Organisme not found"));
        return organisme.getModules();
    }
    public List<Module> getAllModules() {
        return moduleRepo.findAll();
    }

    public Optional<Module> getModuleById(Long id) {
        return moduleRepo.findById(id);
    }

    public Module createModule(Module module) {
        if (moduleRepo.existsByMatiere(module.getMatiere())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matière already exists");
        }

        return moduleRepo.save(module);
    }

    @Transactional
    public void deleteModule(Long id) {
        Module module = moduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        // Supprimer les relations avec les organismes
        for (Organisme organisme : module.getOrganismes()) {
            organisme.getModules().remove(module);
            entityManager.merge(organisme); // Mettre à jour l'entité organisme
        }
        // Supprimer le module lui-même
        moduleRepo.delete(module);
    }



    public Module updateModule(Long id, Module ModuleDetails) {
        return moduleRepo.findById(id)
                .map(module -> {
                    module.setMatiere(ModuleDetails.getMatiere());
                    module.setDuree(ModuleDetails.getDuree());
                    module.setPrix(ModuleDetails.getPrix());

                    return moduleRepo.save(module);
                })
                .orElseGet(() -> {
                    ModuleDetails.setId(id);
                    return moduleRepo.save(ModuleDetails);
                });
    }


    public List<Module> getModulesByMatière(String matière) {
        return moduleRepo.findByMatiere(matière);
    }

    public List<Module> getModulesBySalarié(Long salarieId) {
        salarié salarie = (salarié) userRepository.findById(salarieId)
                .orElseThrow(() -> new RuntimeException("Salarié not found"));
        String matière = salarie.getPoste().getSpecialite();
        return moduleRepo.findByMatiere(matière);
    }

    public Module findById(Long id) {
        Optional<Module> module = moduleRepo.findById(id);
        return module.orElse(null);
    }

    public Set<Organisme> getOrganismesByModuleId(Long moduleId) {
        Optional<Module> module = moduleRepo.findByIdWithOrganismes(moduleId);
        return module.map(Module::getOrganismes).orElse(null);
    }

    public long getNombreModulesSansOrganismes() {
        return moduleRepo.countModulesSansOrganismes();
    }

    public String getMatiereById(Long id) {
        Module module = moduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Module non trouvé"));
        return module.getMatiere();
    }
}
