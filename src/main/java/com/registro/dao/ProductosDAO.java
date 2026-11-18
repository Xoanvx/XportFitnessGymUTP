/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.registro.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.registro.config.ConexionBD;
import com.registro.dto.CategoriaDTO;
import com.registro.dto.MarcasDTO;
import com.registro.dto.ProductosDTO;
import com.registro.dto.UsuarioDTO;

/**
 *
 * @author Ing
 */
public class ProductosDAO {
    
  public ProductosDTO ListaTipoBien()
  {
      ProductosDTO TipoBien = null;
      Connection cn = null;
      CallableStatement cs = null;
      ResultSet rs = null;
      
      try
      {
          cn = ConexionBD.obtenerConexion();
          String sql = "{call sp_ListaTipo_Bien}";
          
          cs = cn.prepareCall(sql);
          rs = cs.executeQuery();

          if(rs.next())
          {
              TipoBien = new ProductosDTO();
              
              TipoBien.setTipoBien_ID(rs.getString("TipoBien"));
              
          }

      }
      catch(SQLException ex)
      {
          ex.printStackTrace();
      }
      finally
      {
         try
         {
          if(cn != null)cn.close();
          if(cs != null)cs.close();
          if(rs != null)rs.close();          
         }        
         catch(SQLException ex)
         {
             ex.printStackTrace();
         }
      }
      
      return TipoBien;
  }
  
  public List<CategoriaDTO> ListaCategorias()
  {
      List<CategoriaDTO> listaCat = new ArrayList<>();
      
      Connection cn = null;
      CallableStatement cs = null;
      ResultSet rs = null;
      
      try
      {
          cn = ConexionBD.obtenerConexion();
          String sql = "{call sp_lista_categoriasAct}";
          
          cs = cn.prepareCall(sql);
          rs = cs.executeQuery();
          
          while(rs.next())
          {
              CategoriaDTO categoria = new CategoriaDTO();
              
              categoria.setCategoria_ID(rs.getInt("Categoria_ID"));
              categoria.setNombre(rs.getString("Nombre"));
              categoria.setEstado(rs.getString("Estado"));
              
              listaCat.add(categoria);
          }
      }
      catch(SQLException ex)
      {
          ex.printStackTrace();
      }
      
      finally
      {
         try
         {
            if(cn != null)cn.close();
            if(cs != null)cs.close();
            if(rs != null)rs.close();
         }
         catch(SQLException ex)
         {
             ex.printStackTrace();
         }
      }
      
      return listaCat;
    
  }
  
  public List<MarcasDTO> ListaMarcas()
  {
      List<MarcasDTO> listaMarca = new ArrayList<>();
      
      Connection cn = null;
      CallableStatement cs = null;
      ResultSet rs = null;
      
      try
      {
          cn = ConexionBD.obtenerConexion();
          String sql = "{call sp_listaMarcaAct}";
          
          cs = cn.prepareCall(sql);
          rs = cs.executeQuery();
          
          while(rs.next())
          {
              MarcasDTO marca = new MarcasDTO();

              // sp_listaMarcaAct devuelve MarcaProducto_ID, Nombre, EstadoMarca
              marca.setMarca_ID(rs.getInt("MarcaProducto_ID"));
              marca.setNombre(rs.getString("Nombre"));
              marca.setEstadoMarca(rs.getString("EstadoMarca"));
              
              listaMarca.add(marca);
          }
      }
      catch(SQLException ex)
      {
          ex.printStackTrace();
      }
      finally
      {
          try
          {
              if(cn != null)cn.close();
              if(cs != null)cs.close();
              if(rs != null)rs.close();
          }
          catch(SQLException ex)
          {
              ex.printStackTrace();
          }     
      }
      
      return listaMarca;
  }
  
