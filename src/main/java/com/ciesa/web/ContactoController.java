package com.ciesa.web;

import com.ciesa.domain.Contacto;
import com.ciesa.service.ContactoService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contacto")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("contacto", new Contacto());
        return "contacto";
    }

    @PostMapping("/enviar")
    public String enviar(@ModelAttribute Contacto contacto,
            RedirectAttributes redirectAttrs) {
        contacto.setFechaEnvio(LocalDateTime.now());
        contacto.setLeido(false);
        contactoService.guardar(contacto);
        redirectAttrs.addFlashAttribute("exito", true);
        return "redirect:/contacto";
    }
}
