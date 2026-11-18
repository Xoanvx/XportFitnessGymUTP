/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.facade;

import com.registro.dao.ProductosDAO;
import com.registro.dto.ProductosDTO;
import com.registro.dto.CategoriaDTO;
import com.registro.dto.MarcasDTO;
import java.util.List;
/**
 *
 * @author Ing
 */
public class ProductosFACADE {

    private final ProductosDAO dao = new ProductosDAO();

    public CategoriaDTO autogeneraCategoria() {
    return dao.AutogeneraCategoria();
   }
    public boolean Mante_Categorias(CategoriaDTO categoria)
    {
        return dao.Mante_Categorias(categoria);
    }
    public boolean Elimina_Categorias(CategoriaDTO categoria)
    {
        return dao.Elimina_Categorias(categoria);
    }

    public MarcasDTO AutogeneraMarca()
    {
        return dao.AutogeneraMarca();
    }
    public boolean Mante_Marcas(MarcasDTO marcas)
    {
        return dao.Mante_Marcas(marcas);
    }
    public boolean Elim_MarcaPro(MarcasDTO marcas)
    {
        return dao.Elim_MarcaPro(marcas);
    }

    public List<CategoriaDTO> ListaCategorias()
    {
        return dao.ListaCategorias();
    }
    public List<MarcasDTO> ListaMarcas()
    {
        return dao.ListaMarcas();
    }

    public List<ProductosDTO> ListaProductosActivos(String codEmp)
    {
        return dao.ListaProductosActivos(codEmp);
    }
    public List<ProductosDTO> BuscaProductosXNombre(String codEmp, String texto)
    {
        return dao.BuscaProductosXNombre(codEmp, texto);
    }
    public List<ProductosDTO> BuscaProductosXMarca(String codEmp, int marcaId)
    {
        return dao.BuscaProductosXMarca(codEmp, marcaId);
    }
    public boolean TieneVentas(String productoId)
    {
        return dao.TieneVentas(productoId);
    }

    public ProductosDTO AutogeneraProducto()
    {
        return dao.Autogenera_codprod();
    }
    public boolean Insert_ProductosD(ProductosDTO productos)
    {
        return dao.Insert_ProductosD(productos);
    }
    public boolean Update_Productos(ProductosDTO productos, boolean incluyeStock)
    {
        return dao.Update_Productos(productos, incluyeStock);
    }
    public boolean Update_Estado_Producto(String productoId, String estado, String codEmp)
    {
        return dao.Update_Estado_Producto(productoId, estado, codEmp);
    }

    public boolean Elimina_Producto(ProductosDTO productos)
    {
        return dao.Elim_Producto(productos);
    }
}
