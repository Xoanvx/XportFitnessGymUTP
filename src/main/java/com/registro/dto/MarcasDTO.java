/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.dto;

/**
 *
 * @author Ing
 */
public class MarcasDTO implements java.io.Serializable {
    
    private int Marca_ID;
    private String Nombre;
    private String EstadoMarca;
            
    public MarcasDTO(){
        
    }
    
    public MarcasDTO(int Marca_ID, String Nombre, String EstadoMarca)
    {
        this.Marca_ID = Marca_ID;
        this.Nombre = Nombre;
        this.EstadoMarca = EstadoMarca;
    }
    
    public int getMarca_ID(){return Marca_ID;}
    public void setMarca_ID(int Marca_ID){this.Marca_ID = Marca_ID;}
    
    public String getNombre(){return Nombre;}
    public void setNombre(String Nombre){this.Nombre = Nombre;}
    
    public String getEstadoMarca(){return EstadoMarca;}
    public void setEstadoMarca(String EstadoMarca){this.EstadoMarca = EstadoMarca;}
}
