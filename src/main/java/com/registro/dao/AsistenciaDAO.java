/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.dao;

import com.registro.config.ConexionBD;
import com.registro.dto.AsistenciaDTO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ing
 */
public class AsistenciaDAO {

    /** Formato que devuelve CONVERT(CHAR(10), fecha, 101) en SQL Server: MM/dd/yyyy */
    private static final DateTimeFormatter FORMATO_FECHA_101 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    /** Formato que devuelve CONVERT(CHAR(10), fecha, 108) en SQL Server: HH:mm:ss */
    private static final DateTimeFormatter FORMATO_HORA_108 = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * InsertDetRegistroAsistencias
     * El procedimiento usa SET NOCOUNT ON y devuelve RETURN 0 si todo salió bien,
     * por eso se lee el código de retorno en lugar de las filas afectadas.
     */
    public boolean InsertDetRegistroAsistencias(AsistenciaDTO asistencia) {
        boolean ResultadoExitoso = false;
        String sql = "{? = call sp_InsertDetRegistroAsistencias(?,?,?,?,?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setString(2, asistencia.getNroSuscripcion_id());
            cs.setString(3, asistencia.getCliente_ID());

            // @FechaHora -> columna HoraIngreso
            if (asistencia.getFechaHora() != null) {
                cs.setTimestamp(4, asistencia.getFechaHora());
            } else {
                cs.setNull(4, Types.TIMESTAMP);
            }

            // @FechaFin -> columna FechaIngreso
            if (asistencia.getFechaFin() != null) {
                cs.setTimestamp(5, Timestamp.valueOf(asistencia.getFechaFin()));
            } else {
                cs.setNull(5, Types.TIMESTAMP);
            }

            cs.setString(6, asistencia.getTurno());

            cs.execute();
            ResultadoExitoso = cs.getInt(1) == 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ResultadoExitoso;
    }

