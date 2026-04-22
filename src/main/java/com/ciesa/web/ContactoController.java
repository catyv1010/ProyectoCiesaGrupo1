package com.ciesa.web;

import com.ciesa.domain.Contacto;
import com.ciesa.service.ContactoService;
import com.ciesa.service.CorreoService;
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
@RequestMapping("/contacto")
public class ContactoController {

    private final ContactoService contactoService;
    private final CorreoService correoService;

    @Value("${ciesa.admin.email}")
    private String adminEmail;

    public ContactoController(ContactoService contactoService,
            CorreoService correoService) {
        this.contactoService = contactoService;
        this.correoService = correoService;
    }

    @GetMapping
    public String mostrar(Model model) {
        model.addAttribute("contacto", new Contacto());
        return "contacto";
    }

    @PostMapping("/enviar")
    public String enviar(@Valid @ModelAttribute Contacto contacto,
            BindingResult result,
            RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("error",
                    "Por favor, complete todos los campos correctamente.");
            return "redirect:/contacto";
        }
        contacto.setFechaEnvio(LocalDateTime.now());
        contacto.setLeido(false);
        contactoService.guardar(contacto);

        // Enviar correo de confirmación al usuario que escribió
        enviarCorreoConfirmacion(contacto);

        // Enviar notificación al administrador
        enviarCorreoAdmin(contacto);

        redirectAttrs.addFlashAttribute("exito", true);
        return "redirect:/contacto";
    }

    private void enviarCorreoConfirmacion(Contacto c) {
        try {
            String contenido = "<h1 style='color:#1a5276;'>Hemos recibido su mensaje</h1>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p>Estimado(a) <b>" + c.getNombre() + "</b>,</p>"
                    + "<p>Gracias por comunicarse con CIESA. Hemos recibido su mensaje "
                    + "con el asunto: <b>\"" + c.getAsunto() + "\"</b>.</p>"
                    + "<p>Nuestro equipo revisará su consulta y le responderemos "
                    + "a la brevedad posible al correo <b>" + c.getCorreo() + "</b>.</p>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p style='color:#888; font-size:0.9em;'>Este es un correo automático, "
                    + "por favor no responda a este mensaje.</p>"
                    + "<h2 style='color:#1a5276;'>Equipo CIESA</h2>";

            correoService.enviarCorreoHtml(
                    c.getCorreo(),
                    "CIESA - Mensaje recibido: " + c.getAsunto(),
                    contenido);
        } catch (Exception e) {
            System.out.println("Error enviando correo de confirmación: " + e.getMessage());
        }
    }

    private void enviarCorreoAdmin(Contacto c) {
        try {
            String fecha = c.getFechaEnvio()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String contenido = "<h1 style='color:#1a5276;'>Nuevo mensaje de contacto</h1>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p>Se ha recibido un nuevo mensaje de contacto:</p>"
                    + "<ul>"
                    + "<li><b>Nombre:</b> " + c.getNombre() + "</li>"
                    + "<li><b>Correo:</b> " + c.getCorreo() + "</li>"
                    + "<li><b>Asunto:</b> " + c.getAsunto() + "</li>"
                    + "<li><b>Fecha:</b> " + fecha + "</li>"
                    + "</ul>"
                    + "<p><b>Mensaje:</b></p>"
                    + "<blockquote style='border-left:3px solid #1a5276; padding-left:15px; "
                    + "color:#555;'>" + c.getMensaje() + "</blockquote>"
                    + "<hr style='border:1px solid #ddd;'>"
                    + "<p>Revise este mensaje desde el panel de administración.</p>"
                    + "<h2 style='color:#1a5276;'>Sistema CIESA</h2>";

            correoService.enviarCorreoHtml(
                    adminEmail,
                    "Nuevo contacto: " + c.getNombre() + " - " + c.getAsunto(),
                    contenido);
        } catch (Exception e) {
            System.out.println("Error enviando correo al admin: " + e.getMessage());
        }
    }
}
