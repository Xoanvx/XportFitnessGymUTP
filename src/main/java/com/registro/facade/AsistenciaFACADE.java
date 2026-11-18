/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.facade;
import com.registro.dao.AsistenciaDAO;
import com.registro.dto.AsistenciaDTO;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Ing
 */
public class AsistenciaFACADE {
    
    private  AsistenciaDAO  dao = new AsistenciaDAO();
    
    public boolean InsertDetRegistroAsistenciasF(AsistenciaDTO asistencia)
    {
        return dao.InsertDetRegistroAsistencias(asistencia);
    }
    
    public List<AsistenciaDTO> ListaDetRegistroAsistenciasXRegistroF(String NroSuscripcion_id, String Cliente_ID)
    {
        return dao.ListaDetRegistroAsistenciasXRegistro(NroSuscripcion_id, Cliente_ID);
    }
    
    public AsistenciaDTO ListaDetRegistroAsistenciasXRegistroXFechaIngF(String NroSuscripcion_id, String Cliente_ID, LocalDate FechaIngreso)
    {
        return dao.ListaDetRegistroAsistenciasXRegistroXFechaIng(NroSuscripcion_id,Cliente_ID,FechaIngreso);
    }
    public boolean InsertaAlertaF(AsistenciaDTO asistencia)
    {
        return dao.InsertaAlerta(asistencia);
    }
    public AsistenciaDTO lista_ClienteXcodF(String Cliente_ID)
    {
        return dao.lista_ClienteXcod(Cliente_ID);
    }
    
    public List<AsistenciaDTO> ListaNroSuscripciones_MembresiaXAlumnoF(String CodEmp, String Cliente_ID)
    {
        return dao.ListaNroSuscripciones_MembresiaXAlumno(CodEmp, Cliente_ID);
    }
    
    public AsistenciaDTO Listasuscripciones_MembresiaXAlumnoXRegistroF(String CodEmp, String Cliente_ID, String NroSuscripcion_id)
    {
        return dao.Listasuscripciones_MembresiaXAlumnoXRegistro(CodEmp,Cliente_ID,NroSuscripcion_id);
    }
    public AsistenciaDTO ListaCantMaximaRegistroAsistenciasXAlumnoF(String CodEmp, String Cliente_ID, String NroSuscripcion_id)
    {
        return dao.ListaCantMaximaRegistroAsistenciasXAlumno(CodEmp,Cliente_ID,NroSuscripcion_id);
    }  
    public List<AsistenciaDTO> lista_Codigoclientes_ActivosF()
    {
        return dao.lista_Codigoclientes_Activos();
    }
}
