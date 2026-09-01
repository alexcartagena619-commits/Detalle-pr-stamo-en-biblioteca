/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import javax.swing.JOptionPane;
import modelo.Usuario;
import vista.InformacionUsuarios;
import vista.LogeoVista;
import vista.MenuVista2;

/**
 *
 * @author user
 */
public class UsuarioControlador {

    private LogeoVista vista;
    private Usuario modelo;
    private String rolEsperado;

    public UsuarioControlador() {
    }

    public UsuarioControlador(LogeoVista vista, Usuario modelo, String rolEsperado) {
        this.vista = vista;
        this.modelo = modelo;
        this.rolEsperado = rolEsperado;
    }

    public void acceder() {
        String user = vista.getTxtUsuario();
        String clave = vista.getPswClave();

        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese el usuario");
            return;
        }

        modelo.setNombre(user);
        modelo.setClave(clave);
        modelo.obtenerCargoSp();

        String cargo = modelo.getCargo();
        if (cargo == null) {
            JOptionPane.showMessageDialog(null, "Usuario no registrado");
            return;
        }

        if (!rolEsperado.equals(cargo)) {
            JOptionPane.showMessageDialog(null, "Acceso no permitido. Debe ingresar por su rol correcto: " + cargo);
            return;
        }

        if ("Estudiante".equals(cargo) || "Docente".equals(cargo)) {
            int tieneClave = modelo.obtenerCargoSp();
            if (tieneClave == 0 && clave.isEmpty()) {
                String nueva = JOptionPane.showInputDialog(null,
                        "No tiene una contraseña, cree una para poder ingresar:");
                if (nueva == null || nueva.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Debe crear una contraseña");
                    return;
                }
                modelo.setClave(nueva.trim());
                if (modelo.crearContrasenaSp()) {
                    JOptionPane.showMessageDialog(null, "Contraseña creada correctamente");
                    abrirInformacion(cargo);
                    return;
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo crear la contraseña");
                    return;
                }
            }
            if (clave.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese su contraseña");
                return;
            }
            if (modelo.comprobarCredencialesSp() == 1) {
                abrirInformacion(cargo);
            } else {
                JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
            }
            return;
        }

        if (clave.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese su contraseña");
            return;
        }
        if (modelo.comprobarCredencialesSp() == 1) {
            MenuVista2 mv = new MenuVista2();
            MenuControlador mc = new MenuControlador(mv);
            mc.iniciar();
            close();
        } else {
            JOptionPane.showMessageDialog(null, "Contraseña incorrecta");
        }
    }

    private void abrirInformacion(String cargo) {
        InformacionUsuarios vistaInfo = new InformacionUsuarios();
        InformacionUsuariosControlador controlador = new InformacionUsuariosControlador(modelo, vistaInfo);
        controlador.iniciar();
        close();
    }

    private void close() {
        if (vista != null && vista instanceof javax.swing.JFrame) {
            ((javax.swing.JFrame) vista).dispose();
        }
    }

    public void iniciar() {
        vista.getBtnIngresar().addActionListener(e -> acceder());
        vista.setVisible(true);
    }
}