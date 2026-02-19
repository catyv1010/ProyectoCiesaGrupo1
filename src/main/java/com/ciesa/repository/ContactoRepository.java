package com.ciesa.repository;

import com.ciesa.domain.Contacto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Integer> {
    List<Contacto> findAllByOrderByFechaEnvioDesc();
    List<Contacto> findByLeidoFalseOrderByFechaEnvioDesc();
}
