package ar.edu.um.programacion2.simple.controller;

import ar.edu.um.programacion2.simple.dtos.Message;
import ar.edu.um.programacion2.simple.dtos.DateRange;
import ar.edu.um.programacion2.simple.dtos.PeticionReporteHistorico;
import ar.edu.um.programacion2.simple.service.ReporteHistoricoService;
import ar.edu.um.programacion2.simple.dtos.Sales;
import ar.edu.um.programacion2.simple.dtos.ReporteRecibido;
import ar.edu.um.programacion2.simple.dtos.RespuestaReporte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.task.TaskExecutor;

import javax.validation.Valid;



@RestController
@RequestMapping("/Reporte")
public class ReporteController {

	@Autowired
	private ReporteHistoricoService reporteHistoricoService;

    @Autowired
    private TaskExecutor taskExecutor;

	@PostMapping("/Historico")
    public ResponseEntity<Message> addReporte(@Valid @RequestBody PeticionReporteHistorico peticionReporteHistorico, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return new ResponseEntity<>(new Message("Revise los campos"),HttpStatus.BAD_REQUEST);
        DateRange dateRange = new DateRange(peticionReporteHistorico.getFechaInicio() ,peticionReporteHistorico.getFechaFin());
        Sales sales = this.reporteHistoricoService.listar_ventas_para_reporte(dateRange);
        ReporteRecibido reporteRecibido = this.reporteHistoricoService.enviar_reporte_historico(sales);
        Message message = new Message(reporteRecibido.getAccion());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // ReporteControler 
    @PostMapping("/historicoPrueba")
    public ResponseEntity<Sales> historicoPrueba(@Valid @RequestBody PeticionReporteHistorico peticionReporteHistorico, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        DateRange dateRange = new DateRange(peticionReporteHistorico.getFechaInicio() ,peticionReporteHistorico.getFechaFin());
        Sales sales = this.reporteHistoricoService.listar_ventas_para_reporte(dateRange);
        taskExecutor.execute(() -> {
            this.reporteHistoricoService.enviar_reporte_historico(sales);
        });
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }
    

    // @PostMapping("/historicoPrueba")
    // public Mono<ResponseEntity<Message>> historicoPrueba(@Valid @RequestBody PeticionReporteHistorico peticionReporteHistorico, BindingResult bindingResult){
    //     if (bindingResult.hasErrors())
    //         return Mono.just(new ResponseEntity<>(new Message("Revise los campos"),HttpStatus.BAD_REQUEST));
    //     DateRange dateRange = new DateRange(peticionReporteHistorico.getFechaInicio() ,peticionReporteHistorico.getFechaFin());
    //     Mono<Sales> sales = this.reporteHistoricoService.listar_ventas_para_reporte(dateRange);
    //     return sales.map(s -> new ResponseEntity<>(new Message("ventas encontradas"), HttpStatus.OK));
    // }
}
