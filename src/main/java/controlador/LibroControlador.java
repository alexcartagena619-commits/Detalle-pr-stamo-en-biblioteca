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
import modelo.Libro;

/**
 *
 * @author user
 */
public class LibroControlador {

    //INSTANCIAR LA CONEXIÓN A LA BASE DE DATOS
    ConexionBDD conectar = new ConexionBDD();
    //CLASE QUE ME PERMITA CONECTARME DIRECTAMENTE A MYSQL
    Connection conectado = (Connection) conectar.conectar();
    //CLASE QUE ME PERMITE EJECUTAR MI SENTENCIA SQL
    PreparedStatement ejecutar;
    //OBTENER RESULTADOS DE LA CONSULTA
    ResultSet resultado;

    public boolean guardarLibro(Libro libro) {
        String sql = "INSERT INTO libro (titulo, autor, editorial, stock, descripcion) VALUES (?, ?, ?, ?, ?)";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, libro.getTitulo());
            ejecutar.setString(2, libro.getAutor());
            ejecutar.setString(3, libro.getEditorial());
            ejecutar.setInt(4, libro.getStock());
            ejecutar.setString(5, libro.getDescripcion());

            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar libro: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Libro> listarLibros() {
        ArrayList<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libro";

        try {
            ejecutar = conectado.prepareStatement(sql);
            resultado = ejecutar.executeQuery();

            while (resultado.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(resultado.getInt("id_libro"));
                libro.setTitulo(resultado.getString("titulo"));
                libro.setAutor(resultado.getString("autor"));
                libro.setEditorial(resultado.getString("editorial"));
                libro.setStock(resultado.getInt("stock"));
                libro.setDescripcion(resultado.getString("descripcion"));
                lista.add(libro);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar libros: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminarLibro(int idLibro) {
        String sql = "DELETE FROM libro WHERE id_libro = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idLibro);
            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarLibro(Libro libro) {
        String sql = "UPDATE libro SET titulo=?, autor=?, editorial=?, stock=?, descripcion=? WHERE id_libro=?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, libro.getTitulo());
            ejecutar.setString(2, libro.getAutor());
            ejecutar.setString(3, libro.getEditorial());
            ejecutar.setInt(4, libro.getStock());
            ejecutar.setString(5, libro.getDescripcion());
            ejecutar.setInt(6, libro.getIdLibro());

            return ejecutar.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }
}
