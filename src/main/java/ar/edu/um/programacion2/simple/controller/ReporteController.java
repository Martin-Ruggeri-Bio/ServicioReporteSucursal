package ar.edu.um.programacion2.simple.controller;

import ar.edu.um.programacion2.simple.dtos.DateRange;
import ar.edu.um.programacion2.simple.dtos.Message;
import ar.edu.um.programacion2.simple.dtos.PeticionReporteHistorico;
import ar.edu.um.programacion2.simple.dtos.PeticionReporteRecurrente;
import ar.edu.um.programacion2.simple.service.ReporteHistoricoService;
import ar.edu.um.programacion2.simple.service.ReporteRecurrenteService;

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

    // Almacenar un solo servicio recurrente
    private Thread servicioReporteRecurrenteThread;

    @PostMapping("/CrearRecurrente")
    public ResponseEntity<Message> ejecutarReporteRecurrente(@RequestBody PeticionReporteRecurrente reporte) {
        if (servicioReporteRecurrenteThread != null && servicioReporteRecurrenteThread.isAlive()) {
            Message message = new Message("Ya hay un servicio de reporte recurrente activo.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        // Crear un hilo para ejecutar el servicio de forma asíncrona
        servicioReporteRecurrenteThread = new Thread(new ReporteRecurrenteService(reporte));
        servicioReporteRecurrenteThread.start();
        Message message = new Message("Servicio de reporte recurrente iniciado correctamente.");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/cancelar-recurrente")
    public ResponseEntity<Message> cancelarReporteRecurrente() {
        if (servicioReporteRecurrenteThread == null || !servicioReporteRecurrenteThread.isAlive()) {
            Message message = new Message("No hay un servicio de reporte recurrente activo.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        // Interrumpir el hilo del servicio recurrente
        servicioReporteRecurrenteThread.interrupt();
        servicioReporteRecurrenteThread = null;
        Message message = new Message("Servicio de reporte recurrente cancelado correctamente.");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