  /**
     * MANTENIMIENTO DE CATEGORIAS DE PRODUCTOS
     * 
     * */
  public CategoriaDTO AutogeneraCategoria()
  {
        CategoriaDTO autogeneraCategoria = null;
        Connection cnn = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        
        try
        {
            cnn = ConexionBD.obtenerConexion();
            String sql = "{call sp_max_cod_categoria}";
            
            cs = cnn.prepareCall(sql);
            rs = cs.executeQuery();
            
            autogeneraCategoria = new CategoriaDTO();
            
            if(rs.next())
            {
                autogeneraCategoria.setCategoria_ID(rs.getInt("Categoria_ID"));
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if(cnn != null){cnn.close();}
                if(cs != null){cs.close();}
                if(rs != null){rs.close();}
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
            }
        }
        
        return autogeneraCategoria;
    }
    
    public boolean Mante_Categorias(CategoriaDTO Categorias)
    {
        Connection cnn = null;
        CallableStatement cs = null;
        boolean registroExitoso = false;
        try
        {
            cnn = ConexionBD.obtenerConexion();
            String sql ="{call sp_Mante_Categorias(?,?,?,?)}";
            cs = cnn.prepareCall(sql);

            cs.setInt(1, Categorias.getCategoria_ID());
            cs.setString(2, Categorias.getNombre());
            cs.setString(3, Categorias.getEstado());
            cs.setString(4, Categorias.getrutaimagen());

            int filasAfectadas = cs.executeUpdate();
            if(filasAfectadas > 0){ registroExitoso = true;}
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if(cnn != null){cnn.close();}
                if(cs != null){cs.close();}

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return registroExitoso;
    }
    
    public boolean Elimina_Categorias(CategoriaDTO Categorias)
    {
        Connection cnn = null;
        CallableStatement cs = null;
        boolean categoriaEliminado = false;
        try
        {
            cnn = ConexionBD.obtenerConexion();
            String sql ="{call sp_Elimina_Categorias(?)}";
            cs = cnn.prepareCall(sql);

            cs.setInt(1, Categorias.getCategoria_ID());
            
            int filasAfectadas = cs.executeUpdate();
            if(filasAfectadas > 0){ categoriaEliminado = true;}
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if(cnn != null){cnn.close();}
                if(cs != null){cs.close();}

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return categoriaEliminado;
    }
    
  /**
     * MANTENIMIENTO DE MARCAS DE PRODUCTOS
     * 
     * */    
    public MarcasDTO AutogeneraMarca()
    {
        MarcasDTO autogeneraMarca = null;
        Connection cnn = null;
        CallableStatement cs = null;
        ResultSet rs = null;
        
        try
        {
            cnn = ConexionBD.obtenerConexion();
            String sql = "{call sp_max_marc}";
            
            cs = cnn.prepareCall(sql);
            rs = cs.executeQuery();
            
            autogeneraMarca = new MarcasDTO();
            
            if(rs.next())
            {
                autogeneraMarca.setMarca_ID(rs.getInt("Marca_ID"));
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if(cnn != null){cnn.close();}
                if(cs != null){cs.close();}
                if(rs != null){rs.close();}
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
            }
        }
        
        return autogeneraMarca;
    }
    
    public boolean Mante_Marcas(MarcasDTO marcas)
    {
        Connection cnn = null;
        CallableStatement cs = null;
        boolean registroExitoso = false;
        try
        {
            cnn = ConexionBD.obtenerConexion();
            String sql ="{call sp_Mante_marcas(?,?,?)}";
            cs = cnn.prepareCall(sql);

            cs.setInt(1, marcas.getMarca_ID());
            cs.setString(2, marcas.getNombre());
            cs.setString(3, marcas.getEstadoMarca());

            int filasAfectadas = cs.executeUpdate();
            if(filasAfectadas > 0){ registroExitoso = true;}
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if(cnn != null){cnn.close();}
                if(cs != null){cs.close();}

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return registroExitoso;
    }
    public boolean Elim_MarcaPro(MarcasDTO marcas)
    {
        Connection cnn = null;
        CallableStatement cs = null;
        boolean marcaEliminado = false;
        try
        {
            cnn = ConexionBD.obtenerConexion();
            String sql ="{call sp_Elim_MarcaPro(?)}";
            cs = cnn.prepareCall(sql);

            cs.setInt(1, marcas.getMarca_ID());
            
            int filasAfectadas = cs.executeUpdate();
            if(filasAfectadas > 0){ marcaEliminado = true;}
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if(cnn != null){cnn.close();}
                if(cs != null){cs.close();}

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return marcaEliminado;
    }

  /**
     * MANTENIMIENTO DE PRODUCTOS DE COMERCIALIZACION
     * */

    /** sp_Lista_ProductosGral_Activos @CodEmp: productos activos de la empresa. */
    public List<ProductosDTO> ListaProductosActivos(String codEmp)
    {
        List<ProductosDTO> lista = new ArrayList<>();
        String sql = "{call sp_Lista_ProductosGral_Activos(?)}";
        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql))
        {
            cs.setString("CodEmp", codEmp);
            leerListado(cs, lista);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return lista;
    }

    /** sp_buscaProdxNomTotAct @CodEmp, @descprod: búsqueda por nombre/descripción (frm_BusquedaProd). */
    public List<ProductosDTO> BuscaProductosXNombre(String codEmp, String texto)
    {
        List<ProductosDTO> lista = new ArrayList<>();
        String sql = "{call sp_buscaProdxNomTotAct(?,?)}";
        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql))
        {
            cs.setString("CodEmp", codEmp);
            cs.setString("descprod", texto);
            leerListado(cs, lista);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return lista;
    }

    /** sp_buscaProdxMarcaActivo @CodEmp, @MarcaProducto_ID: productos activos de una marca. */
    public List<ProductosDTO> BuscaProductosXMarca(String codEmp, int marcaId)
    {
        List<ProductosDTO> lista = new ArrayList<>();
        String sql = "{call sp_buscaProdxMarcaActivo(?,?)}";
        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql))
        {
            cs.setString("CodEmp", codEmp);
            cs.setString("MarcaProducto_ID", String.valueOf(marcaId));
            leerListado(cs, lista);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return lista;
    }

    /** Columnas comunes de los listados: las mismas que ordena frm_Productos.OrdenaGrid(). */
    private void leerListado(CallableStatement cs, List<ProductosDTO> lista) throws SQLException
    {
        try (ResultSet rs = cs.executeQuery())
        {
            while (rs.next())
            {
                ProductosDTO p = new ProductosDTO();
                p.setProducto_ID(recortar(rs.getString("Producto_ID")));
                p.setDescripcion(rs.getString("Descripcion"));
                p.setEstadoProducto(recortar(rs.getString("EstadoProducto")));
                p.setCategoria(rs.getString("Categoria"));
                p.setMarca(rs.getString("Marca"));
                p.setNombreProducto(rs.getString("NombreProducto"));
                p.setUnidadMedida(recortar(rs.getString("UnidadMedida")));
                p.setStockFinal(rs.getDouble("StockFinal"));
                p.setPrecioCosto(rs.getDouble("PrecioCosto"));
                p.setPrecioVenta(rs.getDouble("PrecioVenta"));
                p.setPrecioLimite(rs.getDouble("PrecioLimite"));
                p.setMargenUtil(rs.getDouble("MargenUtil"));
                p.setPeso(rs.getDouble("Peso"));
                p.setImagen(rs.getString("Imagen"));
                p.setCodigoBarra(rs.getString("CodigoBarra"));
                p.setTipoProd(recortar(rs.getString("TipoProd")));
                lista.add(p);
            }
        }
    }

    private static String recortar(String s)
    {
        return s == null ? null : s.trim();
    }

    /** sp_autogenera_codprod: siguiente código de 6 dígitos (columna NuevoID; se lee la primera columna). */
    public ProductosDTO Autogenera_codprod()
    {
        ProductosDTO autogenera = new ProductosDTO();
        String sql = "{call sp_autogenera_codprod}";
        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql);
             ResultSet rs = cs.executeQuery())
        {
            if (rs.next())
            {
                autogenera.setProducto_ID(recortar(rs.getString(1)));
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        return autogenera;
    }

    /**
     * sp_ListaDetalleVentaXProd @Producto_ID: si el producto ya tiene ventas,
     * frm_Productos bloquea el stock y actualiza con sp_Update_Productos2.
     */
    public boolean TieneVentas(String productoId)
    {
        String sql = "{call sp_ListaDetalleVentaXProd(?)}";
        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql))
        {
            cs.setString("Producto_ID", productoId);
            try (ResultSet rs = cs.executeQuery())
            {
                return rs.next();
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return true;   // ante la duda no se permite cambiar el stock
        }
    }

