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
import modelo.Usuario;

/**
 *
 * @author user
 */
public class UsuarioControlador {

    //INSTANCIAR LA CONEXIÓN A LA BASE DE DATOS
    ConexionBDD conectar = new ConexionBDD();
    //CLASE QUE ME PERMITA CONECTARME DIRECTAMENTE A MYSQL
    Connection conectado = (Connection) conectar.conectar();
    //CLASE QUE ME PERMITE EJECUTAR MI SENTENCIA SQL
    PreparedStatement ejecutar;
    //OBTENER RESULTADOS DE LA CONSULTA
    ResultSet resultado;

    public boolean guardarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuario (cedula, nombres, apellidos, telefono, correo) VALUES (?, ?, ?, ?, ?)";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, usuario.getCedula());
            ejecutar.setString(2, usuario.getNombres());
            ejecutar.setString(3, usuario.getApellidos());
            ejecutar.setString(4, usuario.getTelefono());
            ejecutar.setString(5, usuario.getCorreo());

            return ejecutar.executeUpdate() > 0;

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
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultado.getInt("id_usuario"));
                usuario.setCedula(resultado.getString("cedula"));
                usuario.setNombres(resultado.getString("nombres"));
                usuario.setApellidos(resultado.getString("apellidos"));
                usuario.setTelefono(resultado.getString("telefono"));
                usuario.setCorreo(resultado.getString("correo"));
                lista.add(usuario);
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
}
