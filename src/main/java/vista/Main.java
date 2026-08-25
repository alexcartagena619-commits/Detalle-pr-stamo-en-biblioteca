/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import controlador.ConexionBDD;

/**
 *
 * @author user
 */
public class Main {

    public static void main(String[] args) {
        ConexionBDD c=new ConexionBDD();
        c.conectar();
    }
 }   
