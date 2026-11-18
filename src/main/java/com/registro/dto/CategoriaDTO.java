/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.dto;

/**
 *
 * @author Ing
 */
public class CategoriaDTO implements java.io.Serializable {
    
    private int Categoria_ID;
    private String Nombre;
    private String Estado;
    private String rutaimagen;
    

    public CategoriaDTO(){
   }

   public CategoriaDTO(int Categoria_ID,String Nombre,String Estado,String rutaimagen)
   {
       this.Categoria_ID = Categoria_ID;
       this.Nombre = Nombre;
       this.Estado = Estado;
       this.rutaimagen =rutaimagen;

   }
   
       public int getCategoria_ID(){ return Categoria_ID;}
       public void setCategoria_ID(int Categoria_ID){this.Categoria_ID = Categoria_ID;}
       
       public String getNombre(){return Nombre;}
       public void setNombre(String Nombre){this.Nombre = Nombre;}
       
       public String getEstado(){return Estado;}
       public void setEstado(String Estado){this.Estado = Estado;}
       
       public String getrutaimagen(){return rutaimagen;}
       public void setrutaimagen(String rutaimagen){this.rutaimagen =rutaimagen;}
}
