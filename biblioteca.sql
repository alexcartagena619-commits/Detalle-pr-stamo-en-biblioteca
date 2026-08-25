-- =============================================
-- BASE DE DATOS: BIBLIOTECA / MEDIATECA
-- Script SQL para crear las tablas del sistema
-- =============================================

CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

-- =============================================
-- TABLA: USUARIO
-- Almacena la información de estudiantes y profesores
-- =============================================
CREATE TABLE IF NOT EXISTS usuario (
    id INT PRIMARY KEY,
    cedula VARCHAR(10) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(15),
    correo VARCHAR(100),
    tipo_usuario VARCHAR(20) NOT NULL
);

-- =============================================
-- TABLA: LIBRO
-- Almacena la información de los libros disponibles
-- =============================================
CREATE TABLE IF NOT EXISTS libro (
    id INT PRIMARY KEY,
    codigo_inventario VARCHAR(20) NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    editorial VARCHAR(100),
    categoria VARCHAR(50),
    stock INT NOT NULL DEFAULT 0,
    estado_fisico VARCHAR(50)
);

-- =============================================
-- TABLA: PRESTAMO
-- Registra cada préstamo realizado
-- =============================================
CREATE TABLE IF NOT EXISTS prestamo (
    id_prestamo INT PRIMARY KEY,
    fecha_salida DATE NOT NULL,
    fecha_limite DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    nombre_bibliotecaria VARCHAR(100) NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

-- =============================================
-- TABLA: DETALLE_PRESTAMO
-- Detalle de los libros incluidos en cada préstamo
-- =============================================
CREATE TABLE IF NOT EXISTS detalle_prestamo (
    id_detalle INT PRIMARY KEY AUTO_INCREMENT,
    id_prestamo INT NOT NULL,
    id_libro INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    FOREIGN KEY (id_prestamo) REFERENCES prestamo(id_prestamo),
    FOREIGN KEY (id_libro) REFERENCES libro(id)
);

-- =============================================
-- TABLA: MULTA
-- Registra multas por retraso en devolución
-- =============================================
CREATE TABLE IF NOT EXISTS multa (
    id_multa INT PRIMARY KEY AUTO_INCREMENT,
    id_prestamo INT NOT NULL,
    dias_retraso INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
    FOREIGN KEY (id_prestamo) REFERENCES prestamo(id_prestamo)
);

-- =============================================
-- DATOS DE PRUEBA
-- =============================================

INSERT INTO usuario (id, cedula, nombres, apellido, telefono, correo, tipo_usuario) VALUES
(1, '1723456789', 'Maria Jose', 'Lopez', '0991234567', 'maria.lopez@universidad.edu', 'Estudiante'),
(2, '1712345678', 'Carlos Andres', 'Rodriguez', '0987654321', 'carlos.rodriguez@universidad.edu', 'Estudiante'),
(3, '1709876543', 'Ana Lucia', 'Martinez', '0971112233', 'ana.martinez@universidad.edu', 'Profesor'),
(4, '1711112222', 'Pedro Luis', 'Garcia', '0963334455', 'pedro.garcia@universidad.edu', 'Profesor');

INSERT INTO libro (id, codigo_inventario, titulo, autor, editorial, categoria, stock, estado_fisico) VALUES
(1, 'LIB-001', 'Cien Anos de Soledad', 'Gabriel Garcia Marquez', 'Sudamericana', 'Novela', 3, 'Bueno'),
(2, 'LIB-002', 'El Principito', 'Antoine de Saint-Exupery', 'Reynal & Hijos', 'Ficcion', 5, 'Bueno'),
(3, 'LIB-003', 'Don Quijote de la Mancha', 'Miguel de Cervantes', 'Francisco de Robles', 'Clasico', 2, 'Regular'),
(4, 'LIB-004', 'La Sombra del Viento', 'Carlos Ruiz Zafon', 'Planeta', 'Misterio', 4, 'Bueno'),
(5, 'LIB-005', 'Rayuela', 'Julio Cortazar', 'Sudamericana', 'Novela', 2, 'Bueno');
