package com.sistemarm.sistemarm.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.sistemarm.sistemarm.model.entity.Paciente;
import com.sistemarm.sistemarm.model.entity.Usuario;
import com.sistemarm.sistemarm.model.enums.StatusPaciente;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("teste")
public class PacienteRepositoryTest {
	
	@Autowired
	PacienteRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	
	@Test
	public void deveSalvarUmPaciente() {
		Paciente paciente = criarPaciente();
		paciente = repository.save(paciente);
		assertThat(paciente.getId()).isNotNull();
	}
	
	public static Paciente criarPaciente() {
		Usuario usuario = Usuario.builder().id(6l).build();
		return Paciente.builder()
				.nome("nomeTeste")
				.status(StatusPaciente.ATIVO)
				.dataCadastro(LocalDate.now())
				.usuario(usuario)
				.build();
	}
	
	@Test
	public void deveDeletarUmPaciente() {
		Paciente paciente = criarEPersisteUmPaciente();
		paciente = entityManager.find(Paciente.class, paciente.getId());
		repository.delete(paciente);
		Paciente pacienteInexisente = entityManager.find(Paciente.class, paciente.getId());
		assertThat(pacienteInexisente).isNull();
	}
	
	private Paciente criarEPersisteUmPaciente() {
		Paciente paciente = criarPaciente();
		paciente = entityManager.persist(paciente);
		return paciente;
	}
	
	@Test
	public void deveAtualizarUmPaciente() {
		Paciente paciente = criarEPersisteUmPaciente();
		paciente.setNome("Teste Atualizar");
		paciente.setStatus(StatusPaciente.INATIVO);
		repository.save(paciente);
		Paciente pacienteAtualizado = entityManager.find(Paciente.class, paciente.getId());
		assertThat(pacienteAtualizado.getNome()).isEqualTo("Teste Atualizar");
		assertThat(pacienteAtualizado.getStatus()).isEqualTo(StatusPaciente.INATIVO);	
	}
	
	@Test
	public void deveBuscarUmPacientePorId() {
		Paciente paciente = criarEPersisteUmPaciente();
		Optional<Paciente> pacienteEncontrado = repository.findById(paciente.getId());
		 assertThat(pacienteEncontrado.isPresent()).isTrue();
	}

}
