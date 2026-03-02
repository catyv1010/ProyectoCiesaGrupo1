package com.ciesa.web;

import com.ciesa.domain.Proyecto;
import com.ciesa.service.ContactoService;
import com.ciesa.service.PostulacionService;
import com.ciesa.service.ProyectoService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ContactoService contactoService;
    private final PostulacionService postulacionService;
    private final ProyectoService proyectoService;

    public AdminController(ContactoService contactoService,
            PostulacionService postulacionService,
            ProyectoService proyectoService) {
        this.contactoService = contactoService;
        this.postulacionService = postulacionService;
        this.proyectoService = proyectoService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalContactos", contactoService.getTodos().size());
        model.addAttribute("contactosNuevos", contactoService.getNoleidos().size());
        model.addAttribute("totalPostulaciones", postulacionService.getTodas().size());
        model.addAttribute("postulacionesNuevas", postulacionService.getNoRevisadas().size());
        model.addAttribute("totalProyectos", proyectoService.getTodos().size());
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

    // ========== CRUD PROYECTOS ==========

    @GetMapping("/proyectos")
    public String proyectos(Model model) {
        var lista = proyectoService.getTodos();
        model.addAttribute("lista", lista);
        model.addAttribute("proyecto", new Proyecto());
        model.addAttribute("totalProyectos", lista.size());
        return "admin/proyectos";
    }

    @PostMapping("/proyectos/guardar")
    public String guardarProyecto(@ModelAttribute Proyecto proyecto,
            RedirectAttributes redirectAttributes) {
        if (proyecto.getIdProyecto() == null) {
            proyecto.setFechaCreacion(LocalDateTime.now());
        }
        proyectoService.guardar(proyecto);
        redirectAttributes.addFlashAttribute("exito", "Proyecto guardado correctamente.");
        return "redirect:/admin/proyectos";
    }

    @GetMapping("/proyectos/modificar/{id}")
    public String modificarProyecto(@PathVariable Integer id,
            Model model, RedirectAttributes redirectAttributes) {
        Optional<Proyecto> proyectoOpt = proyectoService.getProyecto(id);
        if (proyectoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado.");
            return "redirect:/admin/proyectos";
        }
        model.addAttribute("proyecto", proyectoOpt.get());
        return "admin/proyecto_modifica";
    }

    @PostMapping("/proyectos/eliminar")
    public String eliminarProyecto(@RequestParam Integer idProyecto,
            RedirectAttributes redirectAttributes) {
        try {
            proyectoService.eliminar(idProyecto);
            redirectAttributes.addFlashAttribute("exito", "Proyecto eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el proyecto.");
        }
        return "redirect:/admin/proyectos";
    }
}
