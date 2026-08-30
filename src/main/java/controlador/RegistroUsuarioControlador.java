/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.TipoUsuario;
import modelo.Usuario;
import vista.GestionUsuarios;

/**
 *
 * @author user
 */
public class RegistroUsuarioControlador {

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
    private GestionUsuarios vista;

    public RegistroUsuarioControlador(Usuario usuario, GestionUsuarios vista) {
        this.usuario = usuario;
        this.vista = vista;
    }

    public void iniciar() {
        vista.setControlador(this);
        vista.setVisible(true);
        cargarTabla();
        cargarComboTipos();
    }

    public void cargarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Cedula", "Nombres", "Apellidos", "Telefono", "Correo"});
        ArrayList<Usuario> usuarios = listarUsuarios();
        for (Usuario u : usuarios) {
            modelo.addRow(new Object[]{u.getIdUsuario(), u.getCedula(), u.getNombres(), u.getApellidos(), u.getTelefono(), u.getCorreo()});
        }
        vista.getTblUsuarios().setModel(modelo);
    }

    public void cargarComboTipos() {
        vista.getCmbTipoUsuario().removeAllItems();
        ArrayList<TipoUsuario> tipos = listarTiposUsuario();
        if (tipos.isEmpty()) {
            vista.getCmbTipoUsuario().addItem("Estudiante");
            vista.getCmbTipoUsuario().addItem("Docente");
            return;
        }
        for (TipoUsuario t : tipos) {
            String tipo = t.getTipo();
            if (vista.getCmbTipoUsuario().getItemCount() == 0 || !tipoUsuario(tipo)) {
                vista.getCmbTipoUsuario().addItem(tipo);
            }
        }
    }

    private boolean tipoUsuario(String tipo) {
        for (int i = 0; i < vista.getCmbTipoUsuario().getItemCount(); i++) {
            if (vista.getCmbTipoUsuario().getItemAt(i).equals(tipo)) {
                return true;
            }
        }
        return false;
    }

    public boolean guardarUsuario() {
        String cedula = vista.getTxtCedula().getText();
        String nombres = vista.getTxtNombres().getText();
        String apellidos = vista.getTxtApellidos().getText();
        String email = vista.getTxtEmail().getText();
        String telefono = vista.getTxtTelefono().getText();

        if (cedula.isEmpty() || nombres.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar cedula y nombres");
            return false;
        }

        usuario.setCedula(cedula);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(email);
        usuario.setTelefono(telefono);

        String sql = "INSERT INTO usuario (cedula, nombres, apellidos, telefono, correo) VALUES (?, ?, ?, ?, ?)";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, usuario.getCedula());
            ejecutar.setString(2, usuario.getNombres());
            ejecutar.setString(3, usuario.getApellidos());
            ejecutar.setString(4, usuario.getTelefono());
            ejecutar.setString(5, usuario.getCorreo());

            boolean resultado = ejecutar.executeUpdate() > 0;

            if (resultado) {
                cargarTabla();
                limpiarCampos();
            } else {
            }
            return resultado;

        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Usuario> listarUsuarios() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try {
            ejecutar = conectado.prepareStatement(sql);
            resultado = ejecutar.executeQuery();

            while (resultado.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(resultado.getInt("id_usuario"));
                u.setCedula(resultado.getString("cedula"));
                u.setNombres(resultado.getString("nombres"));
                u.setApellidos(resultado.getString("apellidos"));
                u.setTelefono(resultado.getString("telefono"));
                u.setCorreo(resultado.getString("correo"));
                lista.add(u);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idUsuario);
            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    public void eliminarUsuarioVista() {
        String idTexto = vista.getTxtEliminar().getText().trim();

        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el ID del usuario a eliminar");
            return;
        }

        int idUsuario = Integer.parseInt(idTexto);

        if (eliminarUsuario(idUsuario)) {
            JOptionPane.showMessageDialog(vista, "Usuario eliminado correctamente");
            vista.getTxtEliminar().setText("");
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar el usuario, verifique el ID");
        }
    }

    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuario SET cedula=?, nombres=?, apellidos=?, telefono=?, correo=? WHERE id_usuario=?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, usuario.getCedula());
            ejecutar.setString(2, usuario.getNombres());
            ejecutar.setString(3, usuario.getApellidos());
            ejecutar.setString(4, usuario.getTelefono());
            ejecutar.setString(5, usuario.getCorreo());
            ejecutar.setInt(6, usuario.getIdUsuario());

            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<TipoUsuario> listarTiposUsuario() {
        ArrayList<TipoUsuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_usuario";

        try {
            ejecutar = conectado.prepareStatement(sql);
            resultado = ejecutar.executeQuery();

            while (resultado.next()) {
                TipoUsuario tipo = new TipoUsuario();
                tipo.setIdTipoUsuario(resultado.getInt("id_tipo_usuario"));
                tipo.setEstudiante(resultado.getString("estudiante"));
                tipo.setDocente(resultado.getString("docente"));
                tipo.setIdUsuario(resultado.getInt("id_usuario"));
                lista.add(tipo);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar tipos usuario: " + e.getMessage());
        }
        return lista;
    }

    public void limpiarCampos() {
        vista.getTxtCedula().setText("");
        vista.getTxtNombres().setText("");
        vista.getTxtApellidos().setText("");
        vista.getTxtEmail().setText("");
        vista.getTxtTelefono().setText("");
//        vista.getCmbTipoUsuario().addItem(item);
    }
}
