package com.registro.dao;

import com.registro.config.ConexionBD;
import com.registro.dto.AreaDTO;
import com.registro.dto.UsuarioDTO;
import com.registro.dto.RolDTO;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDAO {

    // Codigos de retorno de sp_Insert_UsuarioPersonal
    public static final int GRABADO_OK = 0;
    public static final int GRABADO_ERROR = -1;
    public static final int USUARIO_DUPLICADO = -2;

    // Codigos de retorno de sp_elimina_personal
    public static final int PERSONAL_ELIMINADO = 1;
    public static final int PERSONAL_INACTIVADO = 2;

    public UsuarioDTO ValidaAcceso(String usuario, String pass)
    {
        UsuarioDTO usuario2 = null;
        Connection cn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try
        {
            String contraseñaEncriptada = Seguridad.encriptarContraseña(pass);
            
            cn = ConexionBD.obtenerConexion();
            String sql = "{call sp_IngresoUsuarioSistema(?,?)}";
            cs = cn.prepareCall(sql);
            cs.setString(1, usuario);
            cs.setString(2, contraseñaEncriptada);

            rs = cs.executeQuery();

            if (rs.next()) {
                
            //     String contrasenaGuardadaBD = rs.getString("Contraseña"); // Reemplaza con el nombre real de tu columna

            // 3. Usa el método de verificación correcto de tu librería de seguridad
            // SI USAS BCRYPT:
            // boolean esValida = BCrypt.checkpw(pass, contrasenaGuardadaBD);
            
                usuario2 = new UsuarioDTO();

                usuario2.setPersonal_ID(rs.getInt("Personal_ID"));
                usuario2.setApellidoPaterno(rs.getString("ApellidoPaterno"));
                usuario2.setApellidoMaterno(rs.getString("ApellidoMaterno"));
                usuario2.setNombres(rs.getString("Nombres"));
                usuario2.setArea_ID(rs.getString("Area_ID"));
                usuario2.setArea(rs.getString("Area"));
                usuario2.setRol(rs.getString("Rol"));
                usuario2.setCodEmp(rs.getString("CodEmp"));    
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (cs != null) cs.close();
                if (cn != null) cn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return usuario2;
    }

    public UsuarioDTO GeneraCodPersonal()
    {
        UsuarioDTO usuario2 = null;
        String sql = "{call sp_GeneraCodigoPersonal}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement cs = cn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            if (rs.next()) {
                usuario2 = new UsuarioDTO();
                // El procedimiento devuelve una columna sin nombre
                usuario2.setPersonal_ID(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario2;
    }

    public List<RolDTO> MuestraRoles() {
        List<RolDTO> listaRoles = new ArrayList<>();
        String sql = "{call sp_ListaRoles}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                RolDTO rol = new RolDTO();
                rol.setrol_id(rs.getInt("rol_id"));
                rol.setnombre_rol(rs.getString("nombre_rol"));
                rol.setdescripcion(rs.getString("descripcion"));
                listaRoles.add(rol);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listaRoles;
    }

    public List<AreaDTO> ListaAreas() {
        List<AreaDTO> lista = new ArrayList<>();
        String sql = "{call sp_lista_areas}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                lista.add(new AreaDTO(rs.getString("Area_ID").trim(),
                                      rs.getString("DescripcionArea")));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    public List<UsuarioDTO> ListaPersonal() {
        List<UsuarioDTO> lista = new ArrayList<>();
        String sql = "{call sp_lista_personal}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPersonal(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    public UsuarioDTO buscarPersonal(int personalId) {
        UsuarioDTO personal = null;
        String sql = "{call sp_busca_personal(?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql)) {

            st.setInt(1, personalId);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    personal = mapearPersonal(rs);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return personal;
    }

    private UsuarioDTO mapearPersonal(ResultSet rs) throws SQLException {
        UsuarioDTO p = new UsuarioDTO();
        p.setPersonal_ID(rs.getInt("Personal_ID"));
        p.setEstadoGene(rs.getString("EstadoGene"));
        p.setApellidoPaterno(rs.getString("ApellidoPaterno"));
        p.setApellidoMaterno(rs.getString("ApellidoMaterno"));
        p.setNombres(rs.getString("Nombres"));
        p.setDireccion(rs.getString("Direccion"));
        p.setSexo(rs.getString("Sexo"));
        p.setRucDNI(rs.getString("RucDNI"));
        p.setTelefono1(rs.getString("Telefono1"));
        p.setCorreo(rs.getString("Correo"));
        p.setReferencia(rs.getString("Referencia"));
        String areaId = rs.getString("Area_ID");
        p.setArea_ID(areaId == null ? null : areaId.trim());
        p.setArea(rs.getString("Area"));
        p.setCodEmp(rs.getString("CodEmp"));
        p.setFoto(rs.getString("Foto"));
        p.setUsuario(rs.getString("Usuario"));
        p.setRol_ID(rs.getInt("rol_id"));
        p.setRol(rs.getString("Rol"));
        // Tiene acceso al sistema si existe un registro en la tabla Usuario
        p.setOpcUserSist(p.getUsuario() != null ? 1 : 0);
        return p;
    }

    /**
     * Graba o actualiza el personal (el procedimiento decide segun exista el Personal_ID).
     * Devuelve GRABADO_OK, GRABADO_ERROR o USUARIO_DUPLICADO.
     */
   public int grabarPersonal(UsuarioDTO usuario) {
       
       if(usuario.getContraseña()!= null)
       {
           String contraseñaEncriptada = Seguridad.encriptarContraseña(usuario.getContraseña());
           usuario.setContraseña(contraseñaEncriptada);
       }

    String sql = "{? = call sp_Insert_UsuarioPersonal(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

    try (Connection cn = ConexionBD.obtenerConexion();
         CallableStatement st = cn.prepareCall(sql)) {

        st.registerOutParameter(1, Types.INTEGER);
        st.setInt(2, usuario.getPersonal_ID());
        st.setString(3, usuario.getEstadoGene());
        st.setString(4, usuario.getApellidoPaterno());
        st.setString(5, usuario.getApellidoMaterno());
        st.setString(6, usuario.getNombres());
        st.setString(7, usuario.getDireccion());
        st.setString(8, usuario.getSexo());
        st.setString(9, usuario.getRucDNI());
        st.setString(10, usuario.getTelefono1());
        st.setString(11, usuario.getCorreo());
        st.setString(12, usuario.getReferencia());
        st.setString(13, usuario.getArea_ID());
        st.setString(14, usuario.getUsuario());
        st.setString(15, usuario.getContraseña());
        st.setString(16, usuario.getCodEmp());
        st.setString(17, usuario.getFoto());
        st.setInt(18, usuario.getUsuarioMod());
        st.setInt(19, usuario.getRol_ID());
        st.setInt(20, usuario.getOpcUserSist());

        st.execute();
        return st.getInt(1);

    } catch (SQLException e) {
        e.printStackTrace();
        return GRABADO_ERROR; 
    }
}

    public boolean registrarPersonal(UsuarioDTO usuario) {
        return grabarPersonal(usuario) == GRABADO_OK;
    }

    /**
     * Elimina el personal si no tiene movimientos; si los tiene, lo inactiva.
     * Devuelve PERSONAL_ELIMINADO, PERSONAL_INACTIVADO o -1 si hubo error.
     */
    public int EliminarPersonal(UsuarioDTO usuario) {
        String sql = "{? = call sp_elimina_personal(?)}";

        try (Connection cn = ConexionBD.obtenerConexion();
             CallableStatement st = cn.prepareCall(sql)) {

            st.registerOutParameter(1, Types.INTEGER);
            st.setInt(2, usuario.getPersonal_ID());
            st.execute();
            return st.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public boolean PersonalUsuario_ActualizaClave(UsuarioDTO usuario)
    {
        Connection cn = null;
        CallableStatement cs = null;
        boolean ResultadoExitoso = false;
        
        try
        {
            if (usuario == null || usuario.getPersonal_ID() <= 0
                    || usuario.getContraseña() == null || usuario.getContraseña().trim().isEmpty()) {
                return false;
            }

            // Misma encriptación que usa el login (Seguridad.encriptarContraseña)
            String claveEncriptada = Seguridad.encriptarContraseña(usuario.getContraseña());

            cn = ConexionBD.obtenerConexion();
            String sql = "{call sp_PersonalUsuario_ActualizaClave(?,?)}";

            cs = cn.prepareCall(sql);

            cs.setInt(1,usuario.getPersonal_ID());
            cs.setString(2,claveEncriptada);

            // El SP usa SET NOCOUNT ON: no devuelve filas afectadas,
            // por eso el éxito es que se ejecute sin error.
            cs.execute();
            ResultadoExitoso = true;

        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (cn!=null)cn.close();
                if (cs !=null)cs.close();
            }
            catch(Exception ex)
            {
                
            }
        }
        return ResultadoExitoso;
    }
    
}
