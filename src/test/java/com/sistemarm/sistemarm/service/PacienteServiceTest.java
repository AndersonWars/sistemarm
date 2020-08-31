package com.sistemarm.sistemarm.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.sistemarm.sistemarm.exception.RegraNegocioException;
import com.sistemarm.sistemarm.model.entity.Paciente;
import com.sistemarm.sistemarm.model.entity.Usuario;
import com.sistemarm.sistemarm.model.enums.StatusPaciente;
import com.sistemarm.sistemarm.model.repository.PacienteRepository;
import com.sistemarm.sistemarm.model.repository.PacienteRepositoryTest;
import com.sistemarm.sistemarm.service.impl.PacienteServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
public class PacienteServiceTest {
	
	@SpyBean
	PacienteServiceImpl service;
	
	@MockBean
	PacienteRepository repository;
	
	@Test
	public void deveSalvarUmLPaciente() {
		//cenario
		Paciente pacienteASalvar = PacienteRepositoryTest.criarPaciente();
		Mockito.doNothing().when(service).valida(pacienteASalvar);
		
		Paciente pacienteSalvo = PacienteRepositoryTest.criarPaciente();
		pacienteSalvo.setId(1l);
		pacienteSalvo.setStatus(StatusPaciente.ATIVO);
		Mockito.when(repository.save(pacienteASalvar)).thenReturn(pacienteSalvo);
		
		//execucao
		Paciente paciente = service.salvar(pacienteASalvar);
		
		//verificacao
		Assertions.assertThat(paciente.getId()).isEqualTo(pacienteSalvo.getId());
		Assertions.assertThat(paciente.getStatus()).isEqualTo(StatusPaciente.ATIVO);
	}
	
	@Test
	public void naoDeveSalvarUmPacienteQuandoHouverErroDeValidacao() {
		//cenario
		Paciente pacienteASalvar = PacienteRepositoryTest.criarPaciente();
		Mockito.doThrow(RegraNegocioException.class).when(service).valida(pacienteASalvar);
		
		//execucao e verificacao
		Assertions.catchThrowableOfType(() -> service.salvar(pacienteASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(pacienteASalvar);
	}
	
	@Test
	public void deveAtualizarUmPaciente() {
		//cenario
		Paciente pacienteSalvo = PacienteRepositoryTest.criarPaciente();
		pacienteSalvo.setId(1l);
		pacienteSalvo.setStatus(StatusPaciente.ATIVO);
		
		Mockito.doNothing().when(service).valida(pacienteSalvo);
		
		Mockito.when(repository.save(pacienteSalvo)).thenReturn(pacienteSalvo);
		
		//execucao
		service.atualizar(pacienteSalvo);
		
		//verificacao
		Mockito.verify(repository, Mockito.times(1)).save(pacienteSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmPacienteQueAindaNaoFoiSalvo() {
		//cenario
		Paciente pacienteASalvar = PacienteRepositoryTest.criarPaciente();
		
		//execucao e verificacao
		Assertions.catchThrowableOfType(() -> service.atualizar(pacienteASalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(pacienteASalvar);
	}
	
	@Test
	public void deveDeletarUmPaciente() {
		//cenario
		Paciente paciente = PacienteRepositoryTest.criarPaciente();
		paciente.setId(1l);
		
		//execucao
		service.deletar(paciente);
		
		//verificacao
		Mockito.verify(repository).delete(paciente);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmPacienteQueAindaNaoFoiSalvo() {
		//cenario
		Paciente paciente = PacienteRepositoryTest.criarPaciente();
		
		//execucao
		Assertions.catchThrowableOfType(() -> service.deletar(paciente), NullPointerException.class);
		
		//verificacao
		Mockito.verify(repository, Mockito.never()).delete(paciente);
	}
	
	@Test
	public void deveFiltrarPacientes() {
		//cenario
		Paciente paciente = PacienteRepositoryTest.criarPaciente();
		paciente.setId(1l);
		
		List<Paciente> lista = Arrays.asList(paciente);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//execucao
		List<Paciente> resultado =  service.buscar(paciente);
		
		//verificacoes
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(paciente);
	}

	@Test
	public void deveAtualizarOStatusDeUmPaciente() {
		//cenario
		Paciente paciente = PacienteRepositoryTest.criarPaciente();
		paciente.setId(1l);
		
		StatusPaciente novoStatus = StatusPaciente.ATIVO;
		Mockito.doReturn(paciente).when(service).atualizar(paciente);
		
		//execucao
		service.atualizarStatus(paciente, novoStatus);
		
		//verificacoes
		Assertions.assertThat(paciente.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(paciente);
	}
	
	@Test
	public void deveObterUmPacientePorID() {
		//cenario
		Long id = 1l;
		
		Paciente paciente = PacienteRepositoryTest.criarPaciente();
		paciente.setId(id);	
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(paciente));
		
		//execucao
		Optional<Paciente> resultado = service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoOPacienteNaoExiste() {
		//cenario
		Long id = 1l;
		
		Paciente paciente = PacienteRepositoryTest.criarPaciente();
		paciente.setId(id);	
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//execucao
		Optional<Paciente> resultado = service.obterPorId(id);
		
		//verificacao
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarUmPaciente() {
		Paciente paciente = new Paciente();
		
		Throwable erro = Assertions.catchThrowable(() -> service.valida(paciente));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um nome válido.");
		
		paciente.setNome("");
		
		erro = Assertions.catchThrowable(() -> service.valida(paciente));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um nome válido.");		
	
		paciente.setNome("UsuarioTeste");
		
		erro = Assertions.catchThrowable(() -> service.valida(paciente));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		paciente.setUsuario(new Usuario());
		
		erro = Assertions.catchThrowable(() -> service.valida(paciente));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		paciente.getUsuario().setId(1l);
		
	}
}
