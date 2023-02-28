package ar.edu.um.programacion2.simple.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import ar.edu.um.programacion2.simple.dtos.Sales;
import lombok.extern.slf4j.Slf4j;
import ar.edu.um.programacion2.simple.dtos.DateRange;
import ar.edu.um.programacion2.simple.dtos.RespuestaReporte;
import ar.edu.um.programacion2.simple.dtos.ReporteRecibido;

import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReporteHistoricoService {

	@Value("${servicioFranquicia.token_id}")
    private String id_tocken_reporte;

    // @Value("${logginSucursal.token_id}")
    // private String id_tocken_sucursal;

	public Sales listar_ventas_para_reporte(DateRange dateRange)  {
        log.debug("Listar ventas para hacer un reporte");
        
        WebClient webClient = WebClient
            .builder()
            .baseUrl("http://localhost:8085/saleDetail/date-between")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.id_tocken_reporte)
            .build();

        // Realiza la llamada POST a la API del servicio de reporte y almacena el resultado en un Mono de tipo Message
        Mono<Sales> sales = webClient
            .post()
            .body(Mono.just(dateRange), DateRange.class)
            .retrieve()
            .bodyToMono(Sales.class);
        
		return sales.block();
    }

	// public Mono<Sales> listar_ventas_para_reporte(DateRange dateRange)  {
    //     WebClient webClient = WebClient
    //         .builder()
    //         .baseUrl("http://localhost:8085/sale/date-between")
    //         .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    //         .build();

    //     // Realiza la llamada POST a la API del servicio de reporte y almacena el resultado en un Mono de tipo Message
    //     return webClient
    //         .post()
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .accept(MediaType.APPLICATION_JSON)
    //         .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.id_tocken_reporte + "\"")
    //         .body(BodyInserters.fromValue(dateRange))
    //         .retrieve()
    //         .bodyToMono(Sales.class);
    // }

	// @Transactional
    // public void enviar_reporte_historico(Sales sales)  {
	// 	RespuestaReporte respuestaReporte = new RespuestaReporte("respuesta_reporte", sales);
    //     log.debug("Respuesta de reporte creada");
    //     System.out.println(this.id_tocken_reporte);
    //     WebClient webClient = WebClient
    //         .builder()
    //         .baseUrl("http://10.101.102.1:8080/api/reporte/datos")
    //         .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    //         .build();

    //     // Realiza la llamada POST a la API del servicio de reporte y almacena el resultado en un Mono de tipo Message
    //     Mono<ReporteRecibido> reporteRecibido = webClient
    //         .post()
    //         .contentType(MediaType.APPLICATION_JSON)
    //         .accept(MediaType.APPLICATION_JSON)
    //         .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.id_tocken_sucursal + "\"")
    //         .body(BodyInserters.fromValue(respuestaReporte))
    //         .retrieve()
    //         .bodyToMono(ReporteRecibido.class);
        
    //     log.debug("Respuesta de reporte enviada");

    // }
}
