/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.MenuInicialControlador;

/**
 *
 * @author user
 */
public class Main {

    public static void main(String[] args) {

        Menu vista = new Menu();
        MenuInicialControlador controlador = new MenuInicialControlador(vista);
        controlador.iniciar();
    }
}
