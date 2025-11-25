<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="modelo.Categoria" %>
<%@ page import="modelo.Usuario" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    
    Categoria categoria = (Categoria) request.getAttribute("categoria");
    boolean edicion = (categoria != null);
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= edicion ? "Editar" : "Agregar" %> Categoría - Camvels</title>
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
            <h1 class="dashboard-title"><%= edicion ? "Editar" : "Agregar" %> Categoría</h1>
            <div class="user-info">
                <div>
                    <div class="user-greeting">Bienvenido,</div>
                    <div class="user-name"><%= usuario.getNombre() %></div>
                    <div class="user-role"><%= usuario.getRol() %></div>
                </div>
            </div>
        </div>
        
        <div class="card">
            <div class="card-header">
                <i class="fas fa-tags"></i> <%= edicion ? "Editar" : "Crear" %> Categoría
            </div>
            <div class="card-body">
                <form action="<%= request.getContextPath() %>/CategoriaServlet" method="post" class="formulario-moderno">
                    <% if (edicion) { %>
                        <input type="hidden" name="id" value="<%= categoria.getId() %>">
                    <% } %>
                    
                    <!-- Sección: Información de la Categoría -->
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-info-circle"></i>
                            <span>Información de la Categoría</span>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-tag"></i>
                                    Nombre *
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" name="nombre" class="form-control" 
                                           value="<%= edicion ? categoria.getNombre() : "" %>" 
                                           placeholder="Ingrese el nombre de la categoría"
                                           required>
                                    <small class="form-help">Nombre de la categoría</small>
                                </div>
                            </div>
                        </div>
                        
                        <div class="mb-4">
                            <label class="form-label with-icon">
                                <i class="fas fa-align-left"></i>
                                Descripción *
                            </label>
                            <div class="input-wrapper">
                                <textarea name="descripcion" class="form-control" rows="4" 
                                          placeholder="Ingrese la descripción detallada de la categoría"
                                          required><%= edicion ? categoria.getDescripcion() : "" %></textarea>
                                <small class="form-help">Descripción detallada de la categoría</small>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Botones de acción -->
                    <div class="form-actions">
                        <a href="<%= request.getContextPath() %>/CategoriaServlet" class="btn btn-secondary btn-lg">
                            <i class="fas fa-times"></i> Cancelar
                        </a>
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="fas fa-save"></i> <%= edicion ? "Actualizar" : "Agregar" %> Categoría
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>