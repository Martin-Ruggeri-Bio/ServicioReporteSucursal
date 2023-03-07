package ar.edu.um.programacion2.simple.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.edu.um.programacion2.simple.model.ReportesRecurrentes;
import ar.edu.um.programacion2.simple.repository.ReportesRecurrentesRepository;


@Service
public class ReportesRecurrentesService {

    @Autowired
    private ReportesRecurrentesRepository repository;

    public void addReporte(ReportesRecurrentes reportesRecurrentes){
        this.repository.save(reportesRecurrentes);
    }
    public ReportesRecurrentes findById(Long id) {
		return repository.findById(id).orElse(null);
	}

    public void deleteById(Long id) {
		repository.deleteById(id);
	}

    public ReportesRecurrentes buscarPrimerReporteListoParaRealizar(LocalDateTime fechaActual) {
        return repository.findFirstByFechaRealizacionLessThanEqualOrderByFechaRealizacionAsc(fechaActual);
    }

    public void updateReporte(Long id, ReportesRecurrentes reportesRecurrentes){
        this.repository.deleteById(id);
        this.repository.save(reportesRecurrentes);
    }
}
