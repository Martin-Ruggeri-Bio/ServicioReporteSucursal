package ar.edu.um.programacion2.simple.dtos;

import java.util.List;

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
public class DatoReportadoEstadoDTO {
    private String accion;
    private String estado;
    private String errorMotivo;
    private List<Sale> erroneos;
}
