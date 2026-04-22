CREATE TABLE rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol    VARCHAR(25) NOT NULL UNIQUE,
  PRIMARY KEY (id_rol)
) ENGINE = InnoDB;

INSERT INTO rol (rol) VALUES ('ADMIN'), ('EDITOR');

-- ===== TABLA USUARIO =====
CREATE TABLE usuario (
  id_usuario   INT NOT NULL AUTO_INCREMENT,
  username     VARCHAR(30) NOT NULL UNIQUE,
  password     VARCHAR(512) NOT NULL,
  nombre       VARCHAR(50) NOT NULL,
  correo       VARCHAR(75) NULL UNIQUE,
  ruta_imagen  VARCHAR(1024),
  activo       BOOLEAN DEFAULT TRUE,
  fecha_creacion     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  INDEX ndx_username (username)
) ENGINE = InnoDB;

-- Usuario admin por defecto (password: admin123)
-- BCrypt de "admin123"
INSERT INTO usuario (username, password, nombre, correo, activo)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Administrador', 'admin@ciesa.co.cr', TRUE);

-- ===== TABLA USUARIO_ROL =====
CREATE TABLE usuario_rol (
  id_usuario INT NOT NULL,
  id_rol     INT NOT NULL,
  PRIMARY KEY (id_usuario, id_rol),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_rol)     REFERENCES rol(id_rol)
) ENGINE = InnoDB;

-- Asignar rol ADMIN al usuario admin
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (1, 1);

-- ===== TABLA RUTA (Spring Security dinámico) =====
CREATE TABLE ruta (
  id_ruta      INT NOT NULL AUTO_INCREMENT,
  ruta         VARCHAR(512) NOT NULL,
  requiere_rol BOOLEAN DEFAULT FALSE,
  id_rol       INT NULL,
  PRIMARY KEY (id_ruta),
  FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
) ENGINE = InnoDB;

-- Rutas PÚBLICAS (requiere_rol = 0)
INSERT INTO ruta (ruta, requiere_rol) VALUES
  ('/',               FALSE),
  ('/nosotros',       FALSE),
  ('/servicios',      FALSE),
  ('/proyectos',      FALSE),
  ('/contacto',       FALSE),
  ('/contacto/**',    FALSE),
  ('/empleo',         FALSE),
  ('/empleo/**',      FALSE),
  ('/reunion',        FALSE),
  ('/reunion/**',     FALSE),
  ('/css/**',         FALSE),
  ('/js/**',          FALSE),
  ('/img/**',         FALSE),
  ('/fav/**',         FALSE),
  ('/webjars/**',     FALSE),
  ('/login',          FALSE),
  ('/acceso_denegado',FALSE);

-- Rutas PROTEGIDAS (solo ADMIN, id_rol = 1)
INSERT INTO ruta (ruta, requiere_rol, id_rol) VALUES
  ('/admin/**', TRUE, 1);

-- Ruta pública para reunion (el formulario público)
INSERT INTO ruta (ruta, requiere_rol) VALUES ('/reunion/**', FALSE);

-- ===== TABLA CONTACTO =====
CREATE TABLE contacto (
  id_contacto INT NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(100) NOT NULL,
  correo      VARCHAR(100) NOT NULL,
  asunto      VARCHAR(200),
  mensaje     TEXT,
  leido       BOOLEAN DEFAULT FALSE,
  fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_contacto)
) ENGINE = InnoDB;

-- ===== TABLA POSTULACION =====
CREATE TABLE postulacion (
  id_postulacion INT NOT NULL AUTO_INCREMENT,
  nombre         VARCHAR(100) NOT NULL,
  cedula         VARCHAR(20) NOT NULL,
  correo         VARCHAR(100) NOT NULL,
  telefono       VARCHAR(25),
  puesto         VARCHAR(100),
  ruta_cv        VARCHAR(1024),
  revisado       BOOLEAN DEFAULT FALSE,
  fecha_envio    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_postulacion)
) ENGINE = InnoDB;

-- =============================================
-- DATOS DE PRUEBA
-- =============================================

INSERT INTO contacto (nombre, correo, asunto, mensaje, leido) VALUES
  ('Juan Pérez', 'juan@email.com', 'Cotización sistema eléctrico', 'Necesito cotización para mi local comercial.', FALSE),
  ('María López', 'maria@email.com', 'Información de servicios', 'Quisiera información sobre remodelaciones.', TRUE);

INSERT INTO postulacion (nombre, cedula, correo, telefono, puesto, revisado) VALUES
  ('Carlos Rodríguez', '1-2345-6789', 'carlos@email.com', '8888-1111', 'Ingeniero Eléctrico', FALSE);

-- =============================================
-- FIX: columna faltante en postulacion
-- =============================================
ALTER TABLE postulacion ADD COLUMN IF NOT EXISTS archivado BOOLEAN DEFAULT FALSE;

-- =============================================
-- TABLA REUNION
-- =============================================
CREATE TABLE reunion (
  id_reunion   INT NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(100) NOT NULL,
  correo       VARCHAR(100) NOT NULL,
  telefono     VARCHAR(25) NOT NULL,
  servicio     VARCHAR(100) NOT NULL,
  fecha        DATE NOT NULL,
  comentarios  TEXT,
  atendido     BOOLEAN DEFAULT FALSE,
  fecha_envio  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_reunion)
) ENGINE = InnoDB;

CREATE TABLE proyecto (
  id_proyecto INT NOT NULL AUTO_INCREMENT,
  titulo      VARCHAR(150) NOT NULL,
  descripcion TEXT,
  categoria   VARCHAR(50),
  ruta_imagen VARCHAR(1024),
  activo      BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_proyecto)
) ENGINE = InnoDB;

INSERT INTO proyecto (titulo, descripcion, categoria, ruta_imagen, activo) VALUES
  ('Instalación de tubería HN y aislante', 'Montaje e instalación de tubería HN con aislamiento térmico para sistemas electromecánicos, garantizando eficiencia operativa, seguridad y cumplimiento técnico en áreas técnicas y cuartos de equipos.', 'Electromecánica', '/img/proyecto1.jpg', TRUE),
  ('Instalación de tubería PVC y accesos', 'Ejecución de redes internas en tubería PVC para canalización de servicios, accesos técnicos y distribución en obra, tanto en proyectos nuevos como en remodelaciones.', 'Infraestructura', '/img/proyecto2.jpg', TRUE),
  ('Tubería HN céd. 40 y aislante', 'Instalación de tubería HN cédula 40 con aislamiento para sistemas mecánicos y electromecánicos, utilizada en infraestructuras comerciales, industriales y técnicas.', 'Electromecánica', '/img/proyecto3.jpg', TRUE),
  ('Planificación de obra previa a actividades', 'Planificación técnica y coordinación de obra antes del inicio de los trabajos, incluyendo logística, secuencia constructiva, seguridad y optimización de recursos en sitio.', 'Infraestructura', '/img/proyecto4.jpg', TRUE),
  ('Instalación de luminarias suspendidas', 'Instalación de sistemas de iluminación suspendida en áreas comerciales, industriales y de oficinas, cumpliendo criterios técnicos, eléctricos y normativos.', 'Electromecánica', '/img/proyecto5.jpg', TRUE),
  ('Instalación eléctrica: iluminación y tomacorrientes', 'Ejecución de instalaciones eléctricas para sistemas de iluminación y tomacorrientes, incluyendo cableado, canalización y puesta en funcionamiento conforme a normativa vigente.', 'Electromecánica', '/img/proyecto6.jpg', TRUE);

