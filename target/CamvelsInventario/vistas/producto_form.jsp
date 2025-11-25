<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="modelo.Producto" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="modelo.Proveedor" %>
<%@ page import="dao.ProveedorDAO" %>
<%@ page import="java.util.List" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    
    Producto producto = (Producto) request.getAttribute("producto");
    boolean edicion = (producto != null);
    
    List<Proveedor> proveedores = (List<Proveedor>) request.getAttribute("proveedores");
    if (proveedores == null) {
        ProveedorDAO proveedorDAO = new ProveedorDAO();
        proveedores = proveedorDAO.listar();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= edicion ? "Editar" : "Agregar" %> Producto - Camvels</title>
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
            <h1 class="dashboard-title"><%= edicion ? "Editar" : "Agregar" %> Producto</h1>
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
                <i class="fas fa-box"></i> <%= edicion ? "Editar" : "Crear" %> Producto
            </div>
            <div class="card-body">
                <form action="<%= request.getContextPath() %>/ProductoServlet" method="post" class="formulario-moderno">
                    <% if (edicion) { %>
                        <input type="hidden" name="id" value="<%= producto.getId() %>">
                    <% } %>
                    
                    <!-- Información Básica -->
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-info-circle"></i>
                            <span>Información Básica</span>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-barcode"></i>
                                    Código *
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" name="codigo" class="form-control" 
                                           value="<%= edicion ? producto.getCodigo() : "" %>" 
                                           placeholder="Ingrese el código del producto"
                                           required>
                                    <small class="form-help">Código único identificador del producto</small>
                                </div>
                            </div>
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-tag"></i>
                                    Nombre *
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" name="nombre" class="form-control" 
                                           value="<%= edicion ? producto.getNombre() : "" %>" 
                                           placeholder="Ingrese el nombre del producto"
                                           required>
                                    <small class="form-help">Nombre completo del producto</small>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-tags"></i>
                                    Categoría *
                                </label>
                                <div class="input-wrapper">
                                    <input type="text" name="categoria" class="form-control" 
                                           value="<%= edicion ? producto.getCategoria() : "" %>" 
                                           placeholder="Ingrese la categoría"
                                           required>
                                    <small class="form-help">Categoría a la que pertenece el producto</small>
                                </div>
                            </div>
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-check-circle"></i>
                                    Estado del Producto *
                                </label>
                                <div class="input-wrapper">
                                    <select name="estado" class="form-select" required>
                                        <option value="">Seleccionar estado</option>
                                        <option value="buen_estado" <%= edicion && "buen_estado".equals(producto.getEstado()) ? "selected" : "" %>>Buen Estado</option>
                                        <option value="mal_estado" <%= edicion && "mal_estado".equals(producto.getEstado()) ? "selected" : "" %>>Mal Estado</option>
                                    </select>
                                    <small class="form-help">Estado físico del producto</small>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Inventario -->
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-warehouse"></i>
                            <span>Inventario</span>
                        </div>
                        <div class="row">
                            <div class="col-md-3 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-check"></i>
                                    Stock Buen Estado *
                                </label>
                                <div class="input-wrapper">
                                    <input type="number" name="stock_buen_estado" id="stock_buen_estado" class="form-control" 
                                           value="<%= edicion ? producto.getStockBuenEstado() : "" %>" 
                                           placeholder="0"
                                           required min="0">
                                    <small class="form-help">Cantidad en buen estado</small>
                                </div>
                            </div>
                            <div class="col-md-3 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-times-circle"></i>
                                    Stock Mal Estado *
                                </label>
                                <div class="input-wrapper">
                                    <input type="number" name="stock_mal_estado" id="stock_mal_estado" class="form-control" 
                                           value="<%= edicion ? producto.getStockMalEstado() : "" %>" 
                                           placeholder="0"
                                           required min="0">
                                    <small class="form-help">Cantidad en mal estado</small>
                                </div>
                            </div>
                            <div class="col-md-3 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-cubes"></i>
                                    Stock Total
                                </label>
                                <div class="input-wrapper">
                                    <input type="number" name="stock" id="stock_total" class="form-control" readonly
                                           value="<%= edicion ? producto.getStock() : "" %>"
                                           placeholder="0">
                                    <small class="form-help">Se calcula automáticamente</small>
                                </div>
                            </div>
                            <div class="col-md-3 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-exclamation-triangle"></i>
                                    Stock Mínimo *
                                </label>
                                <div class="input-wrapper">
                                    <input type="number" name="minimo" class="form-control" 
                                           value="<%= edicion ? producto.getMinimo() : "" %>" 
                                           placeholder="0"
                                           required min="0">
                                    <small class="form-help">Stock mínimo requerido</small>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Precio y Proveedor -->
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-dollar-sign"></i>
                            <span>Precio y Proveedor</span>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-money-bill-wave"></i>
                                    Precio *
                                </label>
                                <div class="input-wrapper">
                                    <input type="number" name="precio" class="form-control" 
                                           value="<%= edicion ? producto.getPrecio() : "" %>" 
                                           placeholder="0.00"
                                           required min="0" step="0.01">
                                    <small class="form-help">Precio unitario del producto</small>
                                </div>
                            </div>
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-truck"></i>
                                    Proveedor
                                </label>
                                <div class="input-wrapper">
                                    <select name="proveedor_id" class="form-select">
                                        <option value="">Sin proveedor</option>
                                        <% for (Proveedor prov : proveedores) { %>
                                            <option value="<%= prov.getId() %>" 
                                                    <%= edicion && producto.getProveedorId() != null && producto.getProveedorId().equals(prov.getId()) ? "selected" : "" %>>
                                                <%= prov.getNombre() %> - <%= prov.getRuc() %>
                                            </option>
                                        <% } %>
                                    </select>
                                    <small class="form-help">Seleccione el proveedor del producto (opcional)</small>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <script>
                        function actualizarStockTotal() {
                            const stockBuenEstado = parseInt(document.getElementById('stock_buen_estado').value) || 0;
                            const stockMalEstado = parseInt(document.getElementById('stock_mal_estado').value) || 0;
                            const stockTotal = stockBuenEstado + stockMalEstado;
                            document.getElementById('stock_total').value = stockTotal;
                        }
                        
                        document.getElementById('stock_buen_estado').addEventListener('input', actualizarStockTotal);
                        document.getElementById('stock_mal_estado').addEventListener('input', actualizarStockTotal);
                        actualizarStockTotal(); // Calcular al cargar
                    </script>
                    
                    <!-- Botones de acción -->
                    <div class="form-actions">
                        <a href="<%= request.getContextPath() %>/ProductoServlet" class="btn btn-secondary btn-lg">
                            <i class="fas fa-times"></i> Cancelar
                        </a>
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="fas fa-save"></i> <%= edicion ? "Actualizar" : "Agregar" %> Producto
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>