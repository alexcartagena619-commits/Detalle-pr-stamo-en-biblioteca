/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Libro;
import modelo.Usuario;
import vista.MenuVista2;
import vista.RegistrarDevolucion;
import vista.GestionLibros;
import vista.RegistrarPrestamo;
import vista.GestionUsuarios;

/**
 *
 * @author user
 */
public class MenuControlador {

    private MenuVista2 menu;

    public MenuControlador() {
    }

    public MenuControlador(MenuVista2 menu) {
        this.menu = menu;
    }

    public void irPrestamo() {
        RegistrarPrestamo rp = new RegistrarPrestamo();
        Usuario modelo = new Usuario();
        PrestamoControlador controlador = new PrestamoControlador(modelo, rp);
        controlador.iniciar();
    }

    public void irRegistrarUsuario() {
        GestionUsuarios vista = new GestionUsuarios();
        Usuario modelo = new Usuario();
        RegistroUsuarioControlador controlador = new RegistroUsuarioControlador(modelo, vista);
        controlador.iniciar();
    }

    public void irDevolucion() {
         RegistrarDevolucion rd = new RegistrarDevolucion();
         Usuario modelo = new Usuario();
         DevolucionControlador controlador = new DevolucionControlador(modelo, rd);
         controlador.iniciar();
    }

    public void irRegistrarLibro() {
         GestionLibros vista = new GestionLibros();
         Libro modelo = new Libro();
         LibroControlador controlador = new LibroControlador(modelo, vista);
         controlador.iniciar();
    }

    public void iniciar() {
        menu.getBtnPrestamo().addActionListener(e -> irPrestamo());
        menu.getBtnUsuario().addActionListener(e -> irRegistrarUsuario());
        menu.getBtnDevolucion().addActionListener(e -> irDevolucion());
        menu.getBtnLibro().addActionListener(e -> irRegistrarLibro());

        menu.setVisible(true);
    }
}
