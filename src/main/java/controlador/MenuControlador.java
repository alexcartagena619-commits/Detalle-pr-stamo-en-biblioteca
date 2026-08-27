/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import vista.MenuVista;
import vista.RegistrarDevolucion;
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
        RegistrarUsuario ru = new RegistrarUsuario();
        ru.setVisible(true);
    }

    public void irDevolucion() {
         RegistrarDevolucion rd = new RegistrarDevolucion();
         rd.setVisible(true);
    }

    public void irRegistrarLibro() {
//         RegistrarLibro rl = new RegistrarLibro();
//         rl.setVisible(true);
    }

    public void iniciar() {
        menu.getBtnPrestamo().addActionListener(e -> irPrestamo());
        menu.getBtnUsuario().addActionListener(e -> irRegistrarUsuario());
        menu.getBtnDevolucion().addActionListener(e -> irDevolucion());
        menu.getBtnLibro().addActionListener(e -> irRegistrarLibro());

        menu.setVisible(true);
    }
}
