/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.dto;

import java.sql.*;
import java.time.LocalDateTime;
/**
 *
 * @author Ing
 */
public class MembresiaDTO {
    
    private String CodigoM;
    private String NombreMembresia;
    private int Duracion_Dias;
    private int Duracion_Semanas;
    private int Duracion_Meses;
    private int Duracion_Anios;
    private int DiasLimite;
    private String Frecuencia;
    private double Tarifa;
    private String Estado;
    private int idTarifa;

    // ---------------------------------------------------------
    // CAMPOS EXTRAS DEL SP HISTORIAL (Para Consultas)
    // ---------------------------------------------------------
    private String registro;
    private String pedido;
    private String documento;
    private String cliente;
    private String membresia; // (Guarda el nombre calculado del SP)
    private Date fechaInicio;
    private Date fechaFin;
    private int dias;
    private String vigente;
    private double total;
    private String condicion;
    private String vendedor;
    
    // ---------------------------------------------------------
    // VARIABLES DE FILTRO DE BUSQUEDA
    // ---------------------------------------------------------
    
    private String CodEmp;
    private LocalDateTime Desde;
    private LocalDateTime Hasta;
    private String Filtro;
    
     // ---------------------------------------------------------
    // VARIABLES DE FILTRO DE SUSCRIPCION DE MEMBRESIA
    // ---------------------------------------------------------
    
    private String NroSuscripcion_id;
    private String cliente_id;
    private int Personal_ID;
    private double SubTotal;
    private double Descuento;
    private double TotalSusc;
    private String EstadoSusc;
    private String CondicionSusc;
    private String ObservacionSusc;
    
     // ---------------------------------------------------------
    // VARIABLES DE DETALLE DE SUSCRIPCION
    // ---------------------------------------------------------
    
    private String Asistio;
    private String Rutina;
    private int IDSecuencia;
    
    // ---------------------------------------------------------
    // VARIABLES DE FRECUENCIAS
    // ---------------------------------------------------------
    
    private int IDFrecuencia;
    
    // ---------------------------------------------------------
    // VARIABLES DIAS MEMBRESIA
    // ---------------------------------------------------------
    
    private String CodDia;
    private String Dia;
    
    public MembresiaDTO(){
    
    }
    
    // ---------------------------------------------------------
    // GETTERS Y SETTERS
    // ---------------------------------------------------------
    public String getCodigoM() { return CodigoM; }
    public void setCodigoM(String CodigoM) { this.CodigoM = CodigoM; }

    public String getNombreMembresia() { return NombreMembresia; }
    public void setNombreMembresia(String NombreMembresia) { this.NombreMembresia = NombreMembresia; }

    public int getDuracionDias() { return Duracion_Dias; }
    public void setDuracionDias(int Duracion_Dias) { this.Duracion_Dias = Duracion_Dias; }

    public int getDuracionSemanas() { return Duracion_Semanas; }
    public void setDuracionSemanas(int Duracion_Semanas) { this.Duracion_Semanas = Duracion_Semanas; }

    public int getDuracionMeses() { return Duracion_Meses; }
    public void setDuracionMeses(int Duracion_Meses) { this.Duracion_Meses = Duracion_Meses; }

    public int getDuracionAnios() { return Duracion_Anios; }
    public void setDuracionAnios(int Duracion_Anios) { this.Duracion_Anios = Duracion_Anios; }

    public int getDiasLimite() { return DiasLimite; }
    public void setDiasLimite(int DiasLimite) { this.DiasLimite = DiasLimite; }

    public String getFrecuencia() { return Frecuencia; }
    public void setFrecuencia(String Frecuencia) { this.Frecuencia = Frecuencia; }

    public double getTarifa() { return Tarifa; }
    public void setTarifa(double Tarifa) { this.Tarifa = Tarifa; }

    public String getEstado() { return Estado; }
    public void setEstado(String Estado) { this.Estado = Estado; }

    public int getIdTarifa() { return idTarifa; }
    public void setIdTarifa(int idTarifa) { this.idTarifa = idTarifa; }

    // Getters y Setters de los campos extras
    public String getRegistro() { return registro; }
    public void setRegistro(String registro) { this.registro = registro; }

    public String getPedido() { return pedido; }
    public void setPedido(String pedido) { this.pedido = pedido; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getMembresia() { return membresia; }
    public void setMembresia(String membresia) { this.membresia = membresia; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public int getDias() { return dias; }
    public void setDias(int dias) { this.dias = dias; }

    public String getVigente() { return vigente; }
    public void setVigente(String vigente) { this.vigente = vigente; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getCondicion() { return condicion; }
    public void setCondicion(String condicion) { this.condicion = condicion; }

    public String getVendedor() { return vendedor; }
    public void setVendedor(String vendedor) { this.vendedor = vendedor; }

    public String getCodEmp(){return CodEmp;}
    public void setCodEmp(String CodEmp){this.CodEmp = CodEmp;}
    
    public LocalDateTime getDesde(){return Desde;}
    public void setDesde(LocalDateTime Desde){this.Desde = Desde;}
    
    public LocalDateTime getHasta(){return Hasta;}
    public void setHasta(LocalDateTime Hasta){this.Hasta = Hasta;}
    
    public String getFiltro(){return Filtro;}
    public void setFiltro(String Filtro){this.Filtro = Filtro;}
    
    
    
    public String getNroSuscripcion_id(){return NroSuscripcion_id;}
    public void setNroSuscripcion_id(String NroSuscripcion_id){this.NroSuscripcion_id = NroSuscripcion_id;}
    
    public String getcliente_id(){return cliente_id;}
    public void setcliente_id(String cliente_id){this.cliente_id = cliente_id;}
    
    public int getPersonal_ID(){return Personal_ID;}
    public void setPersonal_ID(int Personal_ID){this.Personal_ID = Personal_ID;}
    
    public double getSubTotal(){return SubTotal;}
    public void setSubTotal(double SubTotal){this.SubTotal = SubTotal;}
    
    public double getDescuento(){return Descuento;}
    public void setDescuento(double Descuento){this.Descuento = Descuento;}
    
    public double getTotalSusc(){return TotalSusc;}
    public void setTotalSusc(double TotalSusc){this.TotalSusc = TotalSusc;}
    
    public String getEstadoSusc(){return EstadoSusc;}
    public void setEstadoSusc(String EstadoSusc){this.EstadoSusc = EstadoSusc;}
    
    public String getCondicionSusc(){return CondicionSusc;}
    public void setCondicionSusc(String CondicionSusc){this.CondicionSusc = CondicionSusc;}
    
    public String getObservacionSusc(){return ObservacionSusc;}
    public void setObservacionSusc(String ObservacionSusc){this.ObservacionSusc = ObservacionSusc;}
    
    public String getRutina(){return Rutina;}
    public void setRutina(String Rutina){this.Rutina = Rutina;}
    
    public String getAsistio(){return Asistio;}
    public void setAsistio(String Asistio){this.Asistio = Asistio;}
    
    public int getIDSecuencia(){return IDSecuencia;}
    public void setIDSecuencia(int IDSecuencia){this.IDSecuencia = IDSecuencia;}
    
    public int getIDFrecuencia(){return IDFrecuencia;}
    public void setIDFrecuencia(int IDFrecuencia){this.IDFrecuencia = IDFrecuencia;}
    
    public String getCodDia(){return CodDia;}
    public void setCodDia(String CodDia){this.CodDia = CodDia;}
    
    public String getDia(){return Dia;}
    public void setDia(String Dia){this.Dia = Dia;}
}
