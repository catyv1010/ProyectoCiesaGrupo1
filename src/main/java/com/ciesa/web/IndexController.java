package com.ciesa.web;

import com.ciesa.service.ProyectoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final ProyectoService proyectoService;

    public IndexController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }

    @GetMapping("/servicios")
    public String servicios() {
        return "servicios";
    }

    @GetMapping("/proyectos")
    public String proyectos(Model model) {
        model.addAttribute("proyectos", proyectoService.getActivos());
        return "proyectos";
    }
}
