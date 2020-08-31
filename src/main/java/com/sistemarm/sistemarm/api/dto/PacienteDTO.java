package com.sistemarm.sistemarm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {
	
	private Long id;
	private String nome;
	private Long usuario;
	private String tipo;
	private String status;

}