    /**
     * ListaDetRegistroAsistenciasXRegistro
     * Devuelve todas las asistencias de la suscripción. El procedimiento entrega
     * HoraIngreso y FechaIngreso como texto (CONVERT 108 y 101), por eso se parsean.
     */
    public List<AsistenciaDTO> ListaDetRegistroAsistenciasXRegistro(String NroSuscripcion_id, String Cliente_ID) {
        List<AsistenciaDTO> lista = new ArrayList<>();
        String sql = "{call sp_ListaDetRegistroAsistenciasXRegistro(?,?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql)) {

            st.setString(1, NroSuscripcion_id);
            st.setString(2, Cliente_ID);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    AsistenciaDTO det = new AsistenciaDTO();
                    det.setDia(rs.getString("Dia"));
                    det.setIDSecuencia(rs.getInt("Dia"));

                    LocalDate fecha = parsearFecha(rs.getString("FechaIngreso"));
                    LocalTime hora = parsearHora(rs.getString("HoraIngreso"));
                    det.setFechaIngreso(fecha);
                    if (fecha != null && hora != null) {
                        det.setHoraIngreso(fecha.atTime(hora));
                        det.setFechaHora(Timestamp.valueOf(fecha.atTime(hora)));
                    }
                    lista.add(det);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    /**
     * ListaDetRegistroAsistenciasXRegistroXFechaIng
     */
    public AsistenciaDTO ListaDetRegistroAsistenciasXRegistroXFechaIng(String NroSuscripcion_id, String Cliente_ID, LocalDate FechaIngreso) {
        AsistenciaDTO ListaDet = null;
        String sql = "{call sp_ListaDetRegistroAsistenciasXRegistroXFechaIng(?,?,?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql)) {

            st.setString(1, NroSuscripcion_id);
            st.setString(2, Cliente_ID);
            if (FechaIngreso != null) {
                st.setDate(3, Date.valueOf(FechaIngreso));
            } else {
                st.setNull(3, Types.DATE);
            }

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    ListaDet = new AsistenciaDTO();
                    ListaDet.setNroSuscripcion_id(rs.getString("NroSuscripcion_id"));
                    ListaDet.setCliente_ID(rs.getString("Cliente_ID"));
                    ListaDet.setIDSecuencia(rs.getInt("IDSecuencia"));
                    ListaDet.setHoraIngreso(aLocalDateTime(rs.getTimestamp("HoraIngreso")));
                    ListaDet.setFechaIngreso(aLocalDate(rs.getDate("FechaIngreso")));
                    ListaDet.setTurno(rs.getString("Turno"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ListaDet;
    }

    /**
     * InsertaAlerta
     * Igual que el insert de asistencias: SET NOCOUNT ON + RETURN 0.
     */
    public boolean InsertaAlerta(AsistenciaDTO asistencia) {
        boolean ResultadoExitoso = false;
        String sql = "{? = call sp_InsertaAlerta(?,?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, asistencia.getidAlerta());
            cs.setString(3, asistencia.getNombre());

            cs.execute();
            ResultadoExitoso = cs.getInt(1) == 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ResultadoExitoso;
    }

    /**
     * lista_ClienteXcod
     */
    public AsistenciaDTO lista_ClienteXcod(String Cliente_ID) {
        AsistenciaDTO lista_Cliente = null;
        String sql = "{call sp_lista_ClienteXcod(?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql)) {

            st.setString(1, Cliente_ID);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    lista_Cliente = new AsistenciaDTO();
                    lista_Cliente.setCliente_ID(rs.getString("Cliente_ID"));
                    lista_Cliente.setRazonSocial(rs.getString("RazonSocial"));
                    lista_Cliente.setDireccion(rs.getString("Direccion"));
                    lista_Cliente.setTelefono(rs.getString("Telefono"));
                    lista_Cliente.setEstado(rs.getString("Estado"));
                    lista_Cliente.setTelefono2(rs.getString("Telefono2"));
                    lista_Cliente.setCorreo(rs.getString("Correo"));
                    lista_Cliente.setSexo(rs.getString("Sexo"));
                    lista_Cliente.setRucDNI(rs.getString("RucDNI"));
                    lista_Cliente.setTipoId(rs.getString("TipoId"));
                    lista_Cliente.setFoto(rs.getString("Foto"));
                    lista_Cliente.setContacto(rs.getString("Contacto"));
                    lista_Cliente.setTelefonoCont(rs.getString("TelefonoCont"));
                    lista_Cliente.setHuellaDactilar(rs.getBytes("HuellaDactilar"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista_Cliente;
    }

    /**
     * ListaNroSuscripciones_MembresiaXAlumno
     * Un alumno puede tener varias suscripciones activas, por eso devuelve lista.
     */
    public List<AsistenciaDTO> ListaNroSuscripciones_MembresiaXAlumno(String CodEmp, String Cliente_ID) {
        List<AsistenciaDTO> lista = new ArrayList<>();
        String sql = "{call sp_ListaNroSuscripciones_MembresiaXAlumno(?,?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql)) {

            st.setString(1, CodEmp);
            st.setString(2, Cliente_ID);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    AsistenciaDTO item = new AsistenciaDTO();
                    item.setNroSuscripcion_id(rs.getString("NroSuscripcion_id"));
                    item.setCliente_ID(Cliente_ID);
                    lista.add(item);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    /**
     * Listasuscripciones_MembresiaXAlumnoXRegistro
     */
    public AsistenciaDTO Listasuscripciones_MembresiaXAlumnoXRegistro(String CodEmp, String Cliente_ID, String NroSuscripcion_id) {
        AsistenciaDTO listar = null;
        String sql = "{call sp_Listasuscripciones_MembresiaXAlumnoXRegistro(?,?,?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql)) {

            st.setString(1, CodEmp);
            st.setString(2, Cliente_ID);
            st.setString(3, NroSuscripcion_id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    listar = new AsistenciaDTO();
                    listar.setNroSuscripcion_id(rs.getString("NroSuscripcion_id"));
                    listar.setCodSocio(rs.getString("CodSocio"));
                    listar.setSocioCliente(rs.getString("SocioCliente"));
                    listar.setCodigoM(rs.getString("CodigoM"));
                    listar.setMembresia(rs.getString("Membresia"));
                    listar.setDuracion(rs.getInt("Duracion"));
                    listar.setFrecuencia(rs.getString("Frecuencia"));
                    listar.setFechaInicio(aLocalDate(rs.getDate("FechaInicio")));
                    listar.setFechaFin(aLocalDateTime(rs.getTimestamp("FechaFin")));
                    listar.setPersonal_ID(rs.getInt("Personal_ID"));
                    listar.setSubTotal(rs.getDouble("SubTotal"));
                    listar.setDescuento(rs.getDouble("Descuento"));
                    listar.setTotal(rs.getDouble("Total"));
                    listar.setEstado(rs.getString("Estado"));
                    listar.setCondicion(rs.getString("Condicion"));
                    listar.setObservacion(rs.getString("Observacion"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listar;
    }

    /**
     * ListaCantMaximaRegistroAsistenciasXAlumno
     */
    public AsistenciaDTO ListaCantMaximaRegistroAsistenciasXAlumno(String CodEmp, String Cliente_ID, String NroSuscripcion_id) {
        AsistenciaDTO listar = null;
        String sql = "{call sp_ListaCantMaximaRegistroAsistenciasXAlumno(?,?,?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql)) {

            st.setString(1, CodEmp);
            st.setString(2, Cliente_ID);
            st.setString(3, NroSuscripcion_id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    listar = new AsistenciaDTO();
                    listar.setDia(rs.getString("Dias"));
                    listar.setCANT(rs.getInt("CANT"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listar;
    }

    /**
     * lista_Codigoclientes_Activos
     * Devuelve todos los clientes activos (antes solo se leía la primera fila).
     */
    public List<AsistenciaDTO> lista_Codigoclientes_Activos() {
        List<AsistenciaDTO> lista = new ArrayList<>();
        String sql = "{call sp_lista_Codigoclientes_Activos}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                AsistenciaDTO item = new AsistenciaDTO();
                item.setCliente_ID(rs.getString("Cliente_ID"));
                lista.add(item);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // ---------------------------------------------------------------------
    // Utilidades de conversión de fechas
    // ---------------------------------------------------------------------

    private static LocalDate aLocalDate(Date fecha) {
        return fecha != null ? fecha.toLocalDate() : null;
    }

    private static LocalDateTime aLocalDateTime(Timestamp fechaHora) {
        return fechaHora != null ? fechaHora.toLocalDateTime() : null;
    }

    private static LocalDate parsearFecha(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim(), FORMATO_FECHA_101);
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static LocalTime parsearHora(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(texto.trim(), FORMATO_HORA_108);
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
