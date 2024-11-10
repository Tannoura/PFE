package com.example.demo.service;

import com.example.demo.entité.Poste;
import com.example.demo.repository.posteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PosteService {

    @Autowired
    private posteRepository posteRepo;

    public Poste savePoste(Poste poste) {
        return posteRepo.save(poste);
    }

    public List<Poste> getAllPostes() {
        return posteRepo.findAll();
    }
}
