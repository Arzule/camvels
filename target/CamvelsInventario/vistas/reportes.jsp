<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Proveedor" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    
    List<Proveedor> proveedores = (List<Proveedor>) request.getAttribute("proveedores");
    if (proveedores == null) proveedores = new java.util.ArrayList<>();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Reportes - Camvels</title>
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
                    <a href="<%= request.getContextPath() %>/ProveedorServlet" class="nav-link">
                        <i class="fas fa-truck"></i> Proveedores
                    </a>
                </li>
                <li class="nav-item">
                    <a href="<%= request.getContextPath() %>/ReporteServlet" class="nav-link active">
                        <i class="fas fa-chart-bar"></i> Reportes
                    </a>
                </li>
            </ul>
        </nav>
    </div>

    <div class="main-content">
        <div class="dashboard-header">
            <h1 class="dashboard-title">Reportes</h1>
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
        
        <div class="container">
            <% if (request.getAttribute("mensaje") != null) { %>
            <div class="alert alert-<%= request.getAttribute("tipo_mensaje") %> alert-dismissible fade show" role="alert">
                <%= request.getAttribute("mensaje") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>
            
            <div class="row mt-4">
                <div class="col-md-6 mb-4">
                    <div class="card report-card">
                        <div class="card-body text-center">
                            <div class="report-icon">
                                <i class="fas fa-box"></i>
                            </div>
                            <h5 class="card-title">Reporte de Productos</h5>
                            <p class="card-text">Genera un reporte completo de todos los productos en el inventario incluyendo código, nombre, categoría, stock, precio y mínimo.</p>
                            <a href="<%= request.getContextPath() %>/ReporteServlet?tipo=productos" class="btn btn-primary btn-report">
                                <i class="fas fa-download"></i> Descargar PDF
                            </a>
                        </div>
                    </div>
                </div>
                
                <div class="col-md-6 mb-4">
                    <div class="card report-card">
                        <div class="card-body text-center">
                            <div class="report-icon warning">
                                <i class="fas fa-exclamation-triangle"></i>
                            </div>
                            <h5 class="card-title">Reporte de Stock Bajo</h5>
                            <p class="card-text">Genera un reporte de productos con stock bajo o agotado para facilitar la reposición de inventario.</p>
                            <a href="<%= request.getContextPath() %>/ReporteServlet?tipo=stock_bajo" class="btn btn-warning btn-report">
                                <i class="fas fa-download"></i> Descargar PDF
                            </a>
                        </div>
                    </div>
                </div>
                
                <div class="col-md-6 mb-4">
                    <div class="card report-card">
                        <div class="card-body text-center">
                            <div class="report-icon info">
                                <i class="fas fa-exchange-alt"></i>
                            </div>
                            <h5 class="card-title">Reporte de Movimientos</h5>
                            <p class="card-text">Genera un reporte de todos los movimientos de inventario incluyendo entrada, salida y ajustes de stock.</p>
                            <a href="<%= request.getContextPath() %>/ReporteServlet?tipo=movimientos" class="btn btn-info btn-report">
                                <i class="fas fa-download"></i> Descargar PDF
                            </a>
                        </div>
                    </div>
                </div>
                
                <div class="col-md-6 mb-4">
                    <div class="card report-card">
                        <div class="card-body text-center">
                            <div class="report-icon success">
                                <i class="fas fa-truck"></i>
                            </div>
                            <h5 class="card-title">Reporte de Proveedores</h5>
                            <p class="card-text">Genera un reporte con la información completa de todos los proveedores registrados en el sistema.</p>
                            <a href="<%= request.getContextPath() %>/ReporteServlet?tipo=proveedores" class="btn btn-success btn-report">
                                <i class="fas fa-download"></i> Descargar PDF
                            </a>
                        </div>
                    </div>
                </div>
                
                <div class="col-md-6 mb-4">
                    <div class="card report-card" style="border-left: 4px solid #dc3545;">
                        <div class="card-body">
                            <div class="text-center mb-3">
                                <div class="report-icon" style="color: #dc3545;">
                                    <i class="fas fa-file-invoice"></i>
                                </div>
                                <h5 class="card-title">Reporte para Proveedores</h5>
                                <p class="card-text">Genera un reporte específico para enviar a proveedores con productos que tienen stock bajo o están en mal estado.</p>
                            </div>
                            
                            <form method="get" action="<%= request.getContextPath() %>/ReporteServlet" style="margin-top: 20px;" id="formReporte">
                                <input type="hidden" name="tipo" value="proveedores_productos">
                                <!-- Campos ocultos que se actualizan con el estado de los checkboxes -->
                                <input type="hidden" name="incluir_stock_bajo" value="1" id="hidden_stock_bajo">
                                <input type="hidden" name="incluir_mal_estado" value="1" id="hidden_mal_estado">
                                
                                <div class="mb-3">
                                    <label for="proveedor_id" class="form-label" style="font-weight: 500;">
                                        <i class="fas fa-truck"></i> Seleccionar Proveedor:
                                    </label>
                                    <select class="form-select" name="proveedor_id" id="proveedor_id" required>
                                        <option value="0">-- Todos los proveedores --</option>
                                        <% for (Proveedor p : proveedores) { %>
                                        <option value="<%= p.getId() %>">
                                            <%= p.getNombre() %> (<%= p.getRuc() %>)
                                        </option>
                                        <% } %>
                                    </select>
                                    <small class="form-text text-muted" style="display: block; margin-top: 5px;">
                                        Seleccione un proveedor específico o "Todos" para ver todos los proveedores con productos con problemas.
                                    </small>
                                </div>
                                
                                <div class="mb-3">
                                    <label class="form-label" style="font-weight: 500;">
                                        <i class="fas fa-filter"></i> Tipo de Productos a Incluir:
                                    </label>
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="incluir_stock_bajo" checked>
                                        <label class="form-check-label" for="incluir_stock_bajo">
                                            <strong>Productos con Stock Bajo</strong>
                                            <small class="text-muted d-block">Incluye productos que necesitan reposición (cantidad faltante calculada según stock mínimo)</small>
                                        </label>
                                    </div>
                                    <div class="form-check mt-2">
                                        <input class="form-check-input" type="checkbox" id="incluir_mal_estado" checked>
                                        <label class="form-check-label" for="incluir_mal_estado">
                                            <strong>Productos en Mal Estado</strong>
                                            <small class="text-muted d-block">Incluye productos que requieren revisión por estar en mal estado</small>
                                        </label>
                                    </div>
                                    <small class="form-text text-muted" style="display: block; margin-top: 5px;">
                                        Seleccione al menos un tipo de producto para generar el reporte.
                                    </small>
                                </div>
                                
                                <button type="submit" class="btn btn-danger btn-report w-100" id="btnGenerar">
                                    <i class="fas fa-download"></i> Generar y Descargar PDF
                                </button>
                            </form>
                            
                            <script>
                                // Validar que al menos un checkbox esté seleccionado y actualizar campos ocultos
                                const checkboxStockBajo = document.getElementById('incluir_stock_bajo');
                                const checkboxMalEstado = document.getElementById('incluir_mal_estado');
                                const btnGenerar = document.getElementById('btnGenerar');
                                const formReporte = document.getElementById('formReporte');
                                const hiddenStockBajo = document.getElementById('hidden_stock_bajo');
                                const hiddenMalEstado = document.getElementById('hidden_mal_estado');
                                
                                function validarCheckboxes() {
                                    const algunoSeleccionado = checkboxStockBajo.checked || checkboxMalEstado.checked;
                                    btnGenerar.disabled = !algunoSeleccionado;
                                    
                                    hiddenStockBajo.value = checkboxStockBajo.checked ? '1' : '0';
                                    hiddenMalEstado.value = checkboxMalEstado.checked ? '1' : '0';
                                }
                                
                                // Antes de enviar el formulario, actualizar campos ocultos
                                formReporte.addEventListener('submit', function(e) {
                                    validarCheckboxes();
                                });
                                
                                checkboxStockBajo.addEventListener('change', validarCheckboxes);
                                checkboxMalEstado.addEventListener('change', validarCheckboxes);
                                validarCheckboxes();
                            </script>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="info-section mt-5">
                <h4 class="section-title mb-4">
                    <i class="fas fa-info-circle"></i> Información sobre los reportes
                </h4>
                
                <div class="row">
                    <div class="col-md-4 mb-3">
                        <div class="info-card">
                            <div class="info-icon">
                                <i class="fas fa-file-pdf"></i>
                            </div>
                            <h6 class="info-title">Formato PDF</h6>
                            <p class="info-text">Los reportes se generan automáticamente en formato PDF, listos para imprimir o compartir</p>
                        </div>
                    </div>
                    
                    <div class="col-md-4 mb-3">
                        <div class="info-card">
                            <div class="info-icon">
                                <i class="fas fa-clock"></i>
                            </div>
                            <h6 class="info-title">Fecha y Hora</h6>
                            <p class="info-text">Cada reporte incluye fecha y hora de generación para mantener un registro completo</p>
                        </div>
                    </div>
                    
                    <div class="col-md-4 mb-3">
                        <div class="info-card">
                            <div class="info-icon">
                                <i class="fas fa-sync-alt"></i>
                            </div>
                            <h6 class="info-title">Datos Actualizados</h6>
                            <p class="info-text">Los reportes descargados contienen datos actualizados al momento de la descarga</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html> 