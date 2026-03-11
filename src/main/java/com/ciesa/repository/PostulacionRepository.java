package com.ciesa.repository;

import com.ciesa.domain.Postulacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostulacionRepository extends JpaRepository<Postulacion, Integer> {
    List<Postulacion> findAllByOrderByFechaEnvioDesc();
    List<Postulacion> findByRevisadoFalseOrderByFechaEnvioDesc();
    List<Postulacion> findByArchivadoTrueOrderByFechaEnvioDesc();
    List<Postulacion> findByArchivadoFalseOrderByFechaEnvioDesc();
    
}
