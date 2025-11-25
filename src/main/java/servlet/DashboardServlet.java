package servlet;

import modelo.Producto;
import dao.ProductoDAO;
import dao.MovimientoDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class DashboardServlet extends HttpServlet {
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        
        if ("stats".equals(accion)) {
            try {
                response.setContentType("application/json;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                
                Map<String, Object> estadisticas = new LinkedHashMap<>();
                
                estadisticas.put("productosPorCategoria", productoDAO.obtenerProductosPorCategoria());

                estadisticas.put("stockPorCategoria", productoDAO.obtenerStockPorCategoria());
                
                estadisticas.put("movimientosPorTipo", movimientoDAO.obtenerMovimientosPorTipo());
                
                estadisticas.put("cantidadPorTipo", movimientoDAO.obtenerCantidadPorTipo());
                
                estadisticas.put("movimientosPorFecha", movimientoDAO.obtenerMovimientosPorFecha(30));
                
                estadisticas.put("topProductos", productoDAO.obtenerTopProductosPorStock(10));
                
                estadisticas.put("estadisticasStock", productoDAO.obtenerEstadisticasStock());
                
                PrintWriter out = response.getWriter();
                out.print(convertirMapaAJson(estadisticas));
                out.flush();
                return;
            } catch (Exception e) {
                System.err.println("Error generando estadísticas JSON: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print("{\"error\": \"Error al generar estadísticas\"}");
                out.flush();
                return;
            }
        }
        
        System.out.println("=== DASHBOARD REQUEST ===");
        try {
            // Productos en stock
            System.out.println("Obteniendo productos...");
            List<Producto> productos = productoDAO.listar();
            int totalStock = productos.stream().mapToInt(Producto::getStock).sum();
            System.out.println("Total productos: " + productos.size());
            System.out.println("Total stock: " + totalStock);
            
            System.out.println("Obteniendo productos con stock bajo...");
            List<Producto> stockBajo = productoDAO.listarStockBajo();
            System.out.println("Productos con stock bajo: " + stockBajo.size());

            request.setAttribute("totalStock", totalStock);
            request.setAttribute("stockBajo", stockBajo);
            System.out.println("Redirigiendo a dashboard.jsp...");
            request.getRequestDispatcher("/vistas/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error en DashboardServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar el dashboard");
            request.getRequestDispatcher("/vistas/login.jsp").forward(request, response);
        }
    }
    
    // Convierte un Map a formato JSON manualmente
    private String convertirMapaAJson(Map<String, Object> mapa) {
        StringBuilder json = new StringBuilder("{");
        boolean primero = true;
        
        for (Map.Entry<String, Object> entry : mapa.entrySet()) {
            if (!primero) json.append(",");
            json.append("\"").append(escapeJson(entry.getKey())).append("\":");
            json.append(convertirObjetoAJson(entry.getValue()));
            primero = false;
        }
        
        json.append("}");
        return json.toString();
    }
    
    private String convertirObjetoAJson(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapa = (Map<String, Object>) obj;
            StringBuilder json = new StringBuilder("{");
            boolean primero = true;
            for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                if (!primero) json.append(",");
                json.append("\"").append(escapeJson(entry.getKey())).append("\":");
                json.append(convertirObjetoAJson(entry.getValue()));
                primero = false;
            }
            json.append("}");
            return json.toString();
        } else if (obj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> lista = (List<Object>) obj;
            StringBuilder json = new StringBuilder("[");
            boolean primero = true;
            for (Object item : lista) {
                if (!primero) json.append(",");
                json.append(convertirObjetoAJson(item));
                primero = false;
            }
            json.append("]");
            return json.toString();
        } else if (obj instanceof Object[]) {
            Object[] arr = (Object[]) obj;
            StringBuilder json = new StringBuilder("[");
            boolean primero = true;
            for (Object item : arr) {
                if (!primero) json.append(",");
                json.append(convertirObjetoAJson(item));
                primero = false;
            }
            json.append("]");
            return json.toString();
        } else if (obj instanceof String) {
            return "\"" + escapeJson(obj.toString()) + "\"";
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        } else {
            return "\"" + escapeJson(obj.toString()) + "\"";
        }
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
} 