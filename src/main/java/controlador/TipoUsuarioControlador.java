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
import modelo.TipoUsuario;

/**
 *
 * @author user
 */
public class TipoUsuarioControlador {

    //INSTANCIAR LA CONEXIÓN A LA BASE DE DATOS
    ConexionBDD conectar = new ConexionBDD();
    //CLASE QUE ME PERMITA CONECTARME DIRECTAMENTE A MYSQL
    Connection conectado = (Connection) conectar.conectar();
    //CLASE QUE ME PERMITE EJECUTAR MI SENTENCIA SQL
    PreparedStatement ejecutar;
    //OBTENER RESULTADOS DE LA CONSULTA
    ResultSet resultado;

    public boolean guardarTipoUsuario(TipoUsuario tipoUsuario) {
        String sql = "INSERT INTO tipo_usuario (estudiante, docente, id_usuario) VALUES (?, ?, ?)";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, tipoUsuario.getEstudiante());
            ejecutar.setString(2, tipoUsuario.getDocente());
            ejecutar.setInt(3, tipoUsuario.getIdUsuario());

            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar tipo usuario: " + e.getMessage());
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

    public boolean eliminarTipoUsuario(int idTipoUsuario) {
        String sql = "DELETE FROM tipo_usuario WHERE id_tipo_usuario = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idTipoUsuario);
            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar tipo usuario: " + e.getMessage());
            return false;
        }
    }
}
