/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Usuario;
import vista.RegistrarDevolucion;

/**
 *
 * @author user
 */
public class DevolucionControlador {

    //INSTANCIAR LA CONEXIÓN A LA BASE DE DATOS
    ConexionBDD conectar = new ConexionBDD();
    //CLASE QUE ME PERMITA CONECTARME DIRECTAMENTE A MYSQL
    Connection conectado = (Connection) conectar.conectar();
    //CLASE QUE ME PERMITE EJECUTAR MI SENTENCIA SQL
    PreparedStatement ejecutar;
    //OBTENER RESULTADOS DE LA CONSULTA
    ResultSet resultado;

    //ATRIBUTO DEL MODELO
    private Usuario usuario;
    //ATRIBUTO DE LA VISTA
    private RegistrarDevolucion vista;

    public DevolucionControlador(Usuario usuario, RegistrarDevolucion vista) {
        this.usuario = usuario;
        this.vista = vista;
    }

    public void iniciar() {
        vista.setControlador(this);
        cargarTipoUsuario();
        cargarFecha();
        vista.setVisible(true);
    }

    public void cargarTipoUsuario() {
        vista.getCbmTipoUsuario().removeAllItems();
        vista.getCbmTipoUsuario().addItem("Estudiante");
        vista.getCbmTipoUsuario().addItem("Docente");
    }

    public void cargarFecha() {
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        vista.getTxtFecha().setText(hoy.format(formato));
    }

    public void buscarUsuario() {
        String cedula = vista.getTxtCedula().getText().trim();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el numero de cedula");
            return;
        }

        Usuario encontrado = buscarUsuarioPorCedula(cedula);

        if (encontrado == null) {
            JOptionPane.showMessageDialog(vista, "No se encontro el usuario, verifique la cedula");
            return;
        }

        usuario = encontrado;
        vista.getTxtNombres().setText(encontrado.getNombres());
        vista.getTxtApellidos().setText(encontrado.getApellidos());
        vista.getTxtTelfono().setText(encontrado.getTelefono());

        String tipo = obtenerTipoUsuario(encontrado.getIdUsuario());
        if (tipo != null) {
            vista.getCbmTipoUsuario().setSelectedItem(tipo);
        }

