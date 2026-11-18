/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.dto;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.sql.Timestamp;
/**
 *
 * @author Ing
 */
public class AsistenciaDTO {
    
    /**
    * InsertDetRegistroAsistencias
    */
    
    private String NroSuscripcion_id;
    private String Cliente_ID;
    private Timestamp FechaHora;
    private LocalDateTime FechaFin;
    private String Turno;
    
    private int IDSecuencia;
    private String Dia;
    private LocalDateTime HoraIngreso;
    private LocalDate FechaIngreso;
    
    /**
    * _InsertaAlerta
    */
    
    private int idAlerta;
    private String Nombre;

    private String RazonSocial;
    private String Direccion;
    private String Telefono;
    private String Telefono2;
    private String Estado;
    private String Correo;
    
    /**
    * lista_ClienteXcod
    */
    
    private String Sexo;
    private String RucDNI;
    private String TipoId;
    private String Foto;
    private String Contacto;
    private String TelefonoCont;
    private byte[] HuellaDactilar;
     
    /**
    * Listasuscripciones_MembresiaXAlumnoXRegistro
    */
    
    private String CodSocio;
    private String SocioCliente;
    private String Membresia;
    private int Duracion;
    private String Frecuencia;
    private double SubTotal;
    private double Descuento;
    private double Total;
    private String Condicion;
    private String Observacion;
    private String CodigoM;
    private int Personal_ID;
    private LocalDate FechaInicio;
    
    /**
    * ListaAperturaCierreCajaAbierto
    */

    private LocalDateTime FechaCaja;
    private String HoraIni;
    private int CodResp;
    private double MontoInicial;
    private double VentaDia;
    private double VentaContado;
    private double VentaCredito;
    private double VentaEfectivo;
    private double VentaDeposito;
    private double TotalPagos;
    
    private double TotalGastos;
    private double TotalCajaParc;
    private double HoraCierre;
    private double EstadoCaja;
    private double EfectivoActual;

    private int CANT;
    
    public AsistenciaDTO()
    {
        
    }
     /**
    * InsertDetRegistroAsistencias
    */
    public String getNroSuscripcion_id(){return NroSuscripcion_id;}
    public void setNroSuscripcion_id(String NroSuscripcion_id){this.NroSuscripcion_id = NroSuscripcion_id;}
    
    public  String getCliente_ID(){return Cliente_ID;}
    public void setCliente_ID(String Cliente_ID){this.Cliente_ID = Cliente_ID;}
    
    public  Timestamp getFechaHora(){return FechaHora;}
    public void setFechaHora(Timestamp FechaHora){this.FechaHora = FechaHora;}
    
    public  LocalDateTime getFechaFin(){return FechaFin;}
    public void setFechaFin(LocalDateTime FechaFin){this.FechaFin = FechaFin;}
    
    public  String getTurno(){return Turno;}
    public void setTurno(String Turno){this.Turno = Turno;}
    
    public  int getIDSecuencia(){return IDSecuencia;}
    public void setIDSecuencia(int IDSecuencia){this.IDSecuencia = IDSecuencia;}
    
    public  String getDia(){return Dia;}
    public void setDia(String Dia){this.Dia = Dia;}
    
    public  LocalDateTime getHoraIngreso(){return HoraIngreso;}
    public void setHoraIngreso(LocalDateTime HoraIngreso){this.HoraIngreso = HoraIngreso;}
    
    public  LocalDate getFechaIngreso(){return FechaIngreso;}
    public void setFechaIngreso(LocalDate FechaIngreso){this.FechaIngreso = FechaIngreso;}
    
    /**
    * _InsertaAlerta
    */

    public  String getNombre(){return Nombre;}
    public void setNombre(String Nombre){this.Nombre = Nombre;}
    
    public int getidAlerta(){return idAlerta;}
    public void setidAlerta(int idAlerta){this.idAlerta = idAlerta;}
    
    public  String getRazonSocial(){return RazonSocial;}
    public void setRazonSocial(String RazonSocial){this.RazonSocial = RazonSocial;}
    
    public  String getDireccion(){return Direccion;}
    public void setDireccion(String Direccion){this.Direccion = Direccion;}
    
    public  String getTelefono(){return Telefono;}
    public void setTelefono(String Telefono){this.Telefono = Telefono;}
    
    public  String getTelefono2(){return Telefono2;}
    public void setTelefono2(String Telefono2){this.Telefono2 = Telefono2;}
    
    public  String getEstado(){return Estado;}
    public void setEstado(String Estado){this.Estado = Estado;}
    
    public  String getCorreo(){return Correo;}
    public void setCorreo(String Correo){this.Correo = Correo;}
    
    
    /**
    * lista_ClienteXcod
    */
    public  String getSexo(){return Sexo;}
    public void setSexo(String Sexo){this.Sexo = Sexo;}
    
    public  String getRucDNI(){return RucDNI;}
    public void setRucDNI(String RucDNI){this.RucDNI = RucDNI;}
    
    public  String getTipoId(){return TipoId;}
    public void setTipoId(String TipoId){this.TipoId = TipoId;}
    
