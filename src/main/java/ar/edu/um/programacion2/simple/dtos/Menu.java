package ar.edu.um.programacion2.simple.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Martin
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
	private Integer id;
	private String nombre;
	private String descripcion;
	private Float precio;
	private String urlImagen;
	private Boolean activo;
	private String creado;
	private String actualizado;
}
