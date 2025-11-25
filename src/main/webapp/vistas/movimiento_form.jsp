<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="modelo.Movimiento" %>
<%@ page import="modelo.Producto" %>
<%@ page import="java.util.List" %>
<%
    HttpSession sesion = request.getSession(false);
    Usuario usuario = (Usuario) sesion.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/vistas/login.jsp");
        return;
    }
    
    Movimiento movimiento = (Movimiento) request.getAttribute("movimiento");
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    if (productos == null) productos = new java.util.ArrayList<>();
    String accion = (String) request.getAttribute("accion");
    if (accion == null) accion = "nuevo";
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= accion.equals("editar") ? "Editar" : "Nuevo" %> Movimiento - Camvels</title>
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
            <h1 class="dashboard-title"><%= accion.equals("editar") ? "Editar" : "Nuevo" %> Movimiento</h1>
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
                <i class="fas fa-exchange-alt"></i> <%= accion.equals("editar") ? "Editar" : "Crear" %> Movimiento de Inventario
            </div>
            <div class="card-body">
                <form method="post" action="<%= request.getContextPath() %>/MovimientoServlet" class="movimiento-form">
                    <input type="hidden" name="accion" value="<%= accion %>">
                    <% if (movimiento != null) { %>
                    <input type="hidden" name="id" value="<%= movimiento.getId() %>">
                    <% } %>
                    
                    <!-- Sección: Tipo y Producto -->
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-info-circle"></i>
                            <span>Información del Movimiento</span>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-exchange-alt"></i>
                                    Tipo de Movimiento *
                                </label>
                                <div class="input-wrapper">
                                    <select name="tipo" id="tipo-select" class="form-select" required>
                                        <option value="">Seleccione un tipo</option>
                                        <option value="ENTRADA" <%= (movimiento != null && "ENTRADA".equals(movimiento.getTipo())) ? "selected" : "" %>>Entrada</option>
                                        <option value="SALIDA" <%= (movimiento != null && "SALIDA".equals(movimiento.getTipo())) ? "selected" : "" %>>Salida</option>
                                        <option value="AJUSTE" <%= (movimiento != null && "AJUSTE".equals(movimiento.getTipo())) ? "selected" : "" %>>Ajuste</option>
                                    </select>
                                </div>
                            </div>
                            
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-box"></i>
                                    Producto *
                                </label>
                                <div class="producto-search-container">
                                    <div class="search-input-wrapper">
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-search"></i></span>
                                            <input type="text" id="producto-search" class="form-control" 
                                                   placeholder="Buscar por código o nombre..." 
                                                   autocomplete="off">
                                        </div>
                                        <div class="product-count-badge" id="producto-count"></div>
                                    </div>
                                    <div class="select-wrapper">
                                        <select name="producto_id" id="producto-select" class="form-select" required>
                                            <option value="">Seleccione un producto</option>
                                            <% for (Producto p : productos) { %>
                                            <option value="<%= p.getId() %>" 
                                                    data-codigo="<%= p.getCodigo() %>" 
                                                    data-nombre="<%= p.getNombre() %>"
                                                    data-stock="<%= p.getStock() %>"
                                                    <%= (movimiento != null && movimiento.getProductoId() == p.getId()) ? "selected" : "" %>>
                                                <%= p.getCodigo() %> - <%= p.getNombre() %> (Stock: <%= p.getStock() %>)
                                            </option>
                                            <% } %>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-section">
                        <div class="section-title">
                            <i class="fas fa-clipboard-list"></i>
                            <span>Detalles</span>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-4">
                                <label class="form-label with-icon">
                                    <i class="fas fa-hashtag"></i>
                                    Cantidad *
                                </label>
                                <div class="input-wrapper">
                                    <input type="number" 
                                           name="cantidad" 
                                           class="form-control" 
                                           <% if (movimiento != null) { %>value="<%= movimiento.getCantidad() %>"<% } %>
                                           min="1" 
                                           placeholder="Ingrese la cantidad"
                                           required>
                                    <small class="form-help">Cantidad de unidades del producto</small>
                                </div>
                            </div>
                        </div>
                        
                        <div class="mb-4">
                            <label class="form-label with-icon">
                                <i class="fas fa-comment-alt"></i>
                                Observaciones
                            </label>
                            <div class="input-wrapper">
                                <textarea name="observaciones" class="form-control" rows="4" 
                                          placeholder="Descripción adicional del movimiento (opcional)"><%= movimiento != null ? (movimiento.getObservaciones() != null ? movimiento.getObservaciones() : "") : "" %></textarea>
                                <small class="form-help">Información adicional sobre este movimiento</small>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Botones de acción -->
                    <div class="form-actions">
                        <a href="<%= request.getContextPath() %>/MovimientoServlet" class="btn btn-secondary btn-lg">
                            <i class="fas fa-times"></i> Cancelar
                        </a>
                        <button type="submit" class="btn btn-primary btn-lg">
                            <i class="fas fa-save"></i> Guardar Movimiento
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <script>
        // Funcionalidad de búsqueda de productos
        document.addEventListener('DOMContentLoaded', function() {
            const searchInput = document.getElementById('producto-search');
            const selectElement = document.getElementById('producto-select');
            const countElement = document.getElementById('producto-count');
            
            if (searchInput && selectElement) {
                // Guardar todas las opciones originales
                const allOptions = Array.from(selectElement.options).slice(1);
                
                function updateProductCount() {
                    const visibleOptions = Array.from(selectElement.options).filter(opt => 
                        opt.value !== '' && opt.style.display !== 'none'
                    );
                    const total = allOptions.length;
                    const visible = visibleOptions.length;
                    const badge = document.querySelector('.product-count-badge');
                    
                    if (searchInput.value.trim() === '') {
                        if (badge) {
                            badge.textContent = total + ' disponible(s)';
                            badge.classList.add('visible');
                        }
                    } else {
                        if (badge) {
                            badge.textContent = visible + ' de ' + total + ' encontrado(s)';
                            badge.classList.add('visible');
                        }
                    }
                }
                
                function filterProducts() {
                    const searchTerm = searchInput.value.toLowerCase().trim();
                    let visibleCount = 0;
                    
                    allOptions.forEach(option => {
                        const codigo = (option.getAttribute('data-codigo') || '').toLowerCase();
                        const nombre = (option.getAttribute('data-nombre') || '').toLowerCase();
                        const textoCompleto = codigo + ' ' + nombre;
                        
                        if (searchTerm === '' || textoCompleto.includes(searchTerm)) {
                            option.style.display = '';
                            visibleCount++;
                        } else {
                            option.style.display = 'none';
                        }
                    });
                    
                    if (searchTerm !== '' && visibleCount > 0) {
                        const firstVisible = Array.from(selectElement.options).find(opt => 
                            opt.value !== '' && opt.style.display !== 'none'
                        );
                        if (firstVisible && selectElement.value === '') {
                        }
                    }
                    
                    if (selectElement.value !== '') {
                        const selectedOption = selectElement.options[selectElement.selectedIndex];
                        if (selectedOption && selectedOption.style.display === 'none') {
                            selectElement.value = '';
                        }
                    }
                    
                    updateProductCount();
                }
                
                searchInput.addEventListener('input', filterProducts);
                searchInput.addEventListener('keydown', function(e) {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        const firstVisible = Array.from(selectElement.options).find(opt => 
                            opt.value !== '' && opt.style.display !== 'none'
                        );
                        if (firstVisible) {
                            selectElement.value = firstVisible.value;
                            selectElement.focus();
                        }
                    }
                });
                
                // Función para actualizar el título del select con el texto completo
                function updateSelectTitle() {
                    if (selectElement.value !== '') {
                        const selectedOption = selectElement.options[selectElement.selectedIndex];
                        if (selectedOption) {
                            selectElement.title = selectedOption.textContent;
                        }
                    } else {
                        selectElement.title = '';
                    }
                }
                
                selectElement.addEventListener('change', function() {
                    updateSelectTitle();
                });
                
                updateSelectTitle();
                
                // Inicializar contador
                updateProductCount();
            }
        });
    </script>
</body>
</html>
