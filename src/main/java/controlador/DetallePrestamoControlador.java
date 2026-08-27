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
import modelo.DetallePrestamo;

/**
 *
 * @author user
 */
public class DetallePrestamoControlador {

    //INSTANCIAR LA CONEXIÓN A LA BASE DE DATOS
    ConexionBDD conectar = new ConexionBDD();
    //CLASE QUE ME PERMITA CONECTARME DIRECTAMENTE A MYSQL
    Connection conectado = (Connection) conectar.conectar();
    //CLASE QUE ME PERMITE EJECUTAR MI SENTENCIA SQL
    PreparedStatement ejecutar;
    //OBTENER RESULTADOS DE LA CONSULTA
    ResultSet resultado;

//    public boolean guardarDetalle(DetallePrestamo detalle) {
//        String sql = "INSERT INTO detalle_prestamo (id_prestamo, id_libro, estadoFisico, cantidad) VALUES (?, ?, ?, ?)";
//
//        try {
//            ejecutar = conectado.prepareStatement(sql);
//            ejecutar.setInt(1, detalle.getIdPrestamo());
//            ejecutar.setInt(2, detalle.getIdLibro());
//            ejecutar.setString(3, detalle.getEstadoFisico());
//            ejecutar.setInt(4, detalle.getCantidad());
//
//            return ejecutar.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            System.err.println("Error al guardar detalle: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public ArrayList<DetallePrestamo> listarDetallesPorPrestamo(int idPrestamo) {
//        ArrayList<DetallePrestamo> lista = new ArrayList<>();
//        String sql = "SELECT * FROM detalle_prestamo WHERE id_prestamo = ?";
//
//        try {
//            ejecutar = conectado.prepareStatement(sql);
//            ejecutar.setInt(1, idPrestamo);
//            resultado = ejecutar.executeQuery();
//
//            while (resultado.next()) {
//                DetallePrestamo detalle = new DetallePrestamo();
//                detalle.setIdDetalle(resultado.getInt("id_detalle"));
//                detalle.setIdPrestamo(resultado.getInt("id_prestamo"));
//                detalle.setIdLibro(resultado.getInt("id_libro"));
//                detalle.setEstadoFisico(resultado.getString("estadoFisico"));
//                detalle.setCantidad(resultado.getInt("cantidad"));
//                lista.add(detalle);
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Error al listar detalles: " + e.getMessage());
//        }
//        return lista;
//    }

}
