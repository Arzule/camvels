<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Producto" %>
<%@ page import="modelo.Usuario" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    if (productos == null) productos = new java.util.ArrayList<>();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Productos - Camvels</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css?v=<%= System.currentTimeMillis() %>">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <a href="<%= request.getContextPath() %>/DashboardServlet" class="sidebar-brand">
                <i class="fas fa-cube"></i> Camvels
            </a>
        </div>
        <nav class="sidebar-nav">
            <ul class="nav">
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/DashboardServlet" class="nav-link">
                        <i class="fas fa-tachometer-alt"></i> Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/ProductoServlet" class="nav-link active">
                        <i class="fas fa-box"></i> Productos
                    </a>
                </li>
                <% if ("admin".equals(usuario.getRol()) || "supervisor".equals(usuario.getRol())) { %>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/MovimientoServlet" class="nav-link">
                        <i class="fas fa-exchange-alt"></i> Movimientos
                    </a>
                </li>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/CategoriaServlet" class="nav-link">
                        <i class="fas fa-tags"></i> Categorías
                    </a>
                </li>
                <% } %>
                <% if ("admin".equals(usuario.getRol())) { %>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/UsuarioServlet" class="nav-link">
                        <i class="fas fa-users"></i> Usuarios
                    </a>
                </li>
                <% } %>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/ProveedorServlet" class="nav-link">
                        <i class="fas fa-truck"></i> Proveedores
                    </a>
                </li>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/ReporteServlet" class="nav-link">
                        <i class="fas fa-chart-bar"></i> Reportes
                    </a>
                </li>
            </ul>
        </nav>
    </div>

    <div class="main-content">
        <div class="dashboard-header">
            <h1 class="dashboard-title">Productos</h1>
            <div class="user-info">
                <div>
                    <div class="user-greeting">Bienvenido,</div>
                    <div class="user-name"><%= usuario.getNombre() %></div>
                    <div class="user-role"><%= usuario.getRol() %></div>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-secondary">
                        <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
                    </a>
                </div>
            </div>
        </div>
        
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <div>
                    <i class="fas fa-box"></i> Lista de Productos
                </div>
                <a href="<%= request.getContextPath() %>/ProductoServlet?accion=nuevo" class="btn btn-success btn-sm">
                    <i class="fas fa-plus"></i> Agregar Producto
                </a>
            </div>
            <div class="card-body">
                <!-- Filtros de búsqueda -->
                <div class="filters-section mb-4">
                    <form method="get" action="<%= request.getContextPath() %>/ProductoServlet">
                        <div class="row g-3 align-items-end">
                            <div class="col-md-4">
                                <label class="form-label fw-semibold text-muted">Buscar</label>
                                <input type="text" name="busqueda" class="form-control form-control-sm" 
                                       placeholder="Buscar por código o nombre..." 
                                       value="<%= request.getParameter("busqueda") != null ? request.getParameter("busqueda") : "" %>">
                            </div>
                            <div class="col-md-2">
                                <label class="form-label fw-semibold text-muted">Categoría</label>
                                <select name="categoria" class="form-select form-select-sm">
                                    <option value="">Todas las categorías</option>
                                    <option value="Bebidas" <%= "Bebidas".equals(request.getParameter("categoria")) ? "selected" : "" %>>Bebidas</option>
                                    <option value="Lácteos" <%= "Lácteos".equals(request.getParameter("categoria")) ? "selected" : "" %>>Lácteos</option>
                                    <option value="Snacks" <%= "Snacks".equals(request.getParameter("categoria")) ? "selected" : "" %>>Snacks</option>
                                    <option value="Limpieza" <%= "Limpieza".equals(request.getParameter("categoria")) ? "selected" : "" %>>Limpieza</option>
                                    <option value="Higiene" <%= "Higiene".equals(request.getParameter("categoria")) ? "selected" : "" %>>Higiene</option>
                                    <option value="Abarrotes" <%= "Abarrotes".equals(request.getParameter("categoria")) ? "selected" : "" %>>Abarrotes</option>
                                    <option value="Congelados" <%= "Congelados".equals(request.getParameter("categoria")) ? "selected" : "" %>>Congelados</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <label class="form-label fw-semibold text-muted">Estado</label>
                                <select name="estado" class="form-select form-select-sm">
                                    <option value="">Todos los estados</option>
                                    <option value="buen_estado" <%= "buen_estado".equals(request.getParameter("estado")) ? "selected" : "" %>>Buen Estado</option>
                                    <option value="mal_estado" <%= "mal_estado".equals(request.getParameter("estado")) ? "selected" : "" %>>Mal Estado</option>
                                    <option value="con_ajustes" <%= "con_ajustes".equals(request.getParameter("estado")) ? "selected" : "" %>>Con Ajustes</option>
                                    <option value="pendientes_atencion" <%= "pendientes_atencion".equals(request.getParameter("estado")) ? "selected" : "" %>>Pendientes de Atención</option>
                                    <option value="completamente_atendidos" <%= "completamente_atendidos".equals(request.getParameter("estado")) ? "selected" : "" %>>Completamente Atendidos</option>
                                </select>
                            </div>
                            <div class="col-md-4 d-flex gap-2">
                                <button type="submit" class="badge bg-primary border-0 px-3 py-2">
                                    <i class="fas fa-filter"></i> Filtrar
                                </button>
                                <a href="<%= request.getContextPath() %>/ProductoServlet" class="badge bg-secondary border-0 px-3 py-2 text-decoration-none">
                                    <i class="fas fa-times"></i> Limpiar
                                </a>
                            </div>
                        </div>
                    </form>
                </div>
                
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Código</th>
                                <th>Nombre</th>
                                <th>Categoría</th>
                                <th>Stock Total</th>
                                <th>Stock Bueno</th>
                                <th>Stock Malo</th>
                                <th>Mínimo</th>
                                <th>Precio</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (productos.isEmpty()) { %>
                            <tr>
                                <td colspan="11" class="text-center py-4">
                                    <i class="fas fa-info-circle text-muted" style="font-size: 2rem; margin-bottom: 1rem;"></i>
                                    <p class="text-muted mb-0">No hay productos registrados</p>
                                </td>
                            </tr>
                            <% } else { %>
                                <% for (Producto p : productos) { %>
                                <tr>
                                    <td><%= p.getId() %></td>
                                    <td><strong><%= p.getCodigo() %></strong></td>
                                    <td><%= p.getNombre() %></td>
                                    <td><%= p.getCategoria() %></td>
                                    <td>
                                        <% if (p.getStock() <= p.getMinimo()) { %>
                                            <span class="text-danger font-weight-bold"><%= p.getStock() %></span>
                                        <% } else { %>
                                            <strong><%= p.getStock() %></strong>
                                        <% } %>
                                    </td>
                                    <td>
                                        <span class="text-success"><%= p.getStockBuenEstado() %></span>
                                    </td>
                                    <td>
                                        <% if (p.getStockMalEstado() > 0) { %>
                                            <span class="text-danger font-weight-bold"><%= p.getStockMalEstado() %></span>
                                        <% } else { %>
                                            <span class="text-muted">0</span>
                                        <% } %>
                                    </td>
                                    <td><%= p.getMinimo() %></td>
                                    <td>S/. <%= String.format("%.2f", p.getPrecio()) %></td>
                                    <td>
                                        <% if ("buen_estado".equals(p.getEstado())) { %>
                                            <span class="badge bg-success">Buen Estado</span>
                                        <% } else if ("mal_estado".equals(p.getEstado())) { %>
                                            <span class="badge bg-danger">Mal Estado</span>
                                        <% } else { %>
                                            <span class="badge bg-secondary">Sin Estado</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/ProductoServlet?accion=editar&id=<%= p.getId() %>" class="btn btn-primary btn-sm">
                                            <i class="fas fa-edit"></i> Editar
                                        </a>
                                        <% if ("admin".equals(usuario.getRol())) { %>
                                        <a href="<%= request.getContextPath() %>/ProductoServlet?accion=eliminar&id=<%= p.getId() %>" class="btn btn-danger btn-sm" onclick="return confirm('¿Seguro de eliminar este producto?');">
                                            <i class="fas fa-trash"></i> Eliminar
                                        </a>
                                        <% } %>
                                    </td>
                                </tr>
                                <% } %>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</body>
</html> 