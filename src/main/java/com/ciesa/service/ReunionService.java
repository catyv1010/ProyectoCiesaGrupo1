package com.ciesa.service;

import com.ciesa.domain.Reunion;
import com.ciesa.repository.ReunionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReunionService {

    private final ReunionRepository reunionRepository;

    public ReunionService(ReunionRepository reunionRepository) {
        this.reunionRepository = reunionRepository;
    }

    @Transactional(readOnly = true)
    public List<Reunion> getTodas() {
        return reunionRepository.findAllByOrderByFechaEnvioDesc();
    }

    @Transactional(readOnly = true)
    public List<Reunion> getPendientes() {
        return reunionRepository.findByAtendidoFalseOrderByFechaEnvioDesc();
    }

    @Transactional
    public void guardar(Reunion reunion) {
        reunionRepository.save(reunion);
    }

    @Transactional
    public void marcarAtendida(Integer id) {
        reunionRepository.findById(id).ifPresent(r -> {
            r.setAtendido(true);
            reunionRepository.save(r);
        });
    }

    @Transactional
    public void eliminar(Integer id) {
        reunionRepository.deleteById(id);
    }
}
