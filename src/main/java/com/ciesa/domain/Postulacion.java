package com.ciesa.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "postulacion")
public class Postulacion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_postulacion")
    private Integer idPostulacion;

    private String nombre;
    private String cedula;
    private String correo;
    private String telefono;
    private String puesto;

    @Column(name = "ruta_cv")
    private String rutaCv;

    private boolean revisado;
    private boolean archivado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    public Integer getIdPostulacion() { return idPostulacion; }
    public void setIdPostulacion(Integer idPostulacion) { this.idPostulacion = idPostulacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public String getRutaCv() { return rutaCv; }
    public void setRutaCv(String rutaCv) { this.rutaCv = rutaCv; }

    public boolean isRevisado() { return revisado; }
    public void setRevisado(boolean revisado) { this.revisado = revisado; }
    
    public boolean isArchivado() { return archivado; }
    public void setArchivado(boolean archivado) { this.archivado = archivado; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}
