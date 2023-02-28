package ar.edu.um.programacion2.simple.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
// @JsonIgnoreProperties(value = { "id" })
public class Sale {
    private String id;
    private LocalDateTime fecha;
    private String ventaId;
    private Integer menu;
    private Float precio;
}
