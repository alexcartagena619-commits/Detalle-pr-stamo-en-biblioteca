/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Libro;
import modelo.Usuario;
import vista.MenuVista;
import vista.RegistrarDevolucion;
import vista.RegistrarLibro;
import vista.RegistrarPrestamo;
import vista.RegistrarUsuario;

/**
 *
 * @author user
 */
public class MenuControlador {

    private MenuVista menu;

    public MenuControlador() {
    }

    public MenuControlador(MenuVista menu) {
        this.menu = menu;
    }

    public void irPrestamo() {
        RegistrarPrestamo rp = new RegistrarPrestamo();
        rp.setVisible(true);
    }

    public void irRegistrarUsuario() {
        RegistrarUsuario vista = new RegistrarUsuario();
        Usuario modelo = new Usuario();
        RegistroUsuarioControlador controlador = new RegistroUsuarioControlador(modelo, vista);
        controlador.iniciar();
    }

    public void irDevolucion() {
         RegistrarDevolucion rd = new RegistrarDevolucion();
         rd.setVisible(true);
    }

    public void irRegistrarLibro() {
         RegistrarLibro vista = new RegistrarLibro();
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
