/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    }

