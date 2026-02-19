package com.ciesa.service;

import com.ciesa.domain.Contacto;
import com.ciesa.repository.ContactoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactoService {

    private final ContactoRepository contactoRepository;

    public ContactoService(ContactoRepository contactoRepository) {
        this.contactoRepository = contactoRepository;
    }

    @Transactional(readOnly = true)
    public List<Contacto> getTodos() {
        return contactoRepository.findAllByOrderByFechaEnvioDesc();
    }

    @Transactional(readOnly = true)
    public List<Contacto> getNoleidos() {
        return contactoRepository.findByLeidoFalseOrderByFechaEnvioDesc();
    }

    @Transactional
    public void guardar(Contacto contacto) {
        contactoRepository.save(contacto);
    }

    @Transactional
    public void marcarLeido(Integer id) {
        contactoRepository.findById(id).ifPresent(c -> {
            c.setLeido(true);
            contactoRepository.save(c);
        });
    }
}
