package com.ciesa.web;

import com.ciesa.domain.Postulacion;
import com.ciesa.service.CorreoService;
import com.ciesa.service.FirebaseStorageService;
import com.ciesa.service.PostulacionService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
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
    private final CorreoService correoService;

    @Value("${ciesa.admin.email}")
    private String adminEmail;

    public PostulacionController(PostulacionService postulacionService,
            FirebaseStorageService firebaseStorageService,
            CorreoService correoService) {
        this.postulacionService = postulacionService;
        this.firebaseStorageService = firebaseStorageService;
        this.correoService = correoService;
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
                try {
                    String url = firebaseStorageService.uploadImage(archivoCv, "cv");
                    postulacion.setRutaCv(url);
                } catch (Exception firebaseEx) {
                    // Si Firebase falla, se guarda la postulación sin CV
                    System.out.println("=== ERROR FIREBASE CV: " + firebaseEx.getMessage() + " ===");
                }
            }
            postulacion.setFechaEnvio(LocalDateTime.now());
            postulacion.setRevisado(false);
            postulacionService.guardar(postulacion);

            // Enviar correo de confirmación al postulante
            enviarCorreoPostulante(postulacion);

            // Enviar notificación al administrador
            enviarCorreoAdmin(postulacion);

        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al enviar: " + e.getMessage());
            return "redirect:/empleo";
        }
        return "redirect:/empleo/gracias";
    }

    private void enviarCorreoPostulante(Postulacion p) {
        try {
            String contenido = "<h1 style='color:#1a5276;'>Gracias por su postulación</h1>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p>Estimado(a) <b>" + p.getNombre() + "</b>,</p>"
                    + "<p>Hemos recibido exitosamente su postulación para el puesto de "
                    + "<b>" + p.getPuesto() + "</b>.</p>"
                    + "<p>Su currículum ha sido registrado en nuestro sistema y será revisado "
                    + "por nuestro equipo de Recursos Humanos en los próximos días.</p>"
                    + "<p>Le contactaremos al correo <b>" + p.getCorreo() + "</b> "
                    + "o al teléfono <b>" + p.getTelefono() + "</b> si su perfil "
                    + "se ajusta a nuestros requerimientos.</p>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p style='color:#888; font-size:0.9em;'>Este es un correo automático, "
                    + "por favor no responda a este mensaje.</p>"
                    + "<h2 style='color:#1a5276;'>Equipo CIESA</h2>";

            correoService.enviarCorreoHtml(
                    p.getCorreo(),
                    "CIESA - Postulación recibida: " + p.getPuesto(),
                    contenido);
        } catch (Exception e) {
            System.out.println("=== ERROR CORREO POSTULANTE ===");
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void enviarCorreoAdmin(Postulacion p) {
        try {
            String fecha = p.getFechaEnvio()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String contenido = "<h1 style='color:#1a5276;'>Nueva postulación recibida</h1>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p>Se ha recibido una nueva postulación con los siguientes datos:</p>"
                    + "<ul>"
                    + "<li><b>Nombre:</b> " + p.getNombre() + "</li>"
                    + "<li><b>Cédula:</b> " + p.getCedula() + "</li>"
                    + "<li><b>Correo:</b> " + p.getCorreo() + "</li>"
                    + "<li><b>Teléfono:</b> " + p.getTelefono() + "</li>"
                    + "<li><b>Puesto:</b> " + p.getPuesto() + "</li>"
                    + "<li><b>Fecha:</b> " + fecha + "</li>"
                    + "</ul>"
                    + (p.getRutaCv() != null
                    ? "<p><a href='" + p.getRutaCv() + "'>Ver currículum adjunto</a></p>"
                    : "")
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p>Revise esta postulación desde el panel de administración.</p>"
                    + "<h2 style='color:#1a5276;'>Sistema CIESA</h2>";

            correoService.enviarCorreoHtml(
                    adminEmail,
                    "Nueva postulación: " + p.getNombre() + " - " + p.getPuesto(),
                    contenido);
        } catch (Exception e) {
            System.out.println("=== ERROR CORREO ADMIN ===");
            System.out.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
