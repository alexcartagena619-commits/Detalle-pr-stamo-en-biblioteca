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
import modelo.Bibliotecario;

/**
 *
 * @author Usuario
 */
public class BibliotecarioControlador {

    //INSTANCIAR LA CONEXIÓN A LA BASE DE DATOS
    ConexionBDD conectar = new ConexionBDD();
    //CLASE QUE ME PERMITA CONECTARME DIRECTAMENTE A MYSQL
    Connection conectado = (Connection) conectar.conectar();
    //CLASE QUE ME PERMITE EJECUTAR MI SENTENCIA SQL
    PreparedStatement ejecutar;
    //OBTENER RESULTADOS DE LA CONSULTA
    ResultSet resultado;

    public boolean guardarBibliotecario(Bibliotecario bibliotecario) {
        String sql = "INSERT INTO bibliotecario (cedula, nombres, apellidos, telefono, correo) VALUES (?, ?, ?, ?, ?)";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, bibliotecario.getCedula());
            ejecutar.setString(2, bibliotecario.getNombres());
            ejecutar.setString(3, bibliotecario.getApellidos());
            ejecutar.setString(4, bibliotecario.getTelefono());
            ejecutar.setString(5, bibliotecario.getCorreo());

            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar bibliotecario: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Bibliotecario> listarBibliotecarios() {
        ArrayList<Bibliotecario> lista = new ArrayList<>();
        String sql = "SELECT * FROM bibliotecario";

        try {
            ejecutar = conectado.prepareStatement(sql);
            resultado = ejecutar.executeQuery();

            while (resultado.next()) {
                Bibliotecario bib = new Bibliotecario();
                bib.setIdBibliotecario(resultado.getInt("id_bibliotecario"));
                bib.setCedula(resultado.getString("cedula"));
                bib.setNombres(resultado.getString("nombres"));
                bib.setApellidos(resultado.getString("apellidos"));
                bib.setTelefono(resultado.getString("telefono"));
                bib.setCorreo(resultado.getString("correo"));
                lista.add(bib);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar bibliotecarios: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminarBibliotecario(int idBibliotecario) {
        String sql = "DELETE FROM bibliotecario WHERE id_bibliotecario = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idBibliotecario);
            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar bibliotecario: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarBibliotecario(Bibliotecario bibliotecario) {
        String sql = "UPDATE bibliotecario SET cedula=?, nombres=?, apellidos=?, telefono=?, correo=? WHERE id_bibliotecario=?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, bibliotecario.getCedula());
            ejecutar.setString(2, bibliotecario.getNombres());
            ejecutar.setString(3, bibliotecario.getApellidos());
            ejecutar.setString(4, bibliotecario.getTelefono());
            ejecutar.setString(5, bibliotecario.getCorreo());
            ejecutar.setInt(6, bibliotecario.getIdBibliotecario());

            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar bibliotecario: " + e.getMessage());
            return false;
        }
    }
}

//    public Bibliotecario iniciarSesion(String cedula) {
//        String sql = "SELECT * FROM bibliotecario WHERE cedula = ?";
//
//        try {
//            ejecutar = conectado.prepareStatement(sql);
//            ejecutar.setString(1, cedula);
//            resultado = ejecutar.executeQuery();
//
//            if (resultado.next()) {
//                Bibliotecario bib = new Bibliotecario();
//                bib.setIdBibliotecario(resultado.getInt("id_bibliotecario"));
//                bib.setCedula(resultado.getString("cedula"));
//                bib.setNombres(resultado.getString("nombres"));
//                bib.setApellidos(resultado.getString("apellidos"));
//                bib.setTelefono(resultado.getString("telefono"));
//                bib.setCorreo(resultado.getString("correo"));
//                return bib;
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Error al iniciar sesion: " + e.getMessage());
//        }
//        return null;
//    }
//}
