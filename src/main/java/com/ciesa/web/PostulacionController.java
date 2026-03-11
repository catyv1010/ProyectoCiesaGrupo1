package com.ciesa.web;

import com.ciesa.domain.Postulacion;
import com.ciesa.service.FirebaseStorageService;
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
    private final FirebaseStorageService firebaseStorageService;

    public PostulacionController(PostulacionService postulacionService,
            FirebaseStorageService firebaseStorageService) {
        this.postulacionService = postulacionService;
        this.firebaseStorageService = firebaseStorageService;
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
        try {
            if (!archivoCv.isEmpty()) {
                String url = firebaseStorageService.uploadImage(archivoCv, "cv");
                postulacion.setRutaCv(url);
            }
            postulacion.setFechaEnvio(LocalDateTime.now());
            postulacion.setRevisado(false);
            postulacionService.guardar(postulacion);
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al enviar: " + e.getMessage());
            return "redirect:/empleo";
        }
        return "redirect:/empleo/gracias";
    }
}
