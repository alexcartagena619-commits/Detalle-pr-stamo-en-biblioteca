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

//        RegistrarUsuario registrarUsuario = new RegistrarUsuario();
//        Usuario usuario = new Usuario();
//        RegistroUsuarioControlador usuarioControlador = new RegistroUsuarioControlador(usuario, registrarUsuario);
//        usuarioControlador.iniciar();

            MenuVista mv=new MenuVista();
            MenuControlador mc=new MenuControlador(mv);
            mc.iniciar();

    }
}
