/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Usuario;
import vista.InicioBibliotcario;
import vista.InicioDocente;
import vista.InicioEstudiante;
import vista.Menu;

/**
 *
 * @author user
 */
public class MenuInicialControlador {

    private Menu vista;

    public MenuInicialControlador() {
    }

    public MenuInicialControlador(Menu vista) {
        this.vista = vista;
    }

    public void irInicioEstudiante() {
        InicioEstudiante ie = new InicioEstudiante();
        Usuario modelo = new Usuario();
        UsuarioControlador controlador = new UsuarioControlador(ie, modelo, "Estudiante");
        controlador.iniciar();
        vista.dispose();
    }

    public void irInicioDocente() {
        InicioDocente id = new InicioDocente();
        Usuario modelo = new Usuario();
        UsuarioControlador controlador = new UsuarioControlador(id, modelo, "Docente");
        controlador.iniciar();
        vista.dispose();
    }

    public void irInicioBibliotecario() {
        InicioBibliotcario ib = new InicioBibliotcario();
        Usuario modelo = new Usuario();
        UsuarioControlador controlador = new UsuarioControlador(ib, modelo, "Bibliotecario");
        controlador.iniciar();
        vista.dispose();
    }

    public void iniciar() {
        vista.getBtnEstudiante().addActionListener(e -> irInicioEstudiante());
        vista.getBtnDocente().addActionListener(e -> irInicioDocente());
        vista.getBtnBibliotecario().addActionListener(e -> irInicioBibliotecario());
        vista.setVisible(true);
    }
}