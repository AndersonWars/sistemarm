package com.sistemarm.sistemarm.api.resource;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sistemarm.sistemarm.model.entity.Usuario;
import com.sistemarm.sistemarm.model.enums.StatusPaciente;
import com.sistemarm.sistemarm.api.dto.AtualizaStatusDTO;
import com.sistemarm.sistemarm.api.dto.PacienteDTO;
import com.sistemarm.sistemarm.exception.RegraNegocioException;
import com.sistemarm.sistemarm.model.entity.Paciente;
import com.sistemarm.sistemarm.service.PacienteService;
import com.sistemarm.sistemarm.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteResource {
	
	private final PacienteService service;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity buscar(
		@RequestParam(value = "nome", required = false) String nome,
		@RequestParam("usuario") Long idUsario
		){
			Paciente pacienteFiltro = new Paciente();
			pacienteFiltro.setNome(nome);
			
			Optional<Usuario> usuario =  usuarioService.obterPorId(idUsario);
			if(!usuario.isPresent()) {
				return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado.");
			} else {
				pacienteFiltro.setUsuario(usuario.get());
			}
			List<Paciente> pacientes =  service.buscar(pacienteFiltro);
			return ResponseEntity.ok(pacientes);
	}
	
	@GetMapping("{id}")
	public ResponseEntity obterPaciente(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(paciente -> new ResponseEntity(converter(paciente), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody PacienteDTO dto) {
		try {
			Paciente entidade = converter(dto);
			service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar (@PathVariable("id") Long id, @RequestBody PacienteDTO dto) {
		return service.obterPorId(id).map(entity -> {
			try {
			   Paciente paciente = converter(dto);
			   paciente.setId(entity.getId());
			   service.atualizar(paciente);
			   return ResponseEntity.ok(paciente);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Paciente não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		return service.obterPorId(id).map( entity -> {
			StatusPaciente statusSelecionado = StatusPaciente.valueOf(dto.getStatus());
			if (statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possivel atualizar o status do paciente, envie um status válido.");
			}
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Paciente não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(entidade -> {
			service.deletar(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() ->
			new ResponseEntity("Paciente não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
	}

	private PacienteDTO converter(Paciente paciente) {
		return PacienteDTO.builder().id(paciente.getId())
									  .nome(paciente.getNome())
									  .status(paciente.getStatus().name())
									  .usuario(paciente.getUsuario().getId())
									  .build();
	}	
	
	private Paciente converter(PacienteDTO dto) {
		Paciente paciente = new Paciente();
		paciente.setId(dto.getId());
		paciente.setNome(dto.getNome());
		Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado"));
		paciente.setUsuario(usuario);
		if(dto.getStatus() != null) {
			paciente.setStatus(StatusPaciente.valueOf(dto.getStatus()));
		}
		return paciente;
	}
	
}