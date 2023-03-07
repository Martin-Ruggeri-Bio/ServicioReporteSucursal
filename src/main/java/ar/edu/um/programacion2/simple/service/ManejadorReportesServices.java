package ar.edu.um.programacion2.simple.service;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import ar.edu.um.programacion2.simple.dtos.DateRange;
import ar.edu.um.programacion2.simple.dtos.ReporteRecibido;
import ar.edu.um.programacion2.simple.dtos.RespuestaReporte;
import ar.edu.um.programacion2.simple.dtos.Sales;
import ar.edu.um.programacion2.simple.model.ReportesRecurrentes;
import ar.edu.um.programacion2.simple.repository.ReportesRecurrentesRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import java.time.Duration;


@Service
@Slf4j
public class ManejadorReportesServices {
    @Value("${servicioFranquicia.token_id}")
    private String id_tocken_reporte;

    @Value("${logginSucursal.id_tocken}")
    private String id_tocken_sucursal;

    @Autowired
    private ReportesRecurrentesRepository repository;

    @Autowired
    private ReportesRecurrentesService service;

    @Scheduled(cron = "${cron.expression}")
    public void check(){
        try {
            LocalDateTime fechaActual = LocalDateTime.now();
            ReportesRecurrentes reporte = service.buscarPrimerReporteListoParaRealizar(fechaActual);
            if (reporte != null) {
                log.info("Ejecutando servicio de reporte recurrente de tipo " + reporte.getTipo() + "...");
                log.info("Listar ventas para hacer un reporte");
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

                log.info("Obtener la fecha de inicio y el intervalo del informe recurrente existente.");
                Duration intervalo = reporte.getIntervalo();
                
                log.info("Calcular la nueva fecha de realización sumando el intervalo a la fecha de inicio.");
                LocalDateTime nuevaFechaRealizacion = fechaActual.plus(intervalo);
                
                log.info("Establecer la nueva fecha de realización en el informe recurrente.");
                reporte.setFechaRealizacion(nuevaFechaRealizacion);
                reporte.setFechaInicio(fechaActual);
                
                // Guardar los cambios en la base de datos.
                repository.save(reporte);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
