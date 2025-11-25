<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Categoria" %>
<%@ page import="modelo.Usuario" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    
    List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");
    if (categorias == null) categorias = new java.util.ArrayList<>();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Categorías - Camvels</title>
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
                    <a href="<%= request.getContextPath() %>/MovimientoServlet" class="nav-link">
                        <i class="fas fa-exchange-alt"></i> Movimientos
                    </a>
                </li>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/CategoriaServlet" class="nav-link active">
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
            <h1 class="dashboard-title">Categorías</h1>
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
                    <i class="fas fa-tags"></i> Lista de Categorías
                </div>
                <a href="<%= request.getContextPath() %>/CategoriaServlet?accion=nuevo" class="btn btn-success btn-sm">
                    <i class="fas fa-plus"></i> Agregar Categoría
                </a>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Descripción</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (categorias.isEmpty()) { %>
                            <tr>
                                <td colspan="4" class="text-center py-4">
                                    <i class="fas fa-info-circle text-muted" style="font-size: 2rem; margin-bottom: 1rem;"></i>
                                    <p class="text-muted mb-0">No hay categorías registradas</p>
                                </td>
                            </tr>
                            <% } else { %>
                                <% for (Categoria c : categorias) { %>
                                <tr>
                                    <td><%= c.getId() %></td>
                                    <td><strong><%= c.getNombre() %></strong></td>
                                    <td><%= c.getDescripcion() %></td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/CategoriaServlet?accion=editar&id=<%= c.getId() %>" class="btn btn-primary btn-sm">
                                            <i class="fas fa-edit"></i> Editar
                                        </a>
                                        <% if ("admin".equals(usuario.getRol())) { %>
                                        <a href="<%= request.getContextPath() %>/CategoriaServlet?accion=eliminar&id=<%= c.getId() %>" class="btn btn-danger btn-sm" onclick="return confirm('¿Seguro de eliminar esta categoría?');">
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