/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.GeneradorPDF;
import modelo.Libro;
import modelo.Usuario;
import vista.RegistrarPrestamo;



/**
 *
 * @author user
 */
public class PrestamoControlador {

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
    private RegistrarPrestamo vista;
    //ID DEL PRESTAMO RECIEN GUARDADO PARA EL PDF
    private int idPrestamoReciente;

    public PrestamoControlador(Usuario usuario, RegistrarPrestamo vista) {
        this.usuario = usuario;
        this.vista = vista;
    }

    public void iniciar() {
        vista.setControlador(this);
        cargarTipoUsuario();
        cargarLibros();
        cargarFecha();
        inicializarTablaDetalle();
        mostrarDias();
        vista.setVisible(true);
    }

    public void inicializarTablaDetalle() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new Object[]{"ID Libro", "Titulo", "Cantidad", "Estado Fisico"});
        vista.getTblDetallePrestamo().setModel(modelo);
    }

    public void cargarTipoUsuario() {
        vista.getCmbTipoUsuario().removeAllItems();
        vista.getCmbTipoUsuario().addItem("Estudiante");
        vista.getCmbTipoUsuario().addItem("Docente");
    }

    public void cargarFecha() {
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        vista.getTxtFecha().setText(hoy.format(formato));
    }

    public void cargarLibros() {
        vista.getCmbLibros().removeAllItems();
        ArrayList<Libro> libros = listarLibros();
        for (Libro l : libros) {
            vista.getCmbLibros().addItem(l.getTitulo());
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

    public void mostrarCantidadDisponible() {
        int indice = vista.getCmbLibros().getSelectedIndex();
        if (indice < 0) {
            vista.getTxtCantidadDisponible().setText("");
            return;
        }

        String titulo = (String) vista.getCmbLibros().getSelectedItem();
        Libro libro = buscarLibroPorTitulo(titulo);
        if (libro != null) {
            vista.getTxtCantidadDisponible().setText(String.valueOf(libro.getStock()));
        }
    }

    public Libro buscarLibroPorTitulo(String titulo) {
        String sql = "SELECT * FROM libro WHERE titulo = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, titulo);
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

    public void buscarUsuario() {
        String tipoSeleccionado = (String) vista.getCmbTipoUsuario().getSelectedItem();
        String cedula = vista.getTxtCedula().getText().trim();

        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el numero de cedula");
            return;
        }

        Usuario encontrado = buscarUsuarioPorCedula(cedula);

        if (encontrado == null) {
            JOptionPane.showMessageDialog(vista, "No es un usuario registrado");
            return;
        }

        String tipoReal = obtenerTipoUsuario(encontrado.getIdUsuario());

        if (tipoSeleccionado != null && tipoReal != null && !tipoSeleccionado.equals(tipoReal)) {
            JOptionPane.showMessageDialog(vista, "El usuario no es un " + tipoSeleccionado + ", es " + tipoReal);
            return;
        }

        usuario = encontrado;
        vista.getTxtNombres().setText(encontrado.getNombres());
        vista.getTxtApellidos().setText(encontrado.getApellidos());
        vista.getTxtTelefono().setText(encontrado.getTelefono());
        vista.getTxtEmail().setText(encontrado.getCorreo());
    }

    public Usuario buscarUsuarioPorCedula(String cedula) {
        String sql = "SELECT * FROM usuario WHERE cedula = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setString(1, cedula);
            resultado = ejecutar.executeQuery();

            if (resultado.next()) {
                Usuario usu = new Usuario();
                usu.setIdUsuario(resultado.getInt("id_usuario"));
                usu.setCedula(resultado.getString("cedula"));
                usu.setNombres(resultado.getString("nombres"));
                usu.setApellidos(resultado.getString("apellidos"));
                usu.setTelefono(resultado.getString("telefono"));
                usu.setCorreo(resultado.getString("correo"));
                return usu;
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    public String obtenerTipoUsuario(int idUsuario) {
        String sql = "SELECT estudiante, docente FROM tipo_usuario WHERE id_usuario = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idUsuario);
            resultado = ejecutar.executeQuery();

            if (resultado.next()) {
                String estudiante = resultado.getString("estudiante");
                String docente = resultado.getString("docente");
                if (estudiante != null && !estudiante.isEmpty()) {
                    return "Estudiante";
                }
                if (docente != null && !docente.isEmpty()) {
                    return "Docente";
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipo de usuario: " + e.getMessage());
        }
        return null;
    }

    public void agregarPrestamo() {
        if (usuario == null) {
            JOptionPane.showMessageDialog(vista, "Primero busque el usuario");
            return;
        }

        int indice = vista.getCmbLibros().getSelectedIndex();
        if (indice < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un libro");
            return;
        }

        String titulo = (String) vista.getCmbLibros().getSelectedItem();
        Libro libro = buscarLibroPorTitulo(titulo);

        int cantidad = (Integer) vista.getSpnCantidad().getValue();
        String estadoFisico = vista.getTxtEstadoFisico().getText().trim();

        if (estadoFisico.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el estado fisico del libro");
            return;
        }

        if (cantidad <= 0 || cantidad > libro.getStock()) {
            JOptionPane.showMessageDialog(vista, "Cantidad invalida, stock disponible: " + libro.getStock());
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) vista.getTblDetallePrestamo().getModel();
        modelo.addRow(new Object[]{libro.getIdLibro(), libro.getTitulo(), cantidad, estadoFisico});
    }

    public void guardarPrestamo() {
        if (usuario == null) {
            JOptionPane.showMessageDialog(vista, "Primero busque el usuario");
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) vista.getTblDetallePrestamo().getModel();
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "Agregue al menos un libro al detalle");
            return;
        }

        int idTipoUsuario = getIdTipoUsuario(usuario.getIdUsuario());
        if (idTipoUsuario <= 0) {
            JOptionPane.showMessageDialog(vista, "No se encontro el tipo de usuario");
            return;
        }

        int idPrestamo = insertarPrestamo(idTipoUsuario);
        if (idPrestamo <= 0) {
            JOptionPane.showMessageDialog(vista, "No se pudo registrar el prestamo");
            return;
        }
        this.idPrestamoReciente = idPrestamo;

        int filas = modelo.getRowCount();

        javax.swing.SwingWorker<Boolean, Integer> worker = new javax.swing.SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() {
                vista.getPrbGuardar().setMinimum(0);
                vista.getPrbGuardar().setMaximum(filas);
                vista.getPrbGuardar().setValue(0);

                boolean todosOk = true;
                for (int i = 0; i < filas; i++) {
                    int idLibro = Integer.parseInt(modelo.getValueAt(i, 0).toString());
                    int cantidad = Integer.parseInt(modelo.getValueAt(i, 2).toString());
                    String estadoFisico = modelo.getValueAt(i, 3).toString();

                    boolean guardado = guardarDetalle(idPrestamo, idLibro, cantidad, estadoFisico);
                    if (!guardado) {
                        todosOk = false;
                    }
                    publish(i + 1);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        System.err.println("Hilo interrumpido: " + ex.getMessage());
                    }
                }
                return todosOk;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                for (Integer valor : chunks) {
                    vista.getPrbGuardar().setValue(valor);
                    vista.getPrbGuardar().setStringPainted(true);
                    vista.getPrbGuardar().setString(valor + "/" + filas);
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(vista, "Prestamo registrado correctamente");
                        modelo.setRowCount(0);
                        vista.getPrbGuardar().setValue(0);
                        vista.getPrbGuardar().setString("");
                    } else {
                        JOptionPane.showMessageDialog(vista, "Hubo un error al guardar el detalle del prestamo");
                    }
                } catch (Exception ex) {
                    System.err.println("Error al guardar: " + ex.getMessage());
                    JOptionPane.showMessageDialog(vista, "Hubo un error al guardar el detalle del prestamo");
                }
            }
        };
        worker.execute();
    }

    public int getIdTipoUsuario(int idUsuario) {
        String sql = "SELECT id_tipo_usuario FROM tipo_usuario WHERE id_usuario = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idUsuario);
            resultado = ejecutar.executeQuery();

            if (resultado.next()) {
                return resultado.getInt("id_tipo_usuario");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener id tipo usuario: " + e.getMessage());
        }
        return -1;
    }

    public int getDiasPrestamo(String tipo) {
        if ("Docente".equals(tipo)) {
            return 10;
        }
        return 5;
    }

    public void mostrarDias() {
        String tipo = (String) vista.getCmbTipoUsuario().getSelectedItem();
        if (tipo != null) {
            vista.getTxtDescuento().setText(getDiasPrestamo(tipo) + " dias");
        }
    }

    public int insertarPrestamo(int idTipoUsuario) {
        try {
            int dias = getDiasPrestamo((String) vista.getCmbTipoUsuario().getSelectedItem());
            CallableStatement cs = conectado.prepareCall("{CALL sp_registrar_prestamo(?, ?, ?)}");
            cs.setInt(1, idTipoUsuario);
            cs.setInt(2, 1);
            cs.setInt(3, dias);
            resultado = cs.executeQuery();
            if (resultado.next()) {
                return resultado.getInt("id_prestamo");
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar prestamo: " + e.getMessage());
        }
        return -1;
    }

    public boolean guardarDetalle(int idPrestamo, int idLibro, int cantidad, String estadoFisico) {
        try {
            CallableStatement cs = conectado.prepareCall("{CALL sp_registrar_detalle_prestamo(?, ?, ?, ?)}");
            cs.setInt(1, idPrestamo);
            cs.setInt(2, idLibro);
            cs.setInt(3, cantidad);
            cs.setString(4, estadoFisico);
            return cs.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar detalle: " + e.getMessage());
            return false;
        }
    }

    public void generarPDF() {
        if (idPrestamoReciente <= 0) {
            JOptionPane.showMessageDialog(vista, "Primero guarde un prestamo");
            return;
        }

        GeneradorPDF generador = new GeneradorPDF();
        String rutaPdf = generador.generarPDF(idPrestamoReciente);

        if (rutaPdf == null) {
            JOptionPane.showMessageDialog(vista, "No se pudo generar el PDF");
            return;
        }

        JOptionPane.showMessageDialog(vista, "PDF generado en: " + rutaPdf);
        try {
            Desktop.getDesktop().open(new File(rutaPdf));
        } catch (IOException ex) {
            System.err.println("No se pudo abrir el PDF: " + ex.getMessage());
        }
    }
}
