package com.ciesa.repository;

import com.ciesa.domain.Reunion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReunionRepository extends JpaRepository<Reunion, Integer> {
    List<Reunion> findAllByOrderByFechaEnvioDesc();
    List<Reunion> findByAtendidoFalseOrderByFechaEnvioDesc();
}
