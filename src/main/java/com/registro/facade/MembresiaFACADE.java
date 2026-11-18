/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.facade;

import java.util.List;
import java.util.ArrayList;
import com.registro.dto.MembresiaDTO;
import com.registro.dao.MembresiaDAO;
/**
 *
 * @author Ing
 */
public class MembresiaFACADE {
    
    private MembresiaDAO dao = new MembresiaDAO();
    
    public ArrayList<MembresiaDTO> Consulta_HistorialMembresiasF()
    {
        return dao.Consulta_HistorialMembresias();
    }
    
    public MembresiaDTO generasuscripciones_MembresiaF(String CodEmp)
    {
        return dao.generasuscripciones_Membresia(CodEmp);
    }
    public MembresiaDTO Listasuscripciones_MembresiaXRegistroF(String CodEmp,String NroSuscripcion_id)
    {
        return dao.Listasuscripciones_MembresiaXRegistro(CodEmp,NroSuscripcion_id);
    }
    
    public boolean InsertSuscripciones_MembresiaF(MembresiaDTO membresias)
    {
        return dao.InsertSuscripciones_Membresia(membresias);
    }
    public boolean Eliminasuscripciones_MembresiaF(MembresiaDTO membresias)
    {
        return dao.Eliminasuscripciones_Membresia(membresias);
    }
    
    public MembresiaDTO ListaDetSuscripcionPeriodoClasesF(String NroSuscripcion_id, String Cliente_ID)
    {
        return dao.ListaDetSuscripcionPeriodoClases(NroSuscripcion_id,Cliente_ID);
    }
    public ArrayList<MembresiaDTO> ListaFrecuenciaF()
    {
        return dao.ListaFrecuencia();
    }
    public ArrayList<MembresiaDTO> ListaMembresiaF()
    {
        return dao.ListaMembresia();
    }
    public MembresiaDTO ListaMembresiaXCodF(String CodigoM)
    {
        return dao.ListaMembresiaXCod(CodigoM);
    }
    public String autogeneraCodMembresiaF()
    {
        return dao.autogeneraCodMembresia();
    }
    public MembresiaDTO Listasuscripciones_MembresiaXCodMembresiaF(String CodEmp, String CodigoM)
    {
        return dao.Listasuscripciones_MembresiaXCodMembresia(CodEmp,CodigoM);
    }
    
    public boolean InsertMembresiaF(MembresiaDTO membresia)
    {
        return dao.InsertMembresia(membresia);
    }
    public boolean UpdateMembresiaF(MembresiaDTO membresia)
    {
        return dao.UpdateMembresia(membresia);
    }
    public boolean DeleteMembresiaF(MembresiaDTO membresia)
    {
        return dao.DeleteMembresia(membresia);
    }
    
    public boolean InsertDiasMembresiaF(MembresiaDTO membresia)
    {
        return dao.InsertDiasMembresia(membresia);
    }
    public boolean DeleteDiasMembresiaF(MembresiaDTO membresia)
    {
        return dao.DeleteDiasMembresia(membresia);
    }
}
