package com.example.demo.service;

import com.example.demo.entité.salarié;
import com.example.demo.repository.SalarieRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class userService {

    @Autowired
    private UserRepository userRepo;


    @Autowired
    private SalarieRepository salariéRepository;

    public long getSalariéCount() {
        return salariéRepository.count(); // Utilisation de la méthode count() fournie par JpaRepository
    }
    public List<salarié> getAllSalariés() {
        return salariéRepository.findAll();
    }
}
