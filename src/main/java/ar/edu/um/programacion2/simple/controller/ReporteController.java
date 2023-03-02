package ar.edu.um.programacion2.simple.controller;

import ar.edu.um.programacion2.simple.dtos.DateRange;
import ar.edu.um.programacion2.simple.dtos.Message;
import ar.edu.um.programacion2.simple.dtos.PeticionReporteHistorico;
import ar.edu.um.programacion2.simple.dtos.ReporteRecibido;
import ar.edu.um.programacion2.simple.dtos.RespuestaReporte;
import ar.edu.um.programacion2.simple.service.ReporteHistoricoService;
import ar.edu.um.programacion2.simple.dtos.Sales;

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

    // // ReporteControler 
    // @PostMapping("/CrearHistorico")
    // public ResponseEntity<Sales> historicoPrueba(@Valid @RequestBody PeticionReporteHistorico peticionReporteHistorico, BindingResult bindingResult){
    //     if (bindingResult.hasErrors())
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    //     DateRange dateRange = new DateRange(peticionReporteHistorico.getFechaInicio() ,peticionReporteHistorico.getFechaFin());
    //     Sales sales = this.reporteHistoricoService.listar_ventas_para_reporte(dateRange);
    //     // taskExecutor.execute(() -> {
    //     //     this.reporteHistoricoService.enviar_reporte_historico(sales);
    //     // });
    //     return new ResponseEntity<>(sales, HttpStatus.OK);
    // }

    // ReporteControler 
    @PostMapping("/CrearHistorico")
    public ResponseEntity<Message> historicoPrueba(@Valid @RequestBody PeticionReporteHistorico peticionReporteHistorico, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        DateRange dateRange = new DateRange(peticionReporteHistorico.getFechaInicio() ,peticionReporteHistorico.getFechaFin());
        // Sales sales = this.reporteHistoricoService.listar_ventas_para_reporte(dateRange);
        taskExecutor.execute(() -> {
            this.reporteHistoricoService.hacerReporteHistorico(dateRange);
        });
        Message message = new Message("Creando Reporte");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
    

    @PostMapping("/EnviarHistorico")
    public ResponseEntity<ReporteRecibido> historicoPrueba(@Valid @RequestBody Sales sales, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        ReporteRecibido reporteRecibido = this.reporteHistoricoService.enviar_reporte_historico(sales);
        return new ResponseEntity<>(reporteRecibido, HttpStatus.OK);
    }

    @PostMapping("/CrearRespuestaReporte")
    public ResponseEntity<RespuestaReporte> crearRespuestaReporte(@Valid @RequestBody Sales sales, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        RespuestaReporte respuestaReporte = this.reporteHistoricoService.crearRespuestaReporte(sales);
        return new ResponseEntity<>(respuestaReporte, HttpStatus.OK);
    }
}
