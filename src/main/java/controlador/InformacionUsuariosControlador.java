/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import modelo.Usuario;
import vista.InformacionUsuarios;

/**
 *
 * @author user
 */
public class InformacionUsuariosControlador {

    private InformacionUsuarios vista;
    private Usuario modelo;

    public InformacionUsuariosControlador() {
    }

    public InformacionUsuariosControlador(Usuario modelo, InformacionUsuarios vista) {
        this.vista = vista;
        this.modelo = modelo;
    }

    public void cargarPrestamos() {
        DefaultTableModel modeloLogin = new DefaultTableModel();
        modeloLogin.addColumn("Cedula");
        modeloLogin.addColumn("Libro");
        modeloLogin.addColumn("Autor");
        modeloLogin.addColumn("Cantidad");
        modeloLogin.addColumn("Estado Fisico");
        modeloLogin.addColumn("Fecha Limite");
        modeloLogin.addColumn("Dias Restantes");
        modeloLogin.addColumn("Libros por Devolver");

        ArrayList<String[]> prestamos = modelo.listarPrestamosPendientes();
        for (String[] fila : prestamos) {
            modeloLogin.addRow(new Object[]{
                modelo.getNombre(),
                fila[0],
                fila[1],
                fila[2],
                fila[3],
                fila[4],
                fila[5],
                prestamos.size()
            });
        }

        vista.getTblInformacion().setModel(modeloLogin);
        vista.getLblTitulo().setText("PRESTAMOS PENDIENTES - USUARIO: " + modelo.getNombre());
    }

    public void salir() {
        vista.dispose();
    }

    public void iniciar() {
        cargarPrestamos();
        vista.getBtnSalir().addActionListener(e -> salir());
        vista.setVisible(true);
    }
}