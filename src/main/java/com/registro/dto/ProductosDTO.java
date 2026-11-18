/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.dto;

/**
 *
 * @author Ing
 */
public class ProductosDTO implements java.io.Serializable {
    
 private String Producto_ID;      
 private String NombreProducto;   
 private String EstadoProducto;     
 private int Categoria_ID;      
 private int MarcaProducto_ID;   
 private String Descripcion;        
 private String UnidadMedida;     
 private double StockFinal;     
 private double PrecioCosto;      
 private double PrecioVenta;  
 private double PrecioLimite;  
 private double MargenUtil;    
 private double Peso;
 private String Imagen;  
 private String CodigoBarra;   
 private String CodEmp; 
 private String TipoProd;
 private String TipoBien_ID;
 
 private String DescripcionTipo;
 private String EstadoTipo;

 // Nombres que devuelven los listados (sp_Lista_ProductosGral_Activos y búsquedas)
 private String Categoria;
 private String Marca;

 public ProductosDTO(){
     
 }
 
 public ProductosDTO(String Producto_ID,String NombreProducto,String EstadoProducto,int Categoria_ID,int MarcaProducto_ID,String Descripcion,String UnidadMedida,double StockFinal,double PrecioCosto,double PrecioVenta,double PrecioLimite,double MargenUtil,double Peso,String Imagen,String CodigoBarra,String CodEmp, String TipoProd,String TipoBien_ID, String DescripcionTipo, String EstadoTipo)
 {
     this.Producto_ID = Producto_ID;
     this.NombreProducto = NombreProducto;
     this.EstadoProducto = EstadoProducto;
     this.Categoria_ID = Categoria_ID;
     this.MarcaProducto_ID = MarcaProducto_ID;
     this.Descripcion = Descripcion;
     this.UnidadMedida = UnidadMedida;
     this.StockFinal = StockFinal;
     this.PrecioCosto = PrecioCosto;
     this.PrecioVenta = PrecioVenta;
     this.PrecioLimite = PrecioLimite;
     this.MargenUtil = MargenUtil;
     this.Peso = Peso;
     this.Imagen = Imagen;
     this.CodigoBarra = CodigoBarra;
     this.CodEmp = CodEmp;
     this.TipoProd = TipoProd;
     this.TipoBien_ID = TipoBien_ID;
  
     this.DescripcionTipo = DescripcionTipo;
     this.EstadoTipo = EstadoTipo;
 }
 
     public String getProducto_ID(){ return Producto_ID;}
     public void setProducto_ID(String Producto_ID){this.Producto_ID = Producto_ID;}
     
     public String getNombreProducto(){return NombreProducto;}
     public void setNombreProducto(String NombreProducto){this.NombreProducto = NombreProducto;}
     
     public String getEstadoProducto(){return EstadoProducto;}
     public void setEstadoProducto(String EstadoProducto){this.EstadoProducto = EstadoProducto;}
     
     public int getCategoria_ID(){return Categoria_ID;}
     public void setCategoria_ID(int Categoria_ID){this.Categoria_ID = Categoria_ID;}
     
     public int getMarcaProducto_ID(){return MarcaProducto_ID;}
     public void setMarcaProducto_ID(int MarcaProducto_ID){this.MarcaProducto_ID = MarcaProducto_ID;}
     
     public String getDescripcion(){return Descripcion;}
     public void setDescripcion(String Descripcion){this.Descripcion = Descripcion;}
     
     public String getUnidadMedida(){return UnidadMedida;}
     public void setUnidadMedida(String UnidadMedida){this.UnidadMedida = UnidadMedida;}
     
     public double getStockFinal(){return StockFinal;}
     public void setStockFinal(double StockFinal){this.StockFinal = StockFinal;}
     
     public double getPrecioCosto(){return PrecioCosto;}
     public void setPrecioCosto(double PrecioCosto){this.PrecioCosto = PrecioCosto;}
     
     public double getPrecioVenta(){return PrecioVenta;}
     public void setPrecioVenta(double PrecioVenta){this.PrecioVenta = PrecioVenta;}
     
     public double getPrecioLimite(){return PrecioLimite;}
     public void setPrecioLimite(double PrecioLimite){this.PrecioLimite = PrecioLimite;}
     
     public double getMargenUtil(){return MargenUtil;}
     public void setMargenUtil(double MargenUtil){this.MargenUtil = MargenUtil;}
     
     public double getPeso(){return Peso;}
     public void setPeso(double Peso){this.Peso = Peso;}
     
     public String getImagen(){return Imagen;}
     public void setImagen(String Imagen){this.Imagen = Imagen;}
     
     public String getCodigoBarra(){return CodigoBarra;}
     public void setCodigoBarra(String CodigoBarra){this.CodigoBarra = CodigoBarra;}
     
     public String getCodEmp(){return CodEmp;}
     public void setCodEmp(String CodEmp){this.CodEmp = CodEmp;}
     
     public String getTipoProd(){return TipoProd;}
     public void setTipoProd(String TipoProd){this.TipoProd = TipoProd;}
     
     public String getTipoBien_ID(){return TipoBien_ID;}
     public void setTipoBien_ID(String TipoBien_ID){this.TipoBien_ID = TipoBien_ID;}
     
    public String getDescripcionTipo(){ return DescripcionTipo;}
    public void setDescripcionTipo(String DescripcionTipo){ this.DescripcionTipo = DescripcionTipo;}
    
    public String getEstadoTipo(){ return EstadoTipo;}
    public void setEstadoTipo(String EstadoTipo){ this.EstadoTipo = EstadoTipo;}

    public String getCategoria(){ return Categoria;}
    public void setCategoria(String Categoria){ this.Categoria = Categoria;}

    public String getMarca(){ return Marca;}
    public void setMarca(String Marca){ this.Marca = Marca;}

    /** Utilidad en soles: PrecioVenta - PrecioCosto (como txtutil de frm_Productos). */
    public double getUtilidad(){ return Math.round((PrecioVenta - PrecioCosto) * 100.0) / 100.0;}

    /** "VENTA" o "SERVICIO" según TipoProd (V / S). */
    public String getTipoProdTexto(){ return "S".equalsIgnoreCase(TipoProd == null ? "" : TipoProd.trim()) ? "SERVICIO" : "VENTA";}
}
