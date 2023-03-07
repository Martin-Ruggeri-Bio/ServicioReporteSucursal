package ar.edu.um.programacion2.simple.controller;

import ar.edu.um.programacion2.simple.dtos.Message;
import ar.edu.um.programacion2.simple.dtos.PeticionReporteRecurrente;
import ar.edu.um.programacion2.simple.model.ReportesRecurrentes;
import ar.edu.um.programacion2.simple.service.ReportesRecurrentesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ReportesRecurrentes")
public class ReportesRecurrentesController {
	@Autowired
	private ReportesRecurrentesService reportesRecurrentesService;

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
        ReportesRecurrentes reporteRecurrente = new ReportesRecurrentes(
            reporte.getId(),
            reporte.getTipo(),
            reporte.getFechaInicio(),
            reporte.getFechaFin(),
            reporte.getIntervalo(),
            reporte.getFechaInicio()
            );
        this.reportesRecurrentesService.addReporte(reporteRecurrente);

        Message message = new Message("Servicio de reporte recurrente creado correctamente. Id de reporte: " + (reporteRecurrente.getId()));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/Cancelar/{idReporte}")
    public ResponseEntity<Message> cancelarReporteRecurrente(@PathVariable Long idReporte) {
        ReportesRecurrentes reporteRecurrente = this.reportesRecurrentesService.findById(idReporte);
        if (reporteRecurrente != null) {
            this.reportesRecurrentesService.deleteById(idReporte);
            Message message = new Message("Reporte recurrente cancelado correctamente. Id de reporte: " + idReporte);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            Message message = new Message("No se encontró un reporte recurrente con el id especificado.");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }
}
