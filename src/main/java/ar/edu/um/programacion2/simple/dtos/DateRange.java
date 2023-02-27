package ar.edu.um.programacion2.simple.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateRange {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
