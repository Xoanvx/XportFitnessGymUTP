package com.registro.report;

import com.registro.controller.MembresiaBean;
import com.registro.dto.MembresiaDTO;
import com.registro.dto.UsuarioDTO;
import com.registro.facade.MembresiaFACADE;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

/**
 * Reporte PDF de planes de membresía. Los datos vienen de MembresiaFACADE
 * (sp_ListaMembresia) y se aplica el mismo filtro que la pantalla (?filtro=).
 */
@WebServlet("/reportes/planes-membresia.pdf")
public class ReportePlanesMembresiaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String PLANTILLA = "/reportes/planes-membresia.jrxml";

    /** Plantilla compilada una sola vez (compilar el JRXML es costoso). */
    private transient JasperReport reporte;

    private synchronized JasperReport obtenerReporte() throws Exception {
        if (reporte == null) {
            try (InputStream plantilla = getClass().getResourceAsStream(PLANTILLA)) {
                if (plantilla == null) {
                    throw new IllegalStateException("No se encontró " + PLANTILLA);
                }
                reporte = JasperCompileManager.compileReport(plantilla);
            }
        }
        return reporte;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String filtro = request.getParameter("filtro");

        try {
            List<MembresiaDTO> planes = MembresiaBean.filtrar(new MembresiaFACADE().ListaMembresiaF(), filtro);

            List<Map<String, ?>> filas = new ArrayList<>();
            for (MembresiaDTO p : planes) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("codigo", p.getCodigoM());
                fila.put("nombre", p.getNombreMembresia());
                fila.put("duracion", MembresiaBean.textoDuracion(p));
                fila.put("dias", MembresiaBean.diasEquivalentes(p));
                fila.put("limite", p.getDiasLimite());
                fila.put("tarifa", p.getTarifa());
                fila.put("frecuencia", p.getFrecuencia());
                fila.put("estado", p.getEstado());
                fila.put("activo", MembresiaBean.planActivo(p));
                filas.add(fila);
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("FILTRO", filtro);
            parametros.put("FECHA", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            parametros.put("USUARIO", usuarioSesion(request));
            parametros.put("EMPRESA", "Xport Fitness Gym");

            JasperPrint documento = JasperFillManager.fillReport(
                    obtenerReporte(), parametros, new JRMapCollectionDataSource(filas));
            byte[] pdf = JasperExportManager.exportReportToPdf(documento);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"planes_membresia.pdf\"");
            response.setContentLength(pdf.length);
            response.getOutputStream().write(pdf);

        } catch (Exception e) {
            log("Error al generar el reporte de planes de membresía", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No se pudo generar el reporte de planes de membresía. Revisa el registro de Tomcat.");
            }
        }
    }

    private static String usuarioSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object u = session.getAttribute("usuarioAutenticado");
        if (u instanceof UsuarioDTO dto && dto.getNombres() != null) {
            return (dto.getNombres() + " " + (dto.getApellidoPaterno() == null ? "" : dto.getApellidoPaterno())).trim();
        }
        Object usuario = session.getAttribute("usuario");
        return usuario != null ? usuario.toString() : null;
    }
}
