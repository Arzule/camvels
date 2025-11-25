package dao;

import modelo.Producto;
import util.Conexion;
import java.sql.*;
import java.util.*;

public class ProductoDAO {
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT *, " +
                     "COALESCE(stock_buen_estado, CASE WHEN estado = 'buen_estado' THEN stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(stock_mal_estado, CASE WHEN estado = 'mal_estado' THEN stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto p = crearProductoDesdeResultSet(rs);
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    private Producto crearProductoDesdeResultSet(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setCategoria(rs.getString("categoria"));
        p.setStock(rs.getInt("stock"));
        try {
            p.setStockBuenEstado(rs.getInt("stock_buen_estado"));
        } catch (SQLException e) {
            int stockTotal = rs.getInt("stock");
            String estado = rs.getString("estado");
            if ("mal_estado".equals(estado)) {
                p.setStockBuenEstado(0);
                p.setStockMalEstado(stockTotal);
            } else {
                p.setStockBuenEstado(stockTotal);
                p.setStockMalEstado(0);
            }
        }
        try {
            p.setStockMalEstado(rs.getInt("stock_mal_estado"));
        } catch (SQLException e) {

        }
        p.setMinimo(rs.getInt("minimo"));
        p.setPrecio(rs.getDouble("precio"));
        p.setEstado(rs.getString("estado"));

        try {
            int proveedorId = rs.getInt("proveedor_id");
            if (!rs.wasNull()) {
                p.setProveedorId(proveedorId);
            }
        } catch (SQLException e) {

            p.setProveedorId(null);
        }
        return p;
    }

