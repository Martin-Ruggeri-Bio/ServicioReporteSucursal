package ar.edu.um.programacion2.simple.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.http.HttpHeaders;

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

    @Value("${logginSucursal.id_tocken}")
    private String id_tocken_sucursal;

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

	@Transactional
    public ReporteRecibido enviar_reporte_historico(Sales sales)  {
		RespuestaReporte respuestaReporte = new RespuestaReporte("respuesta_reporte", sales.getDetail());
        log.debug("Respuesta de reporte creada");
        WebClient webClient = WebClient
            .builder()
            .baseUrl("http://10.101.102.1:8080/api/reporte/datos")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.id_tocken_sucursal)
            .build();

        // Realiza la llamada POST a la API del servicio de reporte y almacena el resultado en un Mono de tipo Message
        Mono<ReporteRecibido> reporteRecibido = webClient
            .post()
            .body(BodyInserters.fromValue(respuestaReporte))
            .retrieve()
            .bodyToMono(ReporteRecibido.class);
        
        log.debug("Respuesta de reporte enviada");
        return reporteRecibido.block();
    }


    @Transactional
    public RespuestaReporte crearRespuestaReporte(Sales sales)  {
		RespuestaReporte respuestaReporte = new RespuestaReporte("respuesta_reporte", sales.getDetail());
        return respuestaReporte;
    }
}
