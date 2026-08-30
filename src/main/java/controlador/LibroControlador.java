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
import modelo.Libro;
import vista.RegistrarLibro;

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

    //ATRIBUTO DEL MODELO
    private Libro libro;
    //ATRIBUTO DE LA VISTA
    private RegistrarLibro vista;

    public LibroControlador(Libro libro, RegistrarLibro vista) {
        this.libro = libro;
        this.vista = vista;
    }

    public void iniciar() {
        vista.setControlador(this);
        vista.setVisible(true);
        cargarTabla();
    }

    public void cargarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID", "Titulo", "Autor", "Editorial", "Stock", "Descripcion"});
        ArrayList<Libro> libros = listarLibros();
        for (Libro l : libros) {
            modelo.addRow(new Object[]{l.getIdLibro(), l.getTitulo(), l.getAutor(), l.getEditorial(), l.getStock(), l.getDescripcion()});
        }
        vista.getTblLibros().setModel(modelo);
    }

    public void limpiarCampos() {
        vista.getTxtTituloLibro().setText("");
        vista.getTxtAutor().setText("");
        vista.getTxtEditorial().setText("");
        vista.getTxtCantidad().setText("");
        vista.getTxtDescripcion().setText("");
    }

    public void guardarLibro() {
        String titulo = vista.getTxtTituloLibro().getText();
        String autor = vista.getTxtAutor().getText();
        String editorial = vista.getTxtEditorial().getText();
        String cantidad = vista.getTxtCantidad().getText();
        String descripcion = vista.getTxtDescripcion().getText();

        if (titulo.isEmpty() || autor.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar titulo y autor");
            return;
        }

        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setEditorial(editorial);
        if (!cantidad.isEmpty()) {
            libro.setStock(Integer.parseInt(cantidad));
        }
        libro.setDescripcion(descripcion);

        if (guardarLibro(libro)) {
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo guardar el libro");
        }
    }

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

    public void eliminarLibroVista() {
        String idTexto = vista.getTxtEliminarLibro().getText().trim();

        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el ID del libro a eliminar");
            return;
        }

        int idLibro = Integer.parseInt(idTexto);

        if (eliminarLibro(idLibro)) {
            JOptionPane.showMessageDialog(vista, "Libro eliminado correctamente");
            vista.getTxtEliminarLibro().setText("");
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar el libro, verifique el ID");
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
