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
import modelo.Libro;
import vista.GestionLibros;

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
    private GestionLibros vista;

    public LibroControlador(Libro libro, GestionLibros vista) {
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
        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_registrar_libro(?, ?, ?, ?, ?)}");
            cs.setString(1, libro.getTitulo());
            cs.setString(2, libro.getAutor());
            cs.setString(3, libro.getEditorial());
            cs.setInt(4, libro.getStock());
            cs.setString(5, libro.getDescripcion());

            return cs.executeUpdate() > 0;

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
        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_actualizar_libro(?, ?, ?, ?, ?, ?)}");
            cs.setString(1, libro.getTitulo());
            cs.setString(2, libro.getAutor());
            cs.setString(3, libro.getEditorial());
            cs.setInt(4, libro.getStock());
            cs.setString(5, libro.getDescripcion());
            cs.setInt(6, libro.getIdLibro());

            return cs.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }

    public void ejecutarOpcion() {
        String opcion = (String) vista.getCbmOpciones().getSelectedItem();
        if ("Actualizar".equals(opcion)) {
            actualizarLibroVista();
        } else if ("Eliminar".equals(opcion)) {
            eliminarLibroVista();
        } else {
            guardarLibro();
        }
    }

    public void actualizarLibroVista() {
        String idTexto = vista.getTxtEliminarLibro().getText().trim();
        String titulo = vista.getTxtTituloLibro().getText();
        String autor = vista.getTxtAutor().getText();
        String editorial = vista.getTxtEditorial().getText();
        String cantidad = vista.getTxtCantidad().getText();
        String descripcion = vista.getTxtDescripcion().getText();

        if (idTexto.isEmpty() || titulo.isEmpty() || autor.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar ID, titulo y autor");
            return;
        }

        libro.setIdLibro(Integer.parseInt(idTexto));
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setEditorial(editorial);
        if (!cantidad.isEmpty()) {
            libro.setStock(Integer.parseInt(cantidad));
        }
        libro.setDescripcion(descripcion);

        if (actualizarLibro(libro)) {
            JOptionPane.showMessageDialog(vista, "Libro actualizado correctamente");
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar el libro, verifique el ID");
        }
    }

    public void buscarLibroVista() {
        String idTexto = vista.getTxtEliminarLibro().getText().trim();

        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el ID del libro a buscar");
            return;
        }

        Libro encontrado = buscarLibroPorId(Integer.parseInt(idTexto));

        if (encontrado == null) {
            JOptionPane.showMessageDialog(vista, "No se encontro el libro, verifique el ID");
            return;
        }

        vista.getTxtTituloLibro().setText(encontrado.getTitulo());
        vista.getTxtAutor().setText(encontrado.getAutor());
        vista.getTxtEditorial().setText(encontrado.getEditorial());
        vista.getTxtCantidad().setText(String.valueOf(encontrado.getStock()));
        vista.getTxtDescripcion().setText(encontrado.getDescripcion());
    }

    public Libro buscarLibroPorId(int idLibro) {
        String sql = "SELECT * FROM libro WHERE id_libro = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idLibro);
            resultado = ejecutar.executeQuery();

            if (resultado.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(resultado.getInt("id_libro"));
                libro.setTitulo(resultado.getString("titulo"));
                libro.setAutor(resultado.getString("autor"));
                libro.setEditorial(resultado.getString("editorial"));
                libro.setStock(resultado.getInt("stock"));
                libro.setDescripcion(resultado.getString("descripcion"));
                return libro;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar libro: " + e.getMessage());
        }
        return null;
    }
}
