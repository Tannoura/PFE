package com.example.demo.controlleur;

import com.example.demo.entité.Poste;
import com.example.demo.service.PosteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/postes")

public class PosteController {
    @Autowired
    private PosteService posteService;

    @PostMapping
    public Poste createPoste(@RequestBody Poste poste) {
        return posteService.savePoste(poste);
    }

    @GetMapping
    public List<Poste> getAllPostes() {
        return posteService.getAllPostes();
    }
}
