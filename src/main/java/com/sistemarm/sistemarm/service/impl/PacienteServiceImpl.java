package com.sistemarm.sistemarm.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemarm.sistemarm.exception.RegraNegocioException;
import com.sistemarm.sistemarm.model.entity.Paciente;
import com.sistemarm.sistemarm.model.enums.StatusPaciente;
import com.sistemarm.sistemarm.model.repository.PacienteRepository;
import com.sistemarm.sistemarm.service.PacienteService;

@Service
public class PacienteServiceImpl implements PacienteService{
	
	private PacienteRepository repository;
	
	public PacienteServiceImpl(PacienteRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Paciente salvar(Paciente paciente) {
		valida(paciente);
		paciente.setStatus(StatusPaciente.ATIVO);
		return repository.save(paciente);
	}

	@Override
	@Transactional
	public Paciente atualizar(Paciente paciente) {
		Objects.requireNonNull(paciente.getId());
		valida(paciente);
		return repository.save(paciente);
	}

	@Override
	@Transactional
	public void deletar(Paciente paciente) {
		Objects.requireNonNull(paciente.getId());
		repository.delete(paciente);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Paciente> buscar(Paciente pacienteFiltro) {
		Example example = Example.of(pacienteFiltro, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Paciente paciente, StatusPaciente status) {
		paciente.setStatus(status);
		atualizar(paciente);
	}

	@Override
	public void valida(Paciente paciente) {
		if (paciente.getNome() == null || paciente.getNome().trim().equals("")) {
			throw new RegraNegocioException("Informe um nome válido.");
		}
		if(paciente.getUsuario() == null || paciente.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um Usuário.");
		}
		
	}

	@Override
	public Optional<Paciente> obterPorId(Long id) {
		return repository.findById(id);
	}

}
