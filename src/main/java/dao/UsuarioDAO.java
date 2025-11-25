package dao;

import modelo.Usuario;
import util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    // Roles permitidos en el sistema
    private static final String[] ROLES_PERMITIDOS = {"admin", "almacen", "supervisor"};
    
    // Validar si un rol es permitido
    private boolean esRolValido(String rol) {
        if (rol == null) return false;
        for (String rolPermitido : ROLES_PERMITIDOS) {
            if (rolPermitido.equals(rol)) {
                return true;
            }
        }
        return false;
    }
    
    public Usuario validar(String usuario, String password) {
        Usuario u = null;
        String sql = "SELECT * FROM usuarios WHERE usuario=? AND password=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setPassword(rs.getString("password"));
                u.setNombre(rs.getString("nombre"));
                u.setRol(rs.getString("rol"));
            }
        } catch (SQLException e) {
            System.err.println("Error en validar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return u;
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setPassword(rs.getString("password"));
                u.setNombre(rs.getString("nombre"));
                u.setRol(rs.getString("rol"));
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean agregar(Usuario u) {
        // Validar que el rol sea permitido
        if (!esRolValido(u.getRol())) {
            System.err.println("Error: Rol no permitido: " + u.getRol());
            return false;
        }
        
        String sql = "INSERT INTO usuarios (usuario, password, nombre, rol) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getRol());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Usuario u) {
        // Validar que el rol sea permitido
        if (!esRolValido(u.getRol())) {
            System.err.println("Error: Rol no permitido: " + u.getRol());
            return false;
        }
        
        String sql = "UPDATE usuarios SET usuario=?, password=?, nombre=?, rol=? WHERE id=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getRol());
            ps.setInt(5, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Usuario buscarPorId(int id) {
        Usuario u = null;
        String sql = "SELECT * FROM usuarios WHERE id=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setUsuario(rs.getString("usuario"));
                u.setPassword(rs.getString("password"));
                u.setNombre(rs.getString("nombre"));
                u.setRol(rs.getString("rol"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }
} 