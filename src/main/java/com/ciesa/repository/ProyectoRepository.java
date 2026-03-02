package com.ciesa.repository;

import com.ciesa.domain.Proyecto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    List<Proyecto> findByActivoTrueOrderByFechaCreacionDesc();

    List<Proyecto> findAllByOrderByFechaCreacionDesc();
}
