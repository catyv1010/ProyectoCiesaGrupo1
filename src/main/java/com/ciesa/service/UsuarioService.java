package com.ciesa.service;

import com.ciesa.domain.Rol;
import com.ciesa.domain.Usuario;
import com.ciesa.repository.RolRepository;
import com.ciesa.repository.UsuarioRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            @Lazy PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public void guardar(Usuario usuario, Integer idRol) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void toggleActivo(Integer id) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setActivo(!u.isActivo());
            usuarioRepository.save(u);
        });
    }
}
