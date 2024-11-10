package com.example.demo.repository;

import com.example.demo.entité.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository  extends JpaRepository<Image,Integer> {
    List<Image> findByOrderById();
}
