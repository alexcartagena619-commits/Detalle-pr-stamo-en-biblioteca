/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author user
 */
public class TipoUsuario {

    private int idTipoUsuario;
    private String estudiante;
    private String docente;
    private int idUsuario;

    public TipoUsuario() {
    }

    public TipoUsuario(int idTipoUsuario, String estudiante, String docente, int idUsuario) {
        this.idTipoUsuario = idTipoUsuario;
        this.estudiante = estudiante;
        this.docente = docente;
        this.idUsuario = idUsuario;
    }

    public int getIdTipoUsuario() {
        return idTipoUsuario;
    }

    public void setIdTipoUsuario(int idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getDocente() {
        return docente;
    }

    public void setDocente(String docente) {
        this.docente = docente;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        if (estudiante != null && !estudiante.isEmpty()) {
            return "Estudiante";
        }
        if (docente != null && !docente.isEmpty()) {
            return "Docente";
        }
        return "Desconocido";
    }
}
