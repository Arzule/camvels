<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="modelo.Proveedor" %>
<%@ page import="modelo.Usuario" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    
    Proveedor proveedor = (Proveedor) request.getAttribute("proveedor");
    boolean edicion = (proveedor != null);
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= edicion ? "Editar" : "Agregar" %> Proveedor - Camvels</title>
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
                    <a href="<%= request.getContextPath() %>/ProveedorServlet" class="nav-link active">
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
            <h1 class="dashboard-title"><%= edicion ? "Editar" : "Agregar" %> Proveedor</h1>
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
                <i class="fas fa-truck"></i> <%= edicion ? "Editar" : "Crear" %> Proveedor
            </div>
            <div class="card-body">
                <form action="<%= request.getContextPath() %>/ProveedorServlet" method="post" class="formulario-moderno">
                    <% if (edicion) { %>
                        <input type="hidden" name="id" value="<%= proveedor.getId() %>">
                    <% } %>
                    
                    <!-- Sección: Información Básica -->
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-info-circle"></i>
                            <span>Información Básica</span>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-id-card"></i>
                                    RUC *
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" name="ruc" class="form-control" 
                                           value="<%= edicion ? proveedor.getRuc() : "" %>" 
                                           placeholder="Ingrese el RUC"
                                           required maxlength="11">
                                    <small class="form-help">Número de RUC del proveedor</small>
                                </div>
                            </div>
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-building"></i>
                                    Nombre *
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" name="nombre" class="form-control" 
                                           value="<%= edicion ? proveedor.getNombre() : "" %>" 
                                           placeholder="Ingrese el nombre del proveedor"
                                           required>
                                    <small class="form-help">Nombre o razón social del proveedor</small>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-address-book"></i>
                            <span>Información de Contacto</span>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-phone"></i>
                                    Teléfono *
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" name="telefono" class="form-control" 
                                           value="<%= edicion ? proveedor.getTelefono() : "" %>" 
                                           placeholder="Ingrese el teléfono"
                                           required>
                                    <small class="form-help">Número de teléfono de contacto</small>
                                </div>
                            </div>
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-envelope"></i>
                                    Email *
                                </label>
                                <div class="input-wrapper">
                                    <input type="email" name="email" class="form-control" 
                                           value="<%= edicion ? proveedor.getEmail() : "" %>" 
                                           placeholder="correo@ejemplo.com"
                                           required>
                                    <small class="form-help">Correo electrónico de contacto</small>
                                </div>
                            </div>
                        </div>
                        
                        <div class="mb-4">
                            <label class="form-label with-icon">
                                <i class="fas fa-map-marker-alt"></i>
                                Dirección *
                            </label>
                            <div class="input-wrapper">
                                <textarea name="direccion" class="form-control" rows="3" 
                                          placeholder="Ingrese la dirección completa del proveedor"
                                          required><%= edicion ? proveedor.getDireccion() : "" %></textarea>
                                <small class="form-help">Dirección completa del proveedor</small>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Botones de acción -->
                    <div class="form-actions">
                        <a href="<%= request.getContextPath() %>/ProveedorServlet" class="btn btn-secondary btn-lg">
                            <i class="fas fa-times"></i> Cancelar
                        </a>
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="fas fa-save"></i> <%= edicion ? "Actualizar" : "Agregar" %> Proveedor
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>