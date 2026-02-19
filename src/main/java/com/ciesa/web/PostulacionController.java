package com.ciesa.web;

import com.ciesa.domain.Postulacion;
import com.ciesa.service.PostulacionService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empleo")
public class PostulacionController {

    private final PostulacionService postulacionService;

    public PostulacionController(PostulacionService postulacionService) {
        this.postulacionService = postulacionService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("postulacion", new Postulacion());
        return "empleo";
    }

    @PostMapping("/enviar")
    public String enviar(@ModelAttribute Postulacion postulacion,
            @RequestParam("archivoCv") MultipartFile archivoCv,
            RedirectAttributes redirectAttrs) {
        // NOTA: Para subir a Firebase, inyectar FirebaseStorageService y llamar uploadImage()
        // Por ahora se guarda solo el nombre del archivo
        if (!archivoCv.isEmpty()) {
            postulacion.setRutaCv(archivoCv.getOriginalFilename());
        }
        postulacion.setFechaEnvio(LocalDateTime.now());
        postulacion.setRevisado(false);
        postulacionService.guardar(postulacion);
        return "redirect:/empleo/gracias";
    }
}
