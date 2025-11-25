package dao;
import modelo.Movimiento;
import util.Conexion;
import java.sql.*;
import java.util.*;

public class MovimientoDAO {
    
    public List<Movimiento> listar() {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT m.*, p.nombre as producto_nombre, p.codigo as producto_codigo, " +
                     "u.nombre as usuario_nombre " +
                     "FROM movimientos m " +
                     "INNER JOIN productos p ON m.producto_id = p.id " +
                     "INNER JOIN usuarios u ON m.usuario_id = u.id " +
                     "ORDER BY m.fecha DESC";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setId(rs.getInt("id"));
                m.setFecha(rs.getTimestamp("fecha"));
                m.setTipo(rs.getString("tipo"));
                m.setProductoId(rs.getInt("producto_id"));
                m.setProductoNombre(rs.getString("producto_nombre"));
                m.setProductoCodigo(rs.getString("producto_codigo"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setUsuarioId(rs.getInt("usuario_id"));
                m.setUsuarioNombre(rs.getString("usuario_nombre"));
                m.setObservaciones(rs.getString("observaciones"));
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Movimiento> listarPorTipo(String tipo) {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT m.*, p.nombre as producto_nombre, p.codigo as producto_codigo, " +
                     "u.nombre as usuario_nombre " +
                     "FROM movimientos m " +
                     "INNER JOIN productos p ON m.producto_id = p.id " +
                     "INNER JOIN usuarios u ON m.usuario_id = u.id " +
                     "WHERE m.tipo = ? " +
                     "ORDER BY m.fecha DESC";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setId(rs.getInt("id"));
                m.setFecha(rs.getTimestamp("fecha"));
                m.setTipo(rs.getString("tipo"));
                m.setProductoId(rs.getInt("producto_id"));
                m.setProductoNombre(rs.getString("producto_nombre"));
                m.setProductoCodigo(rs.getString("producto_codigo"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setUsuarioId(rs.getInt("usuario_id"));
                m.setUsuarioNombre(rs.getString("usuario_nombre"));
                m.setObservaciones(rs.getString("observaciones"));
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Movimiento> listarPorProducto(int productoId) {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT m.*, p.nombre as producto_nombre, p.codigo as producto_codigo, " +
                     "u.nombre as usuario_nombre " +
                     "FROM movimientos m " +
                     "INNER JOIN productos p ON m.producto_id = p.id " +
                     "INNER JOIN usuarios u ON m.usuario_id = u.id " +
                     "WHERE m.producto_id = ? " +
                     "ORDER BY m.fecha DESC";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setId(rs.getInt("id"));
                m.setFecha(rs.getTimestamp("fecha"));
                m.setTipo(rs.getString("tipo"));
                m.setProductoId(rs.getInt("producto_id"));
                m.setProductoNombre(rs.getString("producto_nombre"));
                m.setProductoCodigo(rs.getString("producto_codigo"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setUsuarioId(rs.getInt("usuario_id"));
                m.setUsuarioNombre(rs.getString("usuario_nombre"));
                m.setObservaciones(rs.getString("observaciones"));
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean agregar(Movimiento m) {
        String sql = "INSERT INTO movimientos (tipo, producto_id, cantidad, usuario_id, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getTipo());
            ps.setInt(2, m.getProductoId());
            ps.setInt(3, m.getCantidad());
            ps.setInt(4, m.getUsuarioId());
            ps.setString(5, m.getObservaciones());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /* Obtiene el conteo de movimientos por tipo para gráficos */
    public Map<String, Integer> obtenerMovimientosPorTipo() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        String sql = "SELECT tipo, COUNT(*) as total FROM movimientos GROUP BY tipo ORDER BY total DESC";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getString("tipo"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }
    
    /*Obtiene movimientos por día para gráficos de línea */
    public Map<String, Map<String, Integer>> obtenerMovimientosPorFecha(int dias) {
        Map<String, Map<String, Integer>> resultado = new LinkedHashMap<>();
        String sql = "SELECT DATE(fecha) as fecha, tipo, SUM(cantidad) as total " +
                     "FROM movimientos " +
                     "WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                     "GROUP BY DATE(fecha), tipo " +
                     "ORDER BY fecha ASC, tipo ASC";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dias);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String fecha = rs.getDate("fecha").toString();
                String tipo = rs.getString("tipo");
                int cantidad = rs.getInt("total");
                
                resultado.computeIfAbsent(fecha, k -> new LinkedHashMap<>()).put(tipo, cantidad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }
    
    /* Obtiene la cantidad total de movimientos por tipo */
    public Map<String, Integer> obtenerCantidadPorTipo() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        String sql = "SELECT tipo, SUM(cantidad) as total_cantidad FROM movimientos GROUP BY tipo ORDER BY total_cantidad DESC";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getString("tipo"), rs.getInt("total_cantidad"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }
}
