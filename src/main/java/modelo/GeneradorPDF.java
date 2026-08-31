/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import controlador.ConexionBDD;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 *
 * @author user
 */
public class GeneradorPDF {

    ConexionBDD conectar = new ConexionBDD();
    Connection conectado = (Connection) conectar.conectar();
    PreparedStatement ejecutar;
    ResultSet resultado;

    public String generarPDF(int idPrestamo) {
        if (idPrestamo <= 0) {
            return null;
        }

        String titulo = "";
        String fecha = "";
        String hora = "";
        String limite = "";
        String estado = "";
        String tipoUsuario = "";
        String usNombre = "";
        String usCedula = "";
        String bNombre = "";
        String bCedula = "";

        String sql = "SELECT p.fecha_prestao, p.hora_prestao, p.fecha_limite_devolucion, p.estado, "
                + "u.nombres, u.apellidos, u.cedula, tu.estudiante, tu.docente, "
                + "b.nombres AS b_nombres, b.apellidos AS b_apellidos, b.cedula AS b_cedula "
                + "FROM prestamo p "
                + "JOIN tipo_usuario tu ON p.id_tipo_usuario = tu.id_tipo_usuario "
                + "JOIN usuario u ON tu.id_usuario = u.id_usuario "
                + "JOIN bibliotecario b ON p.id_blibliotecario = b.id_blibliotecario "
                + "WHERE p.id_prestamo = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idPrestamo);
            resultado = ejecutar.executeQuery();

            if (resultado.next()) {
                titulo = "DETALLE DE PRESTAMO #" + idPrestamo;
                fecha = String.valueOf(resultado.getDate("fecha_prestao"));
                hora = String.valueOf(resultado.getTime("hora_prestao"));
                limite = String.valueOf(resultado.getDate("fecha_limite_devolucion"));
                estado = resultado.getString("estado");
                usNombre = resultado.getString("nombres") + " " + resultado.getString("apellidos");
                usCedula = resultado.getString("cedula");
                String estudiante = resultado.getString("estudiante");
                String docente = resultado.getString("docente");
                tipoUsuario = (estudiante != null && !estudiante.isEmpty()) ? "Estudiante" : "Docente";
                bNombre = resultado.getString("b_nombres") + " " + resultado.getString("b_apellidos");
                bCedula = resultado.getString("b_cedula");
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar prestamo: " + e.getMessage());
            return null;
        }

        try {
            String rutaPdf = System.getProperty("user.home") + File.separator + "Desktop"
                    + File.separator + "FacturaPrestamo_" + idPrestamo + ".pdf";
            Document documento = new Document(PageSize.A4);
            PdfWriter.getInstance(documento, new FileOutputStream(rutaPdf));
            documento.open();

            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font cabeceraFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            Paragraph parrafoTitulo = new Paragraph(titulo, tituloFont);
            parrafoTitulo.setAlignment(Paragraph.ALIGN_CENTER);
            documento.add(parrafoTitulo);
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph("Fecha: " + fecha, normalFont));
            documento.add(new Paragraph("Hora: " + hora, normalFont));
            documento.add(new Paragraph("Fecha limite de devolucion: " + limite, normalFont));
            documento.add(new Paragraph("Estado: " + estado, normalFont));
            documento.add(new Paragraph("Tipo de usuario: " + tipoUsuario, normalFont));
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph("DATOS DEL BIBLIOTECARIO", cabeceraFont));
            documento.add(new Paragraph("Nombre: " + bNombre, normalFont));
            documento.add(new Paragraph("Cedula: " + bCedula, normalFont));
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph("DATOS DEL USUARIO", cabeceraFont));
            documento.add(new Paragraph("Nombre: " + usNombre, normalFont));
            documento.add(new Paragraph("Cedula: " + usCedula, normalFont));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(new float[]{4f, 2f, 2f, 3f});
            tabla.setWidthPercentage(100);
            tabla.setHeaderRows(1);
            tabla.addCell(new PdfPCell(new Phrase("Libro", cabeceraFont)));
            tabla.addCell(new PdfPCell(new Phrase("Cantidad", cabeceraFont)));
            tabla.addCell(new PdfPCell(new Phrase("Estado", cabeceraFont)));
            tabla.addCell(new PdfPCell(new Phrase("Autor", cabeceraFont)));

            ArrayList<String[]> detalle = obtenerDetallePDF(idPrestamo);
            for (String[] fila : detalle) {
                tabla.addCell(new PdfPCell(new Phrase(fila[0], normalFont)));
                tabla.addCell(new PdfPCell(new Phrase(fila[1], normalFont)));
                tabla.addCell(new PdfPCell(new Phrase(fila[2], normalFont)));
                tabla.addCell(new PdfPCell(new Phrase(fila[3], normalFont)));
            }

            documento.add(tabla);
            documento.close();

            return rutaPdf;

        } catch (DocumentException | IOException e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<String[]> obtenerDetallePDF(int idPrestamo) {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT l.titulo, l.autor, dp.cantidad, dp.estadoFisico "
                + "FROM detalle_prestamo dp JOIN libro l ON dp.id_libro = l.id_libro "
                + "WHERE dp.id_prestamo = ?";

        try {
            ejecutar = conectado.prepareStatement(sql);
            ejecutar.setInt(1, idPrestamo);
            resultado = ejecutar.executeQuery();
            while (resultado.next()) {
                lista.add(new String[]{
                    resultado.getString("titulo"),
                    String.valueOf(resultado.getInt("cantidad")),
                    resultado.getString("estadoFisico"),
                    resultado.getString("autor")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalle para PDF: " + e.getMessage());
        }
        return lista;
    }
}
