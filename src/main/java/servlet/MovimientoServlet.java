package servlet;

import modelo.Movimiento;
import modelo.Usuario;
import dao.MovimientoDAO;
import dao.ProductoDAO;
import modelo.Producto;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class MovimientoServlet extends HttpServlet {
    private MovimientoDAO movimientoDAO = new MovimientoDAO();
    private ProductoDAO productoDAO = new ProductoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("=== MOVIMIENTOS REQUEST ===");
        try {
            // Verificar autenticación
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (Usuario) sesion.getAttribute("usuario");
            
            if (usuario == null) {
                response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
                return;
            }
            
            if (!"admin".equals(usuario.getRol()) && !"supervisor".equals(usuario.getRol())) {
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }
            
            String accion = request.getParameter("accion");
            String filtroTipo = request.getParameter("tipo");
            String filtroProducto = request.getParameter("producto");
            
            if ("nuevo".equals(accion)) {
                List<Producto> productos = productoDAO.listar();
                request.setAttribute("productos", productos);
                request.setAttribute("accion", "nuevo");
                request.getRequestDispatcher("/vistas/movimiento_form.jsp").forward(request, response);
                return;
            }
            
            List<Movimiento> movimientos;
            
            if (filtroTipo != null && !filtroTipo.isEmpty()) {
                movimientos = movimientoDAO.listarPorTipo(filtroTipo);
            } else if (filtroProducto != null && !filtroProducto.isEmpty()) {
                movimientos = movimientoDAO.listarPorProducto(Integer.parseInt(filtroProducto));
            } else {
                movimientos = movimientoDAO.listar();
            }
            
            List<Producto> productos = productoDAO.listar();
            
            request.setAttribute("movimientos", movimientos);
            request.setAttribute("productos", productos);
            request.getRequestDispatcher("/vistas/movimientos.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error en MovimientoServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar los movimientos");
            request.getRequestDispatcher("/DashboardServlet").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("=== MOVIMIENTOS POST ===");
        try {
            // Verificar autenticación
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (Usuario) sesion.getAttribute("usuario");
            
            if (usuario == null) {
                response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
                return;
            }
            
            if (!"admin".equals(usuario.getRol()) && !"supervisor".equals(usuario.getRol())) {
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                return;
            }
            
            String accion = request.getParameter("accion");
            
            if ("nuevo".equals(accion)) {
                Movimiento movimiento = new Movimiento();
                movimiento.setTipo(request.getParameter("tipo"));
                int productoId = Integer.parseInt(request.getParameter("producto_id"));
                movimiento.setProductoId(productoId);
                int cantidad = Integer.parseInt(request.getParameter("cantidad"));
                movimiento.setCantidad(cantidad);
                movimiento.setUsuarioId(usuario.getId());
                movimiento.setObservaciones(request.getParameter("observaciones"));
                
                if (cantidad <= 0) {
                    response.sendRedirect(request.getContextPath() + "/MovimientoServlet?error=La+cantidad+debe+ser+mayor+a+cero");
                    return;
                }
                
                Producto productoValidar = productoDAO.buscarPorId(productoId);
                if (productoValidar == null) {
                    response.sendRedirect(request.getContextPath() + "/MovimientoServlet?error=Producto+no+encontrado");
                    return;
                }
                
                String tipo = movimiento.getTipo();
                if ("SALIDA".equals(tipo)) {
                    int stockTotal = productoValidar.getStock();
                    int stockBueno = productoValidar.getStockBuenEstado();
                    int stockMalo = productoValidar.getStockMalEstado();
                    
                    if (stockTotal < cantidad) {
                        response.sendRedirect(request.getContextPath() + 
                            "/MovimientoServlet?error=Stock+insuficiente.+Stock+disponible%3A+" + 
                            stockTotal + "+%28Stock+bueno%3A+" + stockBueno + 
                            "+%2C+Stock+malo%3A+" + stockMalo + "%29");
                        return;
                    }
                }
                
                if (movimientoDAO.agregar(movimiento)) {
                    Producto producto = productoDAO.buscarPorId(movimiento.getProductoId());
                    if (producto != null) {
                        if ("ENTRADA".equals(tipo)) {
                            producto.setStockBuenEstado(producto.getStockBuenEstado() + cantidad);
                        } else if ("SALIDA".equals(tipo)) {
                            int stockBueno = producto.getStockBuenEstado();
                            if (stockBueno >= cantidad) {
                                producto.setStockBuenEstado(stockBueno - cantidad);
                            } else {
                                producto.setStockBuenEstado(0);
                                producto.setStockMalEstado(Math.max(0, producto.getStockMalEstado() - (cantidad - stockBueno)));
                            }
                        } else if ("AJUSTE".equals(tipo)) {
                            int stockMalo = producto.getStockMalEstado();
                            if (stockMalo >= cantidad) {
                                producto.setStockMalEstado(stockMalo - cantidad);
                                producto.setStockBuenEstado(producto.getStockBuenEstado() + cantidad);
                            } else {
                                producto.setStockMalEstado(0);
                                producto.setStockBuenEstado(producto.getStockBuenEstado() + stockMalo + (cantidad - stockMalo));
                        }
                        }
                        
                        producto.actualizarStockTotal();
                        
                        if (producto.getStockMalEstado() > 0) {
                            producto.setEstado("mal_estado");
                        } else if (producto.getStockBuenEstado() > 0) {
                            producto.setEstado("buen_estado");
                        }
                        
                        productoDAO.actualizar(producto);
                    }
                    
                    response.sendRedirect(request.getContextPath() + "/MovimientoServlet?mensaje=Movimiento+creado+exitosamente");
                } else {
                    response.sendRedirect(request.getContextPath() + "/MovimientoServlet?error=Error+al+crear+movimiento");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/MovimientoServlet");
            }
            
        } catch (Exception e) {
            System.err.println("Error en MovimientoServlet POST: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/MovimientoServlet?error=Error+en+el+proceso");
        }
    }
}
