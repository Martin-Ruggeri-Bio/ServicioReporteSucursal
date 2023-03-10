package ar.edu.um.programacion2.simple.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.http.HttpHeaders;

import ar.edu.um.programacion2.simple.dtos.Sales;
import lombok.extern.slf4j.Slf4j;
import ar.edu.um.programacion2.simple.dtos.DateRange;
import ar.edu.um.programacion2.simple.dtos.PeticionReporteRecurrente;
import ar.edu.um.programacion2.simple.dtos.RespuestaReporte;
import ar.edu.um.programacion2.simple.dtos.ReporteRecibido;

import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReporteRecurrenteService implements Runnable {
    private String id_tocken_reporte;
    private String id_tocken_sucursal;

    private PeticionReporteRecurrente reporte;

    public ReporteRecurrenteService(PeticionReporteRecurrente reporte, String id_tocken_reporte, String id_tocken_sucursal) {
        this.reporte = reporte;
        this.id_tocken_reporte = id_tocken_reporte;
        this.id_tocken_sucursal = id_tocken_sucursal;
    }

    @Override
    @Transactional
    public void run() {
        LocalDateTime fechaActual = LocalDateTime.now();

        // Ejecutar el servicio mientras la fecha actual sea menor o igual a la fecha de finalización
        while (fechaActual.isBefore(reporte.getFechaFin()) || fechaActual.isEqual(reporte.getFechaFin())) {
            // Ejecutar la lógica del servicio
            log.info("Ejecutando servicio de reporte recurrente de tipo " + reporte.getTipo() + "...");
            log.info("Listar ventas para hacer un reporte");
            log.info("Token:" + this.id_tocken_reporte);
            DateRange dateRange = new DateRange(reporte.getFechaInicio() ,reporte.getFechaFin());
            WebClient webClientFranquicia = WebClient
                .builder()
                .baseUrl("http://localhost:8085/saleDetail/date-between")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.id_tocken_reporte)
                .build();

            // Realiza la llamada POST a la API del servicio de reporte y almacena el resultado en un Mono de tipo Message
            Mono<Sales> sales = webClientFranquicia
                .post()
                .body(Mono.just(dateRange), DateRange.class)
                .retrieve()
                .bodyToMono(Sales.class);
            log.info("Ventas de reporte listadas");

            log.info("Creando Reporte ");
            RespuestaReporte respuestaReporte = new RespuestaReporte("respuesta_reporte", sales.block().getDetail());
            log.info("Reporte Creado");
            log.info("Token:" + this.id_tocken_sucursal);
            WebClient webClientPrincipal = WebClient
                .builder()
                .baseUrl("http://10.101.102.1:8080/api/reporte/datos")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.id_tocken_sucursal)
                .build();

            // Realiza la llamada POST a la API del servicio de reporte y almacena el resultado en un Mono de tipo Message
            Mono<ReporteRecibido> reporteRecibido = webClientPrincipal
                .post()
                .body(BodyInserters.fromValue(respuestaReporte))
                .retrieve()
                .bodyToMono(ReporteRecibido.class);
            
            log.info("Reporte Recurrente Enviado, Respuesta del servidor principal es Status Code " + reporteRecibido.block().getEstado());

            // Esperar el intervalo de tiempo especificado antes de ejecutar el servicio de nuevo
            try {
                Thread.sleep(reporte.getIntervalo().toMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Actualizar la fecha actual antes de la próxima iteración
            fechaActual = LocalDateTime.now();
        }
    }
}
