package com.sistemarm.sistemarm.service;

import java.util.List;
import java.util.Optional;
import com.sistemarm.sistemarm.model.entity.Paciente;
import com.sistemarm.sistemarm.model.enums.StatusPaciente;

public interface PacienteService {
	
	Paciente salvar(Paciente paciente);
	
	Paciente atualizar(Paciente paciente);
	
	void deletar(Paciente paciente);
	
	List<Paciente> buscar(Paciente pacienteFiltro);
	
	void atualizarStatus(Paciente paciente, StatusPaciente status);
	
	void valida(Paciente paciente);
	
	Optional<Paciente> obterPorId(Long id);

}