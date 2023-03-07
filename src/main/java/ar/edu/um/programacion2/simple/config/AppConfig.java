package ar.edu.um.programacion2.simple.config;

import ar.edu.um.programacion2.simple.dtos.PeticionReporteRecurrente;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {
  @Bean
  public PeticionReporteRecurrente peticionReporteRecurrente() {
    return new PeticionReporteRecurrente();
  }
  @Bean
  public String id_tocken_reporte() {
    return new String();
  }
  @Bean
  public String id_tocken_sucursal() {
    return new String();
  }
}
