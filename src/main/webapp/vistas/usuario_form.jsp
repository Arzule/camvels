<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="modelo.Usuario" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuarioSesion = (Usuario) sesion.getAttribute("usuario");
    if (usuarioSesion == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    
    Usuario usuario = (Usuario) request.getAttribute("usuario");
    boolean edicion = (usuario != null);
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= edicion ? "Editar" : "Agregar" %> Usuario - Camvels</title>
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
                <% if ("admin".equals(usuarioSesion.getRol()) || "supervisor".equals(usuarioSesion.getRol())) { %>
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
                <% if ("admin".equals(usuarioSesion.getRol())) { %>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/UsuarioServlet" class="nav-link active">
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
            <h1 class="dashboard-title"><%= edicion ? "Editar" : "Agregar" %> Usuario</h1>
            <div class="user-info">
                <div>
                    <div class="user-greeting">Bienvenido,</div>
                    <div class="user-name"><%= usuarioSesion.getNombre() %></div>
                    <div class="user-role"><%= usuarioSesion.getRol() %></div>
                </div>
            </div>
        </div>
        
        <div class="card">
            <div class="card-header">
                <i class="fas fa-users"></i> <%= edicion ? "Editar" : "Crear" %> Usuario
            </div>
            <div class="card-body">
                <form action="<%= request.getContextPath() %>/UsuarioServlet" method="post" class="formulario-moderno">
                    <% if (edicion) { %>
                        <input type="hidden" name="id" value="<%= usuario.getId() %>">
                    <% } %>
                    
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-info-circle"></i>
                            <span>Información del Usuario</span>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-user"></i>
                                    Usuario *
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" name="usuario" class="form-control" 
                                           value="<%= edicion ? usuario.getUsuario() : "" %>" 
                                           placeholder="Ingrese el nombre de usuario"
                                           required>
                                    <small class="form-help">Nombre de usuario para acceder al sistema</small>
                                </div>
                            </div>
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-id-badge"></i>
                                    Nombre *
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" name="nombre" class="form-control" 
                                           value="<%= edicion ? usuario.getNombre() : "" %>" 
                                           placeholder="Ingrese el nombre completo"
                                           required>
                                    <small class="form-help">Nombre completo del usuario</small>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-shield-alt"></i>
                            <span>Credenciales y Permisos</span>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-lock"></i>
                                    Contraseña *
                                </label>
                                <div class="input-wrapper">
                                    <input type="password" name="password" class="form-control" 
                                           value="<%= edicion ? usuario.getPassword() : "" %>" 
                                           placeholder="Ingrese la contraseña"
                                           required>
                                    <small class="form-help">Contraseña de acceso al sistema</small>
                                </div>
                            </div>
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-user-tag"></i>
                                    Rol *
                                </label>
                                <div class="input-wrapper">
                                    <select name="rol" class="form-select" required>
                                        <option value="">Seleccione un rol</option>
                                        <option value="admin" <%= edicion && "admin".equals(usuario.getRol()) ? "selected" : "" %>>Administrador</option>
                                        <option value="almacen" <%= edicion && "almacen".equals(usuario.getRol()) ? "selected" : "" %>>Almacén</option>
                                        <option value="supervisor" <%= edicion && "supervisor".equals(usuario.getRol()) ? "selected" : "" %>>Supervisor</option>
                                    </select>
                                    <small class="form-help">Rol de usuario en el sistema</small>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Botones de acción -->
                    <div class="form-actions">
                        <a href="<%= request.getContextPath() %>/UsuarioServlet" class="btn btn-secondary btn-lg">
                            <i class="fas fa-times"></i> Cancelar
                        </a>
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="fas fa-save"></i> <%= edicion ? "Actualizar" : "Agregar" %> Usuario
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>