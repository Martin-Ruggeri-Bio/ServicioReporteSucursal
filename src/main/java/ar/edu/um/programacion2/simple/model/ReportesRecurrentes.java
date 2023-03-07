package ar.edu.um.programacion2.simple.model;

import java.time.LocalDateTime;
import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportesRecurrentes {
	@Id
	private Long id;
	private String tipo;
	private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
	private Duration intervalo;
	private LocalDateTime fechaRealizacion;
}
