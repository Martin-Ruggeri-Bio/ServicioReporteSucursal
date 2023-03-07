package ar.edu.um.programacion2.simple.controller;
import ar.edu.um.programacion2.simple.dtos.Message;
import ar.edu.um.programacion2.simple.dtos.Executor;
import ar.edu.um.programacion2.simple.dtos.PeticionReporteRecurrente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

@RestController
@RequestMapping("/ReportesRecurrentesHilos")
public class ReporteRecurrenteHilosController {

    @Value("${servicioFranquicia.token_id}")
    private String id_tocken_reporte;

    @Value("${logginSucursal.id_tocken}")
    private String id_tocken_sucursal;

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Map<Long, Executor> executors = new ConcurrentHashMap<>();

    @PostMapping("/Crear")
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
        
        Executor executor = new Executor(
            reporte.getId(),
            reporte.getFechaInicio(),
            reporte.getFechaFin(),
            reporte.getIntervalo(),
            false,
            id_tocken_reporte,
            id_tocken_sucursal
            );

        // Agregar el Executor al ExecutorService y al mapa de executors
        this.executorService.submit(executor);
        this.executors.put(reporte.getId(), executor);

        Message message = new Message("Servicio de reporte recurrente creado correctamente. Id de reporte: " + (reporte.getId()));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

    @DeleteMapping("/Cancelar/{idReporte}")
    public ResponseEntity<Message> cancelarReporteRecurrente(@PathVariable Long idReporte) {
        Executor executor = this.executors.get(idReporte);
        if (executor == null) {
            Message message = new Message("No se encontró el reporte recurrente con el id " + idReporte);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            executor.cancel(); // cancelar el Executor
            this.executors.remove(idReporte); // eliminar el Executor del mapa de executors
            Message message = new Message("Reporte recurrente con el id " + idReporte + " cancelado correctamente.");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }
}
