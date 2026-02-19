package com.ciesa.repository;

import com.ciesa.domain.Ruta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    List<Ruta> findAllByOrderByRequiereRolAsc();
}
