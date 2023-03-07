package ar.edu.um.programacion2.simple.controller;

import ar.edu.um.programacion2.simple.dtos.DateRange;
import ar.edu.um.programacion2.simple.dtos.Message;
import ar.edu.um.programacion2.simple.dtos.PeticionReporteHistorico;
import ar.edu.um.programacion2.simple.service.ReporteHistoricoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.task.TaskExecutor;

import javax.validation.Valid;


@RestController
@RequestMapping("/ReporteHistorico")
public class ReporteHistoricoController {

	@Autowired
	private ReporteHistoricoService reporteHistoricoService;

    @Autowired
    private TaskExecutor taskExecutor;

    // ReporteControler 
    @PostMapping("/Crear")
    public ResponseEntity<Message> historicoPrueba(@Valid @RequestBody PeticionReporteHistorico peticionReporteHistorico, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        DateRange dateRange = new DateRange(peticionReporteHistorico.getFechaInicio() ,peticionReporteHistorico.getFechaFin());
        taskExecutor.execute(() -> {
            this.reporteHistoricoService.hacerReporteHistorico(dateRange);
        });
        Message message = new Message("Creando Reporte");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}