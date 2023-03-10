package ar.edu.um.programacion2.simple.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeticionReporteHistorico {
	private Long id;
	private String tipo;
	private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
