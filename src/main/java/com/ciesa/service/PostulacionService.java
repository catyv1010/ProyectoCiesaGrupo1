package com.ciesa.service;

import com.ciesa.domain.Postulacion;
import com.ciesa.repository.PostulacionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostulacionService {

    private final PostulacionRepository postulacionRepository;

    public PostulacionService(PostulacionRepository postulacionRepository) {
        this.postulacionRepository = postulacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Postulacion> getTodas() {
        return postulacionRepository.findAllByOrderByFechaEnvioDesc();
    }

    @Transactional(readOnly = true)
    public List<Postulacion> getNoRevisadas() {
        return postulacionRepository.findByRevisadoFalseOrderByFechaEnvioDesc();
    }

    @Transactional
    public void guardar(Postulacion postulacion) {
        postulacionRepository.save(postulacion);
    }

    @Transactional
    public void marcarRevisada(Integer id) {
        postulacionRepository.findById(id).ifPresent(p -> {
            p.setRevisado(true);
            postulacionRepository.save(p);
        });
    }
}
