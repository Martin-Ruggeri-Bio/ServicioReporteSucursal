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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    // private Thread servicioReporteRecurrenteThread;

    // Almacenar un solo servicio recurrente
    private ScheduledExecutorService executor;

    @PostMapping("/CrearRecurrente")
    public ResponseEntity<Message> ejecutarReporteRecurrente(@RequestBody PeticionReporteRecurrente reporte, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (reporte.getFechaInicio().isAfter(reporte.getFechaFin())) {
            Message message = new Message("La fecha de inicio debe ser anterior a la fecha de fin.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        if (reporte.getIntervalo().isNegative() || reporte.getIntervalo().isZero()) {
            Message message = new Message("El intervalo de tiempo debe ser mayor que cero.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }
        // if (servicioReporteRecurrenteThread != null && servicioReporteRecurrenteThread.isAlive()) {
        //     Message message = new Message("Ya hay un servicio de reporte recurrente activo.");
        //     return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        // }
        // Crear un hilo para ejecutar el servicio de forma asíncrona
        // servicioReporteRecurrenteThread = new Thread(new ReporteRecurrenteService(reporte));
        // servicioReporteRecurrenteThread.start();

        if (executor != null && !executor.isShutdown()) {
            Message message = new Message("Ya hay un servicio de reporte recurrente activo.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        // Crear un executor para planificar el servicio de forma asíncrona
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new ReporteRecurrenteService(reporte), 0, reporte.getIntervalo().getSeconds(), TimeUnit.SECONDS);

        Message message = new Message("Servicio de reporte recurrente iniciado correctamente.");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/CancelaRecurrente")
    public ResponseEntity<Message> cancelarReporteRecurrente() {
        // if (servicioReporteRecurrenteThread == null || !servicioReporteRecurrenteThread.isAlive()) {
        //     Message message = new Message("No hay un servicio de reporte recurrente activo.");
        //     return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        // }

        // // Interrumpir el hilo del servicio recurrente
        // servicioReporteRecurrenteThread.interrupt();
        // servicioReporteRecurrenteThread = null;

        if (executor == null || executor.isShutdown()) {
            Message message = new Message("No hay un servicio de reporte recurrente activo.");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }
            // Detener ScheduledExecutorService y limpiar la referencia
            executor.shutdown();
            executor = null;
        Message message = new Message("Servicio de reporte recurrente cancelado correctamente.");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
