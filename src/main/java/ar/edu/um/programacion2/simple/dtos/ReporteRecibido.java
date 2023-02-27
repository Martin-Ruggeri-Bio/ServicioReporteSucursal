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
public class ReporteRecibido {
    private String accion;
    private String estado;
}
