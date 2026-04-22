package com.ciesa.web;

import com.ciesa.domain.Reunion;
import com.ciesa.service.CorreoService;
import com.ciesa.service.ReunionService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reunion")
public class ReunionController {

    private final ReunionService reunionService;
    private final CorreoService correoService;

    @Value("${ciesa.admin.email}")
    private String adminEmail;

    public ReunionController(ReunionService reunionService,
            CorreoService correoService) {
        this.reunionService = reunionService;
        this.correoService = correoService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("reunion", new Reunion());
        return "reunion";
    }

    @PostMapping("/confirmar")
    public String confirmar(@Valid @ModelAttribute Reunion reunion,
            BindingResult result,
            RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("error",
                    "Por favor, complete todos los campos correctamente.");
            return "redirect:/reunion";
        }

        try {
            reunion.setFechaEnvio(LocalDateTime.now());
            reunion.setAtendido(false);
            reunionService.guardar(reunion);

            enviarCorreoCliente(reunion);
            enviarCorreoAdmin(reunion);

        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error",
                    "Error al procesar la solicitud: " + e.getMessage());
            return "redirect:/reunion";
        }

        return "redirect:/reunion/confirmada";
    }

    private void enviarCorreoCliente(Reunion r) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String contenido = "<h1 style='color:#1a5276;'>Reunión agendada con éxito</h1>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p>Estimado(a) <b>" + r.getNombre() + "</b>,</p>"
                    + "<p>Hemos recibido su solicitud de reunión para el servicio de "
                    + "<b>" + r.getServicio() + "</b>, con fecha deseada: "
                    + "<b>" + r.getFecha().format(fmt) + "</b>.</p>"
                    + "<p>Nuestro equipo se pondrá en contacto para confirmar los detalles.</p>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p style='color:#888; font-size:0.9em;'>Este es un correo automático, "
                    + "por favor no responda a este mensaje.</p>"
                    + "<h2 style='color:#1a5276;'>Equipo CIESA</h2>";

            correoService.enviarCorreoHtml(
                    r.getCorreo(),
                    "CIESA - Solicitud de reunión recibida",
                    contenido);
        } catch (Exception e) {
            System.out.println("Error correo cliente reunión: " + e.getMessage());
        }
    }

    private void enviarCorreoAdmin(Reunion r) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String contenido = "<h1 style='color:#1a5276;'>Nueva solicitud de reunión</h1>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<ul>"
                    + "<li><b>Nombre:</b> " + r.getNombre() + "</li>"
                    + "<li><b>Correo:</b> " + r.getCorreo() + "</li>"
                    + "<li><b>Teléfono:</b> " + r.getTelefono() + "</li>"
                    + "<li><b>Servicio:</b> " + r.getServicio() + "</li>"
                    + "<li><b>Fecha deseada:</b> " + r.getFecha().format(fmt) + "</li>"
                    + (r.getComentarios() != null && !r.getComentarios().isBlank()
                            ? "<li><b>Comentarios:</b> " + r.getComentarios() + "</li>"
                            : "")
                    + "</ul>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p>Revise esta solicitud desde el panel de administración.</p>"
                    + "<h2 style='color:#1a5276;'>Sistema CIESA</h2>";

            correoService.enviarCorreoHtml(
                    adminEmail,
                    "Nueva reunión: " + r.getNombre() + " - " + r.getServicio(),
                    contenido);
        } catch (Exception e) {
            System.out.println("Error correo admin reunión: " + e.getMessage());
        }
    }
}