    public boolean agregar(Producto p) {

        p.actualizarStockTotal();
        
        String sql = "INSERT INTO productos (codigo, nombre, categoria, stock, stock_buen_estado, stock_mal_estado, minimo, precio, estado, proveedor_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getStockBuenEstado());
            ps.setInt(6, p.getStockMalEstado());
            ps.setInt(7, p.getMinimo());
            ps.setDouble(8, p.getPrecio());
            ps.setString(9, p.getEstado());
            if (p.getProveedorId() != null) {
                ps.setInt(10, p.getProveedorId());
            } else {
                ps.setNull(10, java.sql.Types.INTEGER);
            }
            int result = ps.executeUpdate();
            if (result > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {

            if (e.getMessage().contains("stock_buen_estado") || e.getMessage().contains("stock_mal_estado") || e.getMessage().contains("proveedor_id")) {
                return agregarSinStockSeparado(p);
            }
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean agregarSinStockSeparado(Producto p) {
        String sql = "INSERT INTO productos (codigo, nombre, categoria, stock, minimo, precio, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getMinimo());
            ps.setDouble(6, p.getPrecio());
            ps.setString(7, p.getEstado());
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizar(Producto p) {

        p.actualizarStockTotal();
        
        String sql = "UPDATE productos SET codigo=?, nombre=?, categoria=?, stock=?, stock_buen_estado=?, stock_mal_estado=?, minimo=?, precio=?, estado=?, proveedor_id=? WHERE id=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getStockBuenEstado());
            ps.setInt(6, p.getStockMalEstado());
            ps.setInt(7, p.getMinimo());
            ps.setDouble(8, p.getPrecio());
            ps.setString(9, p.getEstado());
            if (p.getProveedorId() != null) {
                ps.setInt(10, p.getProveedorId());
            } else {
                ps.setNull(10, java.sql.Types.INTEGER);
            }
            ps.setInt(11, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {

            if (e.getMessage().contains("stock_buen_estado") || e.getMessage().contains("stock_mal_estado") || e.getMessage().contains("proveedor_id")) {
                return actualizarSinStockSeparado(p);
            }
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean actualizarSinStockSeparado(Producto p) {
        String sql = "UPDATE productos SET codigo=?, nombre=?, categoria=?, stock=?, minimo=?, precio=?, estado=? WHERE id=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getCategoria());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getMinimo());
            ps.setDouble(6, p.getPrecio());
            ps.setString(7, p.getEstado());
            ps.setInt(8, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Producto buscarPorId(int id) {
        Producto p = null;
        String sql = "SELECT *, " +
                     "COALESCE(stock_buen_estado, CASE WHEN estado = 'buen_estado' THEN stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(stock_mal_estado, CASE WHEN estado = 'mal_estado' THEN stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos WHERE id=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p = crearProductoDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }
    
    public List<Producto> listarPorCategoria(String categoria) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT *, " +
                     "COALESCE(stock_buen_estado, CASE WHEN estado = 'buen_estado' THEN stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(stock_mal_estado, CASE WHEN estado = 'mal_estado' THEN stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos WHERE categoria = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categoria);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Producto> listarPorEstado(String estado) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT *, " +
                     "COALESCE(stock_buen_estado, CASE WHEN estado = 'buen_estado' THEN stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(stock_mal_estado, CASE WHEN estado = 'mal_estado' THEN stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos WHERE estado = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Producto> listarPorCategoriaYEstado(String categoria, String estado) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT *, " +
                     "COALESCE(stock_buen_estado, CASE WHEN estado = 'buen_estado' THEN stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(stock_mal_estado, CASE WHEN estado = 'mal_estado' THEN stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos WHERE categoria = ? AND estado = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categoria);
            ps.setString(2, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Producto> listarStockBajo() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT *, " +
                     "COALESCE(stock_buen_estado, CASE WHEN estado = 'buen_estado' THEN stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(stock_mal_estado, CASE WHEN estado = 'mal_estado' THEN stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos WHERE stock <= minimo";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public int obtenerTotalStock() {
        int totalStock = 0;
        String sql = "SELECT SUM(stock) as total FROM productos";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalStock = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalStock;
    }
    
    public List<Producto> buscarPorTexto(String busqueda) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT *, " +
                     "COALESCE(stock_buen_estado, CASE WHEN estado = 'buen_estado' THEN stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(stock_mal_estado, CASE WHEN estado = 'mal_estado' THEN stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos WHERE codigo LIKE ? OR nombre LIKE ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String patron = "%" + busqueda + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public Map<Integer, List<Object[]>> obtenerProductosConProblemasPorProveedor() {
        Map<Integer, List<Object[]>> productosPorProveedor = new LinkedHashMap<>();
        // Productos con stock bajo o en mal estado, junto con información del proveedor
        String sql = "SELECT p.id as producto_id, p.codigo, p.nombre, p.categoria, p.stock, p.minimo, " +
                     "COALESCE(p.stock_buen_estado, CASE WHEN p.estado = 'buen_estado' THEN p.stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(p.stock_mal_estado, CASE WHEN p.estado = 'mal_estado' THEN p.stock ELSE 0 END) as stock_mal_estado, " +
                     "p.precio, p.estado, p.proveedor_id, " +
                     "pr.nombre as proveedor_nombre, pr.ruc as proveedor_ruc, " +
                     "pr.telefono as proveedor_telefono, pr.email as proveedor_email, " +
                     "pr.direccion as proveedor_direccion " +
                     "FROM productos p " +
                     "LEFT JOIN proveedores pr ON p.proveedor_id = pr.id " +
                     "WHERE (p.stock <= p.minimo OR COALESCE(p.stock_mal_estado, 0) > 0) " +
                     "AND p.proveedor_id IS NOT NULL " +
                     "ORDER BY pr.nombre, p.categoria, p.nombre";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int proveedorId = rs.getInt("proveedor_id");
                if (proveedorId > 0) {

                    Object[] productoInfo = new Object[16];
                    productoInfo[0] = rs.getInt("producto_id");
                    productoInfo[1] = rs.getString("codigo");
                    productoInfo[2] = rs.getString("nombre");
                    productoInfo[3] = rs.getString("categoria");
                    productoInfo[4] = rs.getInt("stock");
                    productoInfo[5] = rs.getInt("minimo");
                    productoInfo[6] = rs.getInt("stock_buen_estado");
                    productoInfo[7] = rs.getInt("stock_mal_estado");
                    productoInfo[8] = rs.getDouble("precio");
                    productoInfo[9] = rs.getString("estado");
                    productoInfo[10] = proveedorId;
                    productoInfo[11] = rs.getString("proveedor_nombre");
                    productoInfo[12] = rs.getString("proveedor_ruc");
                    productoInfo[13] = rs.getString("proveedor_telefono");
                    productoInfo[14] = rs.getString("proveedor_email");
                    productoInfo[15] = rs.getString("proveedor_direccion");
                    
                    productosPorProveedor.computeIfAbsent(proveedorId, k -> new ArrayList<>()).add(productoInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productosPorProveedor;
    }
    
    public List<Producto> listarConAjustes() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT p.*, " +
                     "COALESCE(p.stock_buen_estado, CASE WHEN p.estado = 'buen_estado' THEN p.stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(p.stock_mal_estado, CASE WHEN p.estado = 'mal_estado' THEN p.stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos p " +
                     "INNER JOIN movimientos m ON p.id = m.producto_id " +
                     "WHERE m.tipo = 'AJUSTE' " +
                     "ORDER BY p.categoria, p.nombre";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Producto> listarAtendidos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT p.*, " +
                     "COALESCE(p.stock_buen_estado, CASE WHEN p.estado = 'buen_estado' THEN p.stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(p.stock_mal_estado, CASE WHEN p.estado = 'mal_estado' THEN p.stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos p " +
                     "INNER JOIN movimientos m ON p.id = m.producto_id " +
                     "WHERE m.tipo = 'AJUSTE' " +
                     "ORDER BY p.categoria, p.nombre";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Producto> listarPendientesAtencion() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT p.*, " +
                     "COALESCE(p.stock_buen_estado, CASE WHEN p.estado = 'buen_estado' THEN p.stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(p.stock_mal_estado, CASE WHEN p.estado = 'mal_estado' THEN p.stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos p " +
                     "LEFT JOIN movimientos m ON p.id = m.producto_id AND m.tipo = 'AJUSTE' " +
                     "WHERE (p.estado = 'mal_estado' OR COALESCE(p.stock_mal_estado, 0) > 0) AND m.id IS NULL " +
                     "ORDER BY p.categoria, p.nombre";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Producto> listarCompletamenteAtendidos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT p.*, " +
                     "COALESCE(p.stock_buen_estado, CASE WHEN p.estado = 'buen_estado' THEN p.stock ELSE 0 END) as stock_buen_estado, " +
                     "COALESCE(p.stock_mal_estado, CASE WHEN p.estado = 'mal_estado' THEN p.stock ELSE 0 END) as stock_mal_estado " +
                     "FROM productos p " +
                     "INNER JOIN movimientos m ON p.id = m.producto_id " +
                     "WHERE p.estado = 'buen_estado' AND m.tipo = 'AJUSTE' " +
                     "ORDER BY p.categoria, p.nombre";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearProductoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public Map<String, Integer> obtenerProductosPorCategoria() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        String sql = "SELECT categoria, COUNT(*) as total FROM productos GROUP BY categoria ORDER BY total DESC";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getString("categoria"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }
    
    public Map<String, Integer> obtenerStockPorCategoria() {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        String sql = "SELECT categoria, SUM(stock) as total_stock FROM productos GROUP BY categoria ORDER BY total_stock DESC";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getString("categoria"), rs.getInt("total_stock"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }
    
    public List<Object[]> obtenerTopProductosPorStock(int limite) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT codigo, nombre, stock, categoria FROM productos ORDER BY stock DESC LIMIT ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] producto = new Object[4];
                producto[0] = rs.getString("codigo");
                producto[1] = rs.getString("nombre");
                producto[2] = rs.getInt("stock");
                producto[3] = rs.getString("categoria");
                lista.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public Map<String, Integer> obtenerEstadisticasStock() {
        Map<String, Integer> mapa = new HashMap<>();
        String sql = "SELECT " +
                     "SUM(COALESCE(stock_buen_estado, CASE WHEN estado = 'buen_estado' THEN stock ELSE 0 END)) as stock_bueno, " +
                     "SUM(COALESCE(stock_mal_estado, CASE WHEN estado = 'mal_estado' THEN stock ELSE 0 END)) as stock_malo " +
                     "FROM productos";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                mapa.put("buen_estado", rs.getInt("stock_bueno"));
                mapa.put("mal_estado", rs.getInt("stock_malo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }
} 