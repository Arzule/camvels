<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Producto" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    int totalStock = request.getAttribute("totalStock") != null ? (Integer) request.getAttribute("totalStock") : 0;
    List<Producto> stockBajo = (List<Producto>) request.getAttribute("stockBajo");
    if (stockBajo == null) stockBajo = new java.util.ArrayList<>();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Camvels</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css?v=125">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <script>
        // Pasar el contexto de la aplicación al JavaScript externo
        window.CONTEXT_PATH = '<%= request.getContextPath() %>';
    </script>
    <script src="<%= request.getContextPath() %>/js/dashboard.js?v=1.0"></script>
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
                    <a href="<%= request.getContextPath() %>/DashboardServlet" class="nav-link active">
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
        <!-- Dashboard Header -->
        <div class="dashboard-header">
            <h1 class="dashboard-title">Dashboard</h1>
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

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon primary">
                    <i class="fas fa-boxes"></i>
                </div>
                <div class="stat-label">Productos en Stock</div>
                <div class="stat-value"><%= totalStock %></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon danger">
                    <i class="fas fa-exclamation-triangle"></i>
                </div>
                <div class="stat-label">Stock Bajo</div>
                <div class="stat-value"><%= stockBajo.size() %></div>
            </div>
        </div>

        <!-- Sección de Gráficos -->
        <div class="row" style="display: flex; flex-wrap: wrap; margin-bottom: 2rem; gap: 1.5rem;">
            <div class="chart-card" style="flex: 1; min-width: 300px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); padding: 1.5rem;">
                <h5 style="margin-bottom: 1rem; color: #2c3e50;"><i class="fas fa-chart-pie"></i> Productos por Categoría</h5>
                <canvas id="chartProductosCategoria"></canvas>
            </div>
            <div class="chart-card" style="flex: 1; min-width: 300px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); padding: 1.5rem;">
                <h5 style="margin-bottom: 1rem; color: #2c3e50;"><i class="fas fa-chart-bar"></i> Stock por Categoría</h5>
                <canvas id="chartStockCategoria"></canvas>
            </div>
        </div>

        <div class="row" style="display: flex; flex-wrap: wrap; margin-bottom: 2rem; gap: 1.5rem;">
            <div class="chart-card" style="flex: 1; min-width: 300px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); padding: 1.5rem;">
                <h5 style="margin-bottom: 1rem; color: #2c3e50;"><i class="fas fa-exchange-alt"></i> Movimientos por Tipo</h5>
                <canvas id="chartMovimientosTipo"></canvas>
            </div>
            <div class="chart-card" style="flex: 1; min-width: 300px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); padding: 1.5rem;">
                <h5 style="margin-bottom: 1rem; color: #2c3e50;"><i class="fas fa-chart-pie"></i> Stock: Bueno vs Mal Estado</h5>
                <canvas id="chartStockEstado"></canvas>
            </div>
        </div>

        <div class="row" style="display: flex; flex-wrap: wrap; margin-bottom: 2rem; gap: 1.5rem;">
            <div class="chart-card" style="flex: 1; min-width: 500px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); padding: 1.5rem;">
                <h5 style="margin-bottom: 1rem; color: #2c3e50;"><i class="fas fa-chart-line"></i> Movimientos (Últimos 30 días)</h5>
                <canvas id="chartMovimientosFecha"></canvas>
            </div>
        </div>

        <div class="row" style="display: flex; flex-wrap: wrap; margin-bottom: 2rem; gap: 1.5rem;">
            <div class="chart-card" style="flex: 1; min-width: 500px; background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); padding: 1.5rem;">
                <h5 style="margin-bottom: 1rem; color: #2c3e50;"><i class="fas fa-trophy"></i> Top 10 Productos por Stock</h5>
                <canvas id="chartTopProductos"></canvas>
            </div vrijgegeven>
        </div>

        <div class="card">
            <div class="card-header">
                <i class="fas fa-exclamation-triangle"></i> Productos con Stock Bajo
            </div>
            <div class="card-body">
                <% if (stockBajo.size() > 0) { %>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Producto</th>
                                <th>Categoría</th>
                                <th>Stock</th>
                                <th>Mínimo</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Producto p : stockBajo) { %>
                            <tr>
                                <td><strong><%= p.getCodigo() %></strong></td>
                                <td><%= p.getNombre() %></td>
                                <td><%= p.getCategoria() %></td>
                                <td>
                                    <span class="text-danger font-weight-bold"><%= p.getStock() %></span>
                                </td>
                                <td><%= p.getMinimo() %></td>
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
                                    <a href="ProductoServlet?accion=editar&id=<%= p.getId() %>" class="btn btn-primary btn-sm">
                                        <i class="fas fa-edit"></i> Editar
                                    </a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                <div class="text-center py-4">
                    <i class="fas fa-check-circle text-success" style="font-size: 3rem; margin-bottom: 1rem;"></i>
                    <h5 class="text-muted">No hay productos con stock bajo</h5>
                    <p class="text-muted">Todos los productos tienen stock suficiente.</p>
                </div>
                <% } %>
            </div>
        </div>
        <div class="card">
            <div class="card-header">
                <i class="fas fa-info-circle"></i> Información del Sistema
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h6 class="mb-3" style="font-weight: 600; color: #2c3e50;">Funcionalidades Disponibles</h6>
                        <div class="d-flex flex-wrap gap-2">
                            <span class="badge bg-primary">Dashboard</span>
                            <span class="badge bg-primary">Productos</span>
                            <% if ("admin".equals(usuario.getRol()) || "supervisor".equals(usuario.getRol())) { %>
                            <span class="badge bg-primary">Movimientos</span>
                            <span class="badge bg-primary">Categorías</span>
                            <% } %>
                            <% if ("admin".equals(usuario.getRol())) { %>
                            <span class="badge bg-primary">Usuarios</span>
                            <% } %>
                            <span class="badge bg-primary">Proveedores</span>
                            <span class="badge bg-primary">Reportes</span>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <h6 class="mb-3" style="font-weight: 600; color: #2c3e50;">Estado del Sistema</h6>
                        <div class="d-flex align-items-center mb-2">
                            <span class="badge bg-success me-2">Activo</span>
                            <span style="font-weight: 500;">Sistema de Inventario Camvels</span>
                        </div>
                        <small class="text-muted" style="display: block; margin-top: 0.5rem;">
                            <i class="fas fa-clock"></i>
                            Última actualización: <%= new java.util.Date() %>
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
