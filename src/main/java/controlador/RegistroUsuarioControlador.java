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

        if (!validarCedula(cedula)) {
            JOptionPane.showMessageDialog(vista, "Cedula invalida, verifique el numero de cedula");
            return false;
        }

        String tipoSeleccionado = (String) vista.getCmbTipoUsuario().getSelectedItem();
        if (tipoSeleccionado == null || tipoSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione el tipo de usuario");
            return false;
        }

        usuario.setCedula(cedula);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(email);
        usuario.setTelefono(telefono);

        String sql = "INSERT INTO usuario (cedula, nombres, apellidos, telefono, correo) VALUES (?, ?, ?, ?, ?)";

        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_registrar_usuario(?, ?, ?, ?, ?, ?)}");
            cs.setString(1, usuario.getCedula());
            cs.setString(2, usuario.getNombres());
            cs.setString(3, usuario.getApellidos());
            cs.setString(4, usuario.getTelefono());
            cs.setString(5, usuario.getCorreo());
            cs.setString(6, tipoSeleccionado);

            boolean resultado = cs.executeUpdate() > 0;

            if (resultado) {
                cargarTabla();
                limpiarCampos();
            }
            return resultado;

        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean validarCedula(String cedula) {
        if (cedula.length() != 10) {
            return false;
        }

        for (int i = 0; i < 10; i++) {
            if (!Character.isDigit(cedula.charAt(i))) {
                return false;
            }
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }

        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito < 0 || tercerDigito > 5) {
            return false;
        }

        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i));
            int multiplicador = (i % 2 == 0) ? 2 : 1;
            int producto = digito * multiplicador;
            if (producto >= 10) {
                producto -= 9;
            }
            suma += producto;
        }

        int digitoVerificador = Character.getNumericValue(cedula.charAt(9));
        int decenaSuperior = (int) Math.ceil(suma / 10.0) * 10;
        int verificadorCalculado = decenaSuperior - suma;

        return verificadorCalculado == digitoVerificador;
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
        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_eliminar_usuario(?)}");
            cs.setInt(1, idUsuario);
            return cs.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            JOptionPane.showMessageDialog(vista, e.getMessage());
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
        }
    }

    public boolean actualizarUsuario(Usuario usuario) {
        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_actualizar_usuario(?, ?, ?, ?, ?, ?)}");
            cs.setString(1, usuario.getCedula());
            cs.setString(2, usuario.getNombres());
            cs.setString(3, usuario.getApellidos());
            cs.setString(4, usuario.getTelefono());
            cs.setString(5, usuario.getCorreo());
            cs.setInt(6, usuario.getIdUsuario());

            return cs.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public void ejecutarOpcion() {
        String opcion = (String) vista.getCbmOpciones().getSelectedItem();
        if ("Actualizar".equals(opcion)) {
            actualizarUsuarioVista();
        } else if ("Eliminar".equals(opcion)) {
            eliminarUsuarioVista();
        } else {
            guardarUsuario();
        }
    }

    public void actualizarUsuarioVista() {
        String idTexto = vista.getTxtEliminar().getText().trim();
        String cedula = vista.getTxtCedula().getText();
        String nombres = vista.getTxtNombres().getText();
        String apellidos = vista.getTxtApellidos().getText();
        String email = vista.getTxtEmail().getText();
        String telefono = vista.getTxtTelefono().getText();

        if (idTexto.isEmpty() || cedula.isEmpty() || nombres.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar ID, cedula y nombres");
            return;
        }

        usuario.setIdUsuario(Integer.parseInt(idTexto));
        usuario.setCedula(cedula);
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(email);
        usuario.setTelefono(telefono);

        if (actualizarUsuario(usuario)) {
            JOptionPane.showMessageDialog(vista, "Usuario actualizado correctamente");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar el usuario, verifique el ID");
        }
    }

    public void buscarUsuarioVista() {
        String idTexto = vista.getTxtEliminar().getText().trim();

        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el ID del usuario a buscar");
            return;
        }

        Usuario encontrado = buscarUsuarioPorId(Integer.parseInt(idTexto));

        if (encontrado == null) {
            JOptionPane.showMessageDialog(vista, "No se encontro el usuario, verifique el ID");
            return;
        }

        vista.getTxtCedula().setText(encontrado.getCedula());
        vista.getTxtNombres().setText(encontrado.getNombres());
        vista.getTxtApellidos().setText(encontrado.getApellidos());
        vista.getTxtEmail().setText(encontrado.getCorreo());
        vista.getTxtTelefono().setText(encontrado.getTelefono());

        seleccionarTipoUsuario(Integer.parseInt(idTexto));
    }

    public Usuario buscarUsuarioPorId(int idUsuario) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idUsuario);
            resultado = ejecutar.executeQuery();

            if (resultado.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(resultado.getInt("id_usuario"));
                u.setCedula(resultado.getString("cedula"));
                u.setNombres(resultado.getString("nombres"));
                u.setApellidos(resultado.getString("apellidos"));
                u.setTelefono(resultado.getString("telefono"));
                u.setCorreo(resultado.getString("correo"));
                return u;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    private void seleccionarTipoUsuario(int idUsuario) {
        String sql = "SELECT * FROM tipo_usuario WHERE id_usuario = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idUsuario);
            resultado = ejecutar.executeQuery();

            if (resultado.next()) {
                TipoUsuario tipo = new TipoUsuario();
                tipo.setEstudiante(resultado.getString("estudiante"));
                tipo.setDocente(resultado.getString("docente"));
                vista.getCmbTipoUsuario().setSelectedItem(tipo.getTipo());
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar tipo de usuario: " + e.getMessage());
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