        cargarPrestamos(encontrado.getIdUsuario());
        cargarLibrosPrestados(encontrado.getIdUsuario());
    }

    public Usuario buscarUsuarioPorCedula(String cedula) {
        String sql = "SELECT * FROM usuario WHERE cedula = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, cedula);
            resultado = ejecutar.executeQuery();

            if (resultado.next()) {
                Usuario usu = new Usuario();
                usu.setIdUsuario(resultado.getInt("id_usuario"));
                usu.setCedula(resultado.getString("cedula"));
                usu.setNombres(resultado.getString("nombres"));
                usu.setApellidos(resultado.getString("apellidos"));
                usu.setTelefono(resultado.getString("telefono"));
                usu.setCorreo(resultado.getString("correo"));
                return usu;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    public String obtenerTipoUsuario(int idUsuario) {
        String sql = "SELECT estudiante, docente FROM tipo_usuario WHERE id_usuario = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idUsuario);
            resultado = ejecutar.executeQuery();

            if (resultado.next()) {
                String estudiante = resultado.getString("estudiante");
                String docente = resultado.getString("docente");
                if (estudiante != null && !estudiante.isEmpty()) {
                    return "Estudiante";
                }
                if (docente != null && !docente.isEmpty()) {
                    return "Docente";
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipo de usuario: " + e.getMessage());
        }
        return null;
    }

    public void cargarPrestamos(int idUsuario) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID Prestamo", "Libro", "Cantidad", "Estado Fisico", "Fecha Limite", "ID Detalle", "ID Libro"});

        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_prestamos_activos(?)}");
            cs.setInt(1, idUsuario);
            resultado = cs.executeQuery();

            while (resultado.next()) {
                modelo.addRow(new Object[]{
                    resultado.getInt("id_prestamo"),
                    resultado.getString("titulo"),
                    resultado.getInt("cantidad"),
                    resultado.getString("estadoFisico"),
                    resultado.getDate("fecha_limite_devolucion"),
                    resultado.getInt("id_detalle"),
                    resultado.getInt("id_libro")
                });
            }

        } catch (SQLException e) {
            System.err.println("Error al listar prestamos: " + e.getMessage());
        }

        vista.getTblEstadoLibros().setModel(modelo);
    }

    public void cargarLibrosPrestados(int idUsuario) {
        vista.getCmbLibros().removeAllItems();

        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_libros_prestados(?)}");
            cs.setInt(1, idUsuario);
            resultado = cs.executeQuery();

            while (resultado.next()) {
                vista.getCmbLibros().addItem(resultado.getString("titulo"));
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar libros prestados: " + e.getMessage());
        }
    }

    public void seleccionarLibro() {
        String titulo = (String) vista.getCmbLibros().getSelectedItem();
        if (titulo == null) {
            return;
        }
        int idDetalle = -1;
        int idLibro = -1;
        int cantidad = 1;
        String estado = "";

        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_detalle_libro_prestado(?, ?)}");
            cs.setInt(1, usuario != null ? usuario.getIdUsuario() : -1);
            cs.setString(2, titulo);
            resultado = cs.executeQuery();

            if (resultado.next()) {
                idDetalle = resultado.getInt("id_detalle");
                idLibro = resultado.getInt("id_libro");
                cantidad = resultado.getInt("cantidad");
                estado = resultado.getString("estadoFisico");
            }

        } catch (SQLException e) {
            System.err.println("Error al seleccionar libro: " + e.getMessage());
        }

        if (idLibro > 0) {
            vista.getSpnCantidad().setValue(cantidad);
            vista.getTxtEstadoFisico().setText(estado != null ? estado : "");
        }
    }

    public String getTipoUsuario(int idUsuario) {
        return obtenerTipoUsuario(idUsuario);
    }

    public int getIdTipoUsuario(String tipo) {
        String sql = "SELECT id_tipo_usuario FROM tipo_usuario WHERE "
                + ("Estudiante".equals(tipo) ? "estudiante IS NOT NULL" : "docente IS NOT NULL") + " LIMIT 1";

        try {
            ejecutar = conectado.prepareStatement(sql);
            resultado = ejecutar.executeQuery();
            if (resultado.next()) {
                return resultado.getInt("id_tipo_usuario");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener id tipo usuario: " + e.getMessage());
        }
        return -1;
    }

    public void guardarDevolucion() {
        if (usuario == null) {
            JOptionPane.showMessageDialog(vista, "Primero busque el usuario");
            return;
        }

        String titulo = (String) vista.getCmbLibros().getSelectedItem();
        if (titulo == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione el libro que va a devolver");
            return;
        }

        String estadoFisico = vista.getTxtEstadoFisico().getText().trim();
        if (estadoFisico.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el estado fisico del libro");
            return;
        }

        int idDetalle = -1;
        int idPrestamo = -1;
        int idLibro = -1;
        int cantidadPrestada = 1;

        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_detalle_libro_prestado(?, ?)}");
            cs.setInt(1, usuario.getIdUsuario());
            cs.setString(2, titulo);
            resultado = cs.executeQuery();
            if (resultado.next()) {
                idDetalle = resultado.getInt("id_detalle");
                idLibro = resultado.getInt("id_libro");
                cantidadPrestada = resultado.getInt("cantidad");
                idPrestamo = resultado.getInt("id_prestamo");
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar detalle a devolver: " + e.getMessage());
        }

        if (idDetalle <= 0) {
            JOptionPane.showMessageDialog(vista, "No se encontro un prestamo activo de ese libro");
            return;
        }

        boolean ok = registrarDevolucion(idPrestamo, idDetalle, idLibro, cantidadPrestada, estadoFisico);

        if (ok) {
            JOptionPane.showMessageDialog(vista, "Devolucion registrada correctamente");
            cargarPrestamos(usuario.getIdUsuario());
            cargarLibrosPrestados(usuario.getIdUsuario());
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo registrar la devolucion");
        }
    }

    public boolean registrarDevolucion(int idPrestamo, int idDetalle, int idLibro, int cantidad, String estadoFisico) {
        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_registrar_devolucion(?, ?, ?, ?, ?)}");
            cs.setInt(1, idPrestamo);
            cs.setInt(2, idDetalle);
            cs.setInt(3, idLibro);
            cs.setInt(4, cantidad);
            cs.setString(5, estadoFisico);
            return cs.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar devolucion: " + e.getMessage());
            return false;
        }
    }
}
