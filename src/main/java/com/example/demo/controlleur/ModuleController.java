package com.example.demo.controlleur;

import com.example.demo.entité.Module;
import com.example.demo.entité.Organisme;
import com.example.demo.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/modules")

public class ModuleController {
    @Autowired
    private ModuleService moduleService;

    @PostMapping
    public Module createModule(@RequestBody Module module) {
        return moduleService.createModule(module);
    }

    @GetMapping
    public List<Module> getAllModules() {
        return moduleService.getAllModules();
    }


    @GetMapping("/bySalarie/{salarieId}")
    public ResponseEntity<List<Module>> getModulesBySalarié(@PathVariable Long salarieId) {
        List<Module> modules = moduleService.getModulesBySalarié(salarieId);
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/organisme/{organismeId}")
    public Set<Module> getModulesByOrganismeId(@PathVariable Long organismeId) {
        return moduleService.getModulesByOrganismeId(organismeId);
    }

    @GetMapping("/{moduleId}/organismes")
    public ResponseEntity<Set<Organisme>> getOrganismesByModuleId(@PathVariable Long moduleId) {
        Set<Organisme> organismes = moduleService.getOrganismesByModuleId(moduleId);
        if (organismes == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(organismes);
        }
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> deleteModule(@PathVariable long moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sans-organismes")
    public ResponseEntity<Long> getNombreModulesSansOrganismes() {
        long nombreModules = moduleService.getNombreModulesSansOrganismes();
        return ResponseEntity.ok(nombreModules);
    }

    @GetMapping("/{id}/matiere")
    public String getMatiereById(@PathVariable Long id) {
        return moduleService.getMatiereById(id);
    }
}