    public  String getFoto(){return Foto;}
    public void setFoto(String Foto){this.Foto = Foto;}

    public  String getContacto(){return Contacto;}
    public void setContacto(String Contacto){this.Contacto = Contacto;}
    
    public  String getTelefonoCont(){return TelefonoCont;}
    public void setTelefonoCont(String TelefonoCont){this.TelefonoCont = TelefonoCont;}
    
    public  byte[] getHuellaDactilar(){return HuellaDactilar;}
    public void setHuellaDactilar(byte[] HuellaDactilar){this.HuellaDactilar = HuellaDactilar;}

    
    /**
    * Listasuscripciones_MembresiaXAlumnoXRegistro
    */
   public  String getCodSocio(){return CodSocio;}
   public void setCodSocio(String CodSocio){this.CodSocio = CodSocio;}
    
   public  String getSocioCliente(){return SocioCliente;}
   public void setSocioCliente(String SocioCliente){this.SocioCliente = SocioCliente;}
    
   public  String getMembresia(){return Membresia;}
   public void setMembresia(String Membresia){this.Membresia = Membresia;}
    
   public  int getDuracion(){return Duracion;}
   public void setDuracion(int Duracion){this.Duracion = Duracion;}

   public  String getFrecuencia(){return Frecuencia;}
   public void setFrecuencia(String Frecuencia){this.Frecuencia = Frecuencia;}
    
   public  double getSubTotal(){return SubTotal;}
   public void setSubTotal(double SubTotal){this.SubTotal = SubTotal;}
    
   public  double getDescuento(){return Descuento;}
   public void setDescuento(double Descuento){this.Descuento = Descuento;}


   public  double getTotal(){return Total;}
   public void setTotal(double Total){this.Total = Total;}
    
   public  String getCondicion(){return Condicion;}
   public void setCondicion(String Condicion){this.Condicion = Condicion;}
    
   public  String getObservacion(){return Observacion;}
   public void setObservacion(String Observacion){this.Observacion = Observacion;}

   public  int getPersonal_ID(){return Personal_ID;}
   public void setPersonal_ID(int Personal_ID){this.Personal_ID = Personal_ID;}
   
   public  String getCodigoM(){return CodigoM;}
   public void setCodigoM(String CodigoM){this.CodigoM = CodigoM;}
   
   public  LocalDate getFechaInicio(){return FechaInicio;}
   public void setFechaInicio(LocalDate FechaInicio){this.FechaInicio = FechaInicio;}
   
    /**
    * ListaAperturaCierreCajaAbierto
    */
    
   public  LocalDateTime getFechaCaja(){return FechaCaja;}
   public void setFechaCaja(LocalDateTime FechaCaja){this.FechaCaja = FechaCaja;}
    
   public  String getHoraIni(){return HoraIni;}
   public void setHoraIni(String HoraIni){this.HoraIni = HoraIni;}
    
   public  int getCodResp(){return CodResp;}
   public void setCodResp(int CodResp){this.CodResp = CodResp;}
    
    
    
   public  double getMontoInicial(){return MontoInicial;}
   public void setMontoInicial(double MontoInicial){this.MontoInicial = MontoInicial;}
    
   public  double getVentaDia(){return VentaDia;}
   public void setVentaDia(double VentaDia){this.VentaDia = VentaDia;}
    
   public  double getVentaContado(){return VentaContado;}
   public void setVentaContado(double VentaContado){this.VentaContado = VentaContado;}

   public  double getVentaCredito(){return VentaCredito;}
   public void setVentaCredito(double VentaCredito){this.VentaCredito = VentaCredito;}
    
   public  double getVentaEfectivo(){return VentaEfectivo;}
   public void setVentaEfectivo(double VentaEfectivo){this.VentaEfectivo = VentaEfectivo;}
    
   public  double getVentaDeposito(){return VentaDeposito;}
   public void setVentaDeposito(double VentaDeposito){this.VentaDeposito = VentaDeposito;}
    
   public  double getTotalPagos(){return TotalPagos;}
   public void setTotalPagos(double TotalPagos){this.TotalPagos = TotalPagos;}
    

   public  double getTotalGastos(){return TotalGastos;}
   public void setTotalGastos(double TotalGastos){this.TotalGastos = TotalGastos;}
    
   public  double getTotalCajaParc(){return TotalCajaParc;}
   public void setTotalCajaParc(double TotalCajaParc){this.TotalCajaParc = TotalCajaParc;}

   public  double getHoraCierre(){return HoraCierre;}
   public void setHoraCierre(double HoraCierre){this.HoraCierre = HoraCierre;}
    
   public  double getEstadoCaja(){return EstadoCaja;}
   public void setEstadoCaja(double EstadoCaja){this.EstadoCaja = EstadoCaja;}
    
   public  double getEfectivoActual(){return EfectivoActual;}
   public void setEfectivoActual(double EfectivoActual){this.EfectivoActual = EfectivoActual;}
    
   public  int getCANT(){return CANT;}
   public void setCANT(int CANT){this.CANT = CANT;}
    
}
