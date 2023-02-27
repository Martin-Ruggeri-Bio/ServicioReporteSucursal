package ar.edu.um.programacion2.simple.model;
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
public class ReporteHistorico {
    @Id
	private Long id;
	private String tipo;
	private String fechaInicio;
    private String fechaFin;
}
