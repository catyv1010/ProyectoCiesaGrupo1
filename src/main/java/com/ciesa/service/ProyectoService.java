package com.ciesa.service;

import com.ciesa.domain.Proyecto;
import com.ciesa.repository.ProyectoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    @Transactional(readOnly = true)
    public List<Proyecto> getTodos() {
        return proyectoRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Transactional(readOnly = true)
    public List<Proyecto> getActivos() {
        return proyectoRepository.findByActivoTrueOrderByFechaCreacionDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Proyecto> getProyecto(Integer id) {
        return proyectoRepository.findById(id);
    }

    @Transactional
    public void guardar(Proyecto proyecto) {
        proyectoRepository.save(proyecto);
    }

    @Transactional
    public void eliminar(Integer id) {
        proyectoRepository.deleteById(id);
    }
}
