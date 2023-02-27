package ar.edu.um.programacion2.simple.dtos;

import java.time.LocalDateTime;

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
public class Sale {
	private String id;
    private Double total;
    private LocalDateTime date;
    private User client;
}