    /** sp_Insert_Productos. Devuelve true si el procedimiento terminó sin error. */
    public boolean Insert_ProductosD(ProductosDTO p)
    {
        String sql = "{call sp_Insert_Productos(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql))
        {
            asignarComunes(cs, p, true);
            cs.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza el producto.si el stock se puede editar (sin ventas) usa
     * sp_Update_Productos; si ya tiene ventas usa sp_Update_Productos2, que no recibe StockFinal.
     */
    public boolean Update_Productos(ProductosDTO p, boolean incluyeStock)
    {
        String sql = incluyeStock
                ? "{call sp_Update_Productos(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"
                : "{call sp_Update_Productos2(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql))
        {
            asignarComunes(cs, p, incluyeStock);
            cs.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /** Parámetros que envía Alm_Mante_ProductosD (Insert / Update / Update2). */
    private void asignarComunes(CallableStatement cs, ProductosDTO p, boolean incluyeStock) throws SQLException
    {
        cs.setString("Producto_ID", p.getProducto_ID());
        cs.setString("NombreProducto", p.getNombreProducto());
        cs.setString("EstadoProducto", p.getEstadoProducto());
        cs.setInt("Categoria_ID", p.getCategoria_ID());
        cs.setString("MarcaProducto_ID", String.valueOf(p.getMarcaProducto_ID()));
        cs.setString("Descripcion", p.getDescripcion());
        cs.setString("UnidadMedida", p.getUnidadMedida());
        if (incluyeStock)
        {
            cs.setDouble("StockFinal", p.getStockFinal());
        }
        cs.setBigDecimal("PrecioCosto", dinero(p.getPrecioCosto()));
        cs.setBigDecimal("PrecioVenta", dinero(p.getPrecioVenta()));
        cs.setBigDecimal("PrecioLimite", dinero(p.getPrecioLimite()));
        cs.setBigDecimal("MargenUtil", dinero(p.getMargenUtil()));
        cs.setDouble("Peso", p.getPeso());
        cs.setString("Imagen", p.getImagen() == null ? "" : p.getImagen());
        cs.setString("CodigoBarra", p.getCodigoBarra() == null ? "" : p.getCodigoBarra());
        cs.setString("CodEmp", p.getCodEmp());
        cs.setString("TipoProd", p.getTipoProd());
    }

    private static java.math.BigDecimal dinero(double valor)
    {
        return java.math.BigDecimal.valueOf(valor).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * sp_Update_Estado_Producto @Producto_ID, @EstadoProducto, @CodEmp.
     * "Eliminar" en frm_Productos no borra: deja el producto Inactivo.
     */
    public boolean Update_Estado_Producto(String productoId, String estado, String codEmp)
    {
        String sql = "{call sp_Update_Estado_Producto(?,?,?)}";
        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql))
        {
            cs.setString("Producto_ID", productoId);
            cs.setString("EstadoProducto", estado);
            cs.setString("CodEmp", codEmp);
            cs.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /** sp_Elim_Producto: borrado físico (no lo usa frm_Productos; se conserva por compatibilidad). */
    public boolean Elim_Producto(ProductosDTO producto)
    {
        String sql = "{call sp_Elim_Producto(?)}";
        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql))
        {
            cs.setString(1, producto.getProducto_ID());
            return cs.executeUpdate() > 0;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }
}
