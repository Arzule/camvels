<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="modelo.Movimiento" %>
<%@ page import="modelo.Producto" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    
    List<Movimiento> movimientos = (List<Movimiento>) request.getAttribute("movimientos");
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    if (movimientos == null) movimientos = new java.util.ArrayList<>();
    if (productos == null) productos = new java.util.ArrayList<>();
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Movimientos - Camvels</title>
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
                    <a href="<%= request.getContextPath() %>/ProductoServlet" class="nav-link">
                        <i class="fas fa-box"></i> Productos
                    </a>
                </li>
                <% if ("admin".equals(usuario.getRol()) || "supervisor".equals(usuario.getRol())) { %>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/MovimientoServlet" class="nav-link active">
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
            <h1 class="dashboard-title">Movimientos de Inventario</h1>
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
                    <i class="fas fa-exchange-alt"></i> Historial de Movimientos
                </div>
                <a href="<%= request.getContextPath() %>/MovimientoServlet?accion=nuevo" class="btn btn-success btn-sm">
                    <i class="fas fa-plus"></i> Nuevo Movimiento
                </a>
            </div>
            <div class="card-body">
        <!-- Filtros -->
                <div class="filters-section mb-4">
                    <form method="get" action="<%= request.getContextPath() %>/MovimientoServlet">
                        <div class="row g-3 align-items-end">
                            <div class="col-md-3">
                                <label class="form-label fw-semibold text-muted">Tipo</label>
                                <select name="tipo" class="form-select form-select-sm">
                        <option value="">Todos los tipos</option>
                        <option value="ENTRADA">Entrada</option>
                        <option value="SALIDA">Salida</option>
                        <option value="AJUSTE">Ajuste</option>
                    </select>
                </div>
                            <div class="col-md-3">
                                <label class="form-label fw-semibold text-muted">Producto</label>
                                <select name="producto" class="form-select form-select-sm">
                        <option value="">Todos los productos</option>
                                    <% for (Producto p : productos) { %>
                                    <option value="<%= p.getId() %>"><%= p.getCodigo() %> - <%= p.getNombre() %></option>
                                    <% } %>
                    </select>
                </div>
                            <div class="col-md-6 d-flex gap-2">
                                <button type="submit" class="badge bg-primary border-0 px-3 py-2">
                                    <i class="fas fa-filter"></i> Filtrar
                                </button>
                                <a href="<%= request.getContextPath() %>/MovimientoServlet" class="badge bg-secondary border-0 px-3 py-2 text-decoration-none">
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
                    <th>Fecha</th>
                    <th>Tipo</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Usuario</th>
                    <th>Observaciones</th>
                </tr>
            </thead>
            <tbody>
                            <% if (movimientos.isEmpty()) { %>
                            <tr>
                                <td colspan="7" class="text-center py-4">
                                    <i class="fas fa-info-circle text-muted" style="font-size: 2rem; margin-bottom: 1rem;"></i>
                                    <p class="text-muted mb-0">No hay movimientos registrados</p>
                                </td>
                </tr>
                            <% } else { %>
                                <% for (Movimiento mov : movimientos) { %>
                                <tr>
                                    <td><%= mov.getId() %></td>
                                    <td><%= sdf.format(mov.getFecha()) %></td>
                                    <td>
                                        <% if ("ENTRADA".equals(mov.getTipo())) { %>
                                            <span class="badge bg-success">Entrada</span>
                                        <% } else if ("SALIDA".equals(mov.getTipo())) { %>
                                            <span class="badge bg-danger">Salida</span>
                                        <% } else { %>
                                            <span class="badge bg-warning">Ajuste</span>
                                        <% } %>
                                    </td>
                                    <td><%= mov.getProductoCodigo() %> - <%= mov.getProductoNombre() %></td>
                                    <td class="<% if ("ENTRADA".equals(mov.getTipo())) { %>text-success<% } else if ("SALIDA".equals(mov.getTipo())) { %>text-danger<% } else { %>text-warning<% } %>">
                                        <strong><%= (mov.getTipo().equals("ENTRADA") ? "+" : mov.getTipo().equals("SALIDA") ? "-" : "") + mov.getCantidad() %></strong>
                                    </td>
                                    <td><%= mov.getUsuarioNombre() %></td>
                                    <td><%= mov.getObservaciones() != null ? mov.getObservaciones() : "-" %></td>
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