package ar.edu.um.programacion2.simple.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaReporte {
    private String accion;
    private Sales datos;
}
