/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import controlador.ConexionBDD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Usuario {

    private int idUsuario;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;

    private String nombre;
    private String clave;
    private String cargo;

    //INSTANCIAR LA CONEXIÓN A LA BASE DE DATOS
    ConexionBDD conectar = new ConexionBDD();
    //CLASE QUE ME PERMITA CONECTARME DIRECTAMENTE A MYSQL
    Connection conectado = (Connection) conectar.conectar();
    //OBTENER RESULTADOS DE LA CONSULTA
    ResultSet resultado;

    public Usuario() {
    }

    public Usuario(int idUsuario, String cedula, String nombres, String apellidos, String telefono, String correo) {
        this.idUsuario = idUsuario;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int comprobarCredencialesSp() {
        String sentenciaSQL = "{call sp_validar_login(?, ?, ?, ?)}";

        try (CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL)) {
            ejecutar.setString(1, this.nombre);
            ejecutar.setString(2, this.clave);
            ejecutar.registerOutParameter(3, Types.VARCHAR);
            ejecutar.registerOutParameter(4, Types.BOOLEAN);

            ejecutar.execute();

            boolean acceso = ejecutar.getBoolean(4);
            if (acceso) {
                this.cargo = ejecutar.getString(3);
                return 1;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Error en el conector MySQL JDBC: " + e.getMessage());
        }
        return 0;
    }

    public int obtenerCargoSp() {
        String sentenciaSQL = "{call sp_obtener_cargo(?, ?, ?)}";

        try (CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL)) {
            ejecutar.setString(1, this.nombre);
            ejecutar.registerOutParameter(2, Types.VARCHAR);
            ejecutar.registerOutParameter(3, Types.BOOLEAN);

            ejecutar.execute();

            this.cargo = ejecutar.getString(2);
            boolean tieneClave = ejecutar.getBoolean(3);
            if (tieneClave) {
                return 1;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Error en el conector MySQL JDBC: " + e.getMessage());
        }
        return 0;
    }

    public boolean crearContrasenaSp() {
        String sentenciaSQL = "{call sp_crear_contrasena(?, ?)}";

        try (CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL)) {
            ejecutar.setString(1, this.nombre);
            ejecutar.setString(2, this.clave);
            return ejecutar.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en el conector MySQL JDBC: " + e.getMessage());
        }
        return false;
    }

    public ArrayList<String[]> listarPrestamosPendientes() {
        ArrayList<String[]> lista = new ArrayList<>();
        String sentenciaSQL = "{call sp_prestamos_usuario(?)}";

        try (CallableStatement ejecutar = conectado.prepareCall(sentenciaSQL)) {
            ejecutar.setString(1, this.nombre);
            resultado = ejecutar.executeQuery();
            while (resultado.next()) {
                lista.add(new String[]{
                    resultado.getString("titulo"),
                    resultado.getString("autor"),
                    String.valueOf(resultado.getInt("cantidad")),
                    resultado.getString("estadoFisico"),
                    String.valueOf(resultado.getDate("fecha_limite_devolucion")),
                    String.valueOf(resultado.getInt("dias_restantes"))
                });
            }
        } catch (SQLException e) {
            System.err.println("Error en el conector MySQL JDBC: " + e.getMessage());
        }
        return lista;
    }
}
