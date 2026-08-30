/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.MenuControlador;

/**
 *
 * @author user
 */
public class Main {

    public static void main(String[] args) {

//        IniciarSesion inicioSesionControlador = new IniciarSesion();
//        inicioSesionControlador.iniciar();

            MenuVista mv=new MenuVista();
            MenuControlador mc=new MenuControlador(mv);
            mc.iniciar();
    }
}
