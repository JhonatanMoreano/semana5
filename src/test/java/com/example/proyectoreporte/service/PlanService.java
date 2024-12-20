/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.proyectoreporte.service;

import com.example.proyectoreporte.model.Plan;
import com.example.proyectoreporte.repository.PlanRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author PC
 */
public class PlanService {

    @Autowired
    private PlanRepository repository;

    public List<Plan> listarTodos() {
        return repository.findAll();
    }

    public void guardar(Plan plan) {
        repository.save(plan);
    }

    public Optional<Plan> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
