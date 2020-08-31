package com.sistemarm.sistemarm.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemarm.sistemarm.model.entity.Paciente;


public interface PacienteRepository extends JpaRepository<Paciente, Long>{

}
