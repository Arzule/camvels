package servlet;

import modelo.Producto;
import modelo.Movimiento;
import modelo.Usuario;
import dao.ProductoDAO;
import dao.MovimientoDAO;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class ProductoServlet extends HttpServlet {
    private ProductoDAO dao = new ProductoDAO();
    private MovimientoDAO movimientoDAO = new MovimientoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";
        switch (accion) {
            case "nuevo" -> {
                dao.ProveedorDAO proveedorDAO = new dao.ProveedorDAO();
                request.setAttribute("proveedores", proveedorDAO.listar());
                request.getRequestDispatcher("/vistas/producto_form.jsp").forward(request, response);
            }
            case "editar" -> {
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Producto prodEditar = dao.buscarPorId(idEditar);
                request.setAttribute("producto", prodEditar);
                dao.ProveedorDAO proveedorDAO = new dao.ProveedorDAO();
                request.setAttribute("proveedores", proveedorDAO.listar());
                request.getRequestDispatcher("/vistas/producto_form.jsp").forward(request, response);
            }
            case "eliminar" -> {
                HttpSession sesion = request.getSession(false);
                modelo.Usuario u = (modelo.Usuario) (sesion != null ? sesion.getAttribute("usuario") : null);
                if (u == null || "supervisor".equals(u.getRol())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "No autorizado para eliminar productos");
                    return;
                }
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("ProductoServlet");
            }
            default -> {
                String categoriaFiltro = request.getParameter("categoria");
                String estadoFiltro = request.getParameter("estado");
                String busqueda = request.getParameter("busqueda");
                
                List<Producto> lista;
                
                // Si hay búsqueda por texto, buscar por código o nombre
                if (busqueda != null && !busqueda.isEmpty()) {
                    lista = dao.buscarPorTexto(busqueda);
                    if (categoriaFiltro != null && !categoriaFiltro.isEmpty()) {
                        lista = lista.stream()
                            .filter(p -> p.getCategoria().equals(categoriaFiltro))
                            .collect(java.util.stream.Collectors.toList());
                    }
                    if (estadoFiltro != null && !estadoFiltro.isEmpty()) {
                        if ("con_ajustes".equals(estadoFiltro)) {
                            List<Producto> productosConAjustes = dao.listarConAjustes();
                            java.util.Set<Integer> idsConAjustes = new java.util.HashSet<>();
                            for (Producto p : productosConAjustes) {
                                idsConAjustes.add(p.getId());
                            }
                            lista = lista.stream()
                                .filter(p -> idsConAjustes.contains(p.getId()))
                                .collect(java.util.stream.Collectors.toList());
                        } else if ("completamente_atendidos".equals(estadoFiltro)) {
                            List<Producto> productosCompletos = dao.listarCompletamenteAtendidos();
                            java.util.Set<Integer> idsCompletos = new java.util.HashSet<>();
                            for (Producto p : productosCompletos) {
                                idsCompletos.add(p.getId());
                            }
                            lista = lista.stream()
                                .filter(p -> idsCompletos.contains(p.getId()))
                                .collect(java.util.stream.Collectors.toList());
                        } else if ("pendientes_atencion".equals(estadoFiltro)) {
                            List<Producto> productosPendientes = dao.listarPendientesAtencion();
                            java.util.Set<Integer> idsPendientes = new java.util.HashSet<>();
                            for (Producto p : productosPendientes) {
                                idsPendientes.add(p.getId());
                            }
                            lista = lista.stream()
                                .filter(p -> idsPendientes.contains(p.getId()))
                                .collect(java.util.stream.Collectors.toList());
                        } else {
                            lista = lista.stream()
                                .filter(p -> estadoFiltro.equals(p.getEstado()))
                                .collect(java.util.stream.Collectors.toList());
                        }
                    }
                } else if (categoriaFiltro != null && !categoriaFiltro.isEmpty()) {
                    if (estadoFiltro != null && !estadoFiltro.isEmpty()) {
                        if ("con_ajustes".equals(estadoFiltro)) {
                            List<Producto> productosConAjustes = dao.listarConAjustes();
                            lista = productosConAjustes.stream()
                                .filter(p -> p.getCategoria().equals(categoriaFiltro))
                                .collect(java.util.stream.Collectors.toList());
                        } else if ("completamente_atendidos".equals(estadoFiltro)) {
                            List<Producto> productosCompletos = dao.listarCompletamenteAtendidos();
                            lista = productosCompletos.stream()
                                .filter(p -> p.getCategoria().equals(categoriaFiltro))
                                .collect(java.util.stream.Collectors.toList());
                        } else if ("pendientes_atencion".equals(estadoFiltro)) {
                            List<Producto> productosPendientes = dao.listarPendientesAtencion();
                            lista = productosPendientes.stream()
                                .filter(p -> p.getCategoria().equals(categoriaFiltro))
                                .collect(java.util.stream.Collectors.toList());
                        } else {
                            lista = dao.listarPorCategoriaYEstado(categoriaFiltro, estadoFiltro);
                        }
                    } else {
                        lista = dao.listarPorCategoria(categoriaFiltro);
                    }
                } else if (estadoFiltro != null && !estadoFiltro.isEmpty()) {
                    if ("con_ajustes".equals(estadoFiltro)) {
                        lista = dao.listarConAjustes();
                    } else if ("completamente_atendidos".equals(estadoFiltro)) {
                        lista = dao.listarCompletamenteAtendidos();
                    } else if ("pendientes_atencion".equals(estadoFiltro)) {
                        lista = dao.listarPendientesAtencion();
                    } else {
                        lista = dao.listarPorEstado(estadoFiltro);
                    }
                } else {
                    lista = dao.listar();
                }
                
                request.setAttribute("productos", lista);
                request.getRequestDispatcher("/vistas/productos.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty() ? Integer.parseInt(request.getParameter("id")) : 0;
        String codigo = request.getParameter("codigo");
        String nombre = request.getParameter("nombre");
        String categoria = request.getParameter("categoria");
        
        int stockBuenEstado = 0;
        int stockMalEstado = 0;
        try {
            String stockBuenoStr = request.getParameter("stock_buen_estado");
            String stockMaloStr = request.getParameter("stock_mal_estado");
            if (stockBuenoStr != null && !stockBuenoStr.isEmpty()) {
                stockBuenEstado = Integer.parseInt(stockBuenoStr);
            }
            if (stockMaloStr != null && !stockMaloStr.isEmpty()) {
                stockMalEstado = Integer.parseInt(stockMaloStr);
            }
        } catch (NumberFormatException e) {
            int stock = Integer.parseInt(request.getParameter("stock"));
            String estado = request.getParameter("estado");
            if ("mal_estado".equals(estado)) {
                stockMalEstado = stock;
                stockBuenEstado = 0;
            } else {
                stockBuenEstado = stock;
                stockMalEstado = 0;
            }
        }
        
        int minimo = Integer.parseInt(request.getParameter("minimo"));
        double precio = Double.parseDouble(request.getParameter("precio"));
        String estado = request.getParameter("estado");
        
        Integer proveedorId = null;
        String proveedorIdStr = request.getParameter("proveedor_id");
        if (proveedorIdStr != null && !proveedorIdStr.isEmpty()) {
            try {
                proveedorId = Integer.valueOf(proveedorIdStr);
            } catch (NumberFormatException e) {
                proveedorId = null;
            }
        }

        Producto p = new Producto();
        p.setId(id);
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setStockBuenEstado(stockBuenEstado);
        p.setStockMalEstado(stockMalEstado);
        p.actualizarStockTotal();
        p.setMinimo(minimo);
        p.setPrecio(precio);
        p.setEstado(estado);
        p.setProveedorId(proveedorId);

        HttpSession sesion = request.getSession(false);
        Usuario usuario = (Usuario) (sesion != null ? sesion.getAttribute("usuario") : null);
        
        if (id == 0) {
            boolean creado = dao.agregar(p);
            if (creado && p.getId() > 0 && p.getStock() > 0 && usuario != null) {
                Movimiento movimientoInicial = new Movimiento();
                movimientoInicial.setTipo("ENTRADA");
                movimientoInicial.setProductoId(p.getId());
                movimientoInicial.setCantidad(p.getStock());
                movimientoInicial.setUsuarioId(usuario.getId());
                movimientoInicial.setObservaciones("Stock inicial al crear el producto");
                movimientoDAO.agregar(movimientoInicial);
            }
        } else {
            dao.actualizar(p);
        }
        response.sendRedirect("ProductoServlet");
    }
} 