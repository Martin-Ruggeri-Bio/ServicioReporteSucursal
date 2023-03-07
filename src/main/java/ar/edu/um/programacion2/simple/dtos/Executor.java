package ar.edu.um.programacion2.simple.dtos;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.Callable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Executor implements Callable<Boolean> {
    private Long id;
	private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
	private Duration intervalo;
    private volatile boolean cancelled;
    private String id_tocken_reporte;
    private String id_tocken_sucursal;

    @Override
    public Boolean call() throws Exception {
        System.out.println("Arranca el hilo "+this.id +" Como executor");
        LocalDateTime fechaActual = LocalDateTime.now();
        Integer i = 0;
        // reviso si la fecha de inicio es futura
        if (fechaActual.isBefore(fechaInicio)){
            Duration intervalo2 = Duration.between(fechaInicio, fechaFin);
            Thread.sleep(intervalo2.toMillis());
        }
        while (fechaInicio.isBefore(fechaFin) || fechaInicio.isEqual(fechaFin)) {
            if (cancelled) {
                System.out.println("Se cancela el hilo "+this.id);
                return false;
            }
            fechaActual = LocalDateTime.now();
            System.out.println(String.format("Iteracion nro: %d del reporte nro: [%s]", i, this.id));
            log.info("Listar ventas para hacer un reporte");
            DateRange dateRange = new DateRange(fechaInicio, fechaFin);
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

            fechaInicio = fechaActual.plus(intervalo);
            i = i + 1;
            try {
                Thread.sleep(this.intervalo.toMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Se termina el hilo "+this.id);
        return true;
    }

    public void cancel() {
        cancelled = true;
    }
}
