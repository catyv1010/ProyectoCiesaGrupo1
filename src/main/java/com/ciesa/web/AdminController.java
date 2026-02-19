package com.ciesa.web;

import com.ciesa.service.ContactoService;
import com.ciesa.service.PostulacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ContactoService contactoService;
    private final PostulacionService postulacionService;

    public AdminController(ContactoService contactoService,
            PostulacionService postulacionService) {
        this.contactoService = contactoService;
        this.postulacionService = postulacionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalContactos", contactoService.getTodos().size());
        model.addAttribute("contactosNuevos", contactoService.getNoleidos().size());
        model.addAttribute("totalPostulaciones", postulacionService.getTodas().size());
        model.addAttribute("postulacionesNuevas", postulacionService.getNoRevisadas().size());
        return "admin/dashboard";
    }

    @GetMapping("/contactos")
    public String contactos(Model model) {
        model.addAttribute("lista", contactoService.getTodos());
        return "admin/contactos";
    }

    @GetMapping("/contactos/leido/{id}")
    public String marcarLeido(@PathVariable Integer id) {
        contactoService.marcarLeido(id);
        return "redirect:/admin/contactos";
    }

    @GetMapping("/postulaciones")
    public String postulaciones(Model model) {
        model.addAttribute("lista", postulacionService.getTodas());
        return "admin/postulaciones";
    }

    @GetMapping("/postulaciones/revisar/{id}")
    public String marcarRevisada(@PathVariable Integer id) {
        postulacionService.marcarRevisada(id);
        return "redirect:/admin/postulaciones";
    }
}
