package ar.edu.um.programacion2.simple.repository;

import ar.edu.um.programacion2.simple.model.ReporteRecurrente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReporteRecurrenteRepository extends JpaRepository<ReporteRecurrente, Long>{
    public Optional<ReporteRecurrente> findById(Long id);
}
