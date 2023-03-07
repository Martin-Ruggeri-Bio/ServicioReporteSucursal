package ar.edu.um.programacion2.simple.repository;

import ar.edu.um.programacion2.simple.model.ReportesRecurrentes;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReportesRecurrentesRepository extends JpaRepository<ReportesRecurrentes, Long>{
    public Optional<ReportesRecurrentes> findById(Long id);

    ReportesRecurrentes findFirstByFechaRealizacionLessThanEqualOrderByFechaRealizacionAsc(LocalDateTime fechaActual);

    @Modifying
	public void deleteById(Long id);
}
