<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Proveedor" %>
<%@ page import="modelo.Usuario" %>
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
    <title>Proveedores - Camvels</title>
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
            <h1 class="dashboard-title">Proveedores</h1>
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
        
        <% 
            String mensaje = (String) request.getSession().getAttribute("mensaje");
            String tipoMensaje = (String) request.getSession().getAttribute("tipo_mensaje");
            if (mensaje != null) {
                // No mostrar mensajes de éxito relacionados con correos
                boolean esMensajeCorreo = mensaje.contains("Correo enviado exitosamente") || 
                                         mensaje.contains("servidor SMTP") ||
                                         (tipoMensaje != null && "success".equals(tipoMensaje) && mensaje.contains("correo"));
                
                if (!esMensajeCorreo) {
                    request.getSession().removeAttribute("mensaje");
                    request.getSession().removeAttribute("tipo_mensaje");
        %>
        <div class="alert alert-<%= tipoMensaje != null ? tipoMensaje : "info" %>" style="margin: 1rem 0; padding: 1rem; border-radius: 8px; background: <%= "success".equals(tipoMensaje) ? "#d4edda" : "danger".equals(tipoMensaje) ? "#f8d7da" : "#d1ecf1" %>; color: <%= "success".equals(tipoMensaje) ? "#155724" : "danger".equals(tipoMensaje) ? "#721c24" : "#0c5460" %>; border: 1px solid <%= "success".equals(tipoMensaje) ? "#c3e6cb" : "danger".equals(tipoMensaje) ? "#f5c6cb" : "#bee5eb" %>;">
            <%= mensaje %>
        </div>
        <%     } else {
                    // Limpiar el mensaje de correo sin mostrarlo
                    request.getSession().removeAttribute("mensaje");
                    request.getSession().removeAttribute("tipo_mensaje");
                }
            } %>
        
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <div>
                    <i class="fas fa-truck"></i> Lista de Proveedores
                </div>
                <a href="<%= request.getContextPath() %>/ProveedorServlet?accion=nuevo" class="btn btn-success btn-sm">
                    <i class="fas fa-plus"></i> Agregar Proveedor
                </a>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>RUC</th>
                                <th>Nombre</th>
                                <th>Dirección</th>
                                <th>Teléfono</th>
                                <th>Email</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (proveedores.isEmpty()) { %>
                            <tr>
                                <td colspan="7" class="text-center py-4">
                                    <i class="fas fa-info-circle text-muted" style="font-size: 2rem; margin-bottom: 1rem;"></i>
                                    <p class="text-muted mb-0">No hay proveedores registrados</p>
                                </td>
                            </tr>
                            <% } else { %>
                                <% for (Proveedor p : proveedores) { %>
                                <tr>
                                    <td><%= p.getId() %></td>
                                    <td><strong><%= p.getRuc() %></strong></td>
                                    <td><%= p.getNombre() %></td>
                                    <td><%= p.getDireccion() %></td>
                                    <td><%= p.getTelefono() %></td>
                                    <td><%= p.getEmail() %></td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/ProveedorServlet?accion=editar&id=<%= p.getId() %>" class="btn btn-primary btn-sm">
                                            <i class="fas fa-edit"></i> Editar
                                        </a>
                                        <button class="btn btn-info btn-sm" 
                                                data-proveedor-id="<%= p.getId() %>"
                                                data-proveedor-nombre="<%= p.getNombre() %>"
                                                data-proveedor-email="<%= p.getEmail() != null ? p.getEmail() : "" %>"
                                                onclick="abrirModalContacto(this)">
                                            <i class="fas fa-envelope"></i> Contactar
                                        </button>
                                        <% if ("admin".equals(usuario.getRol())) { %>
                                        <a href="<%= request.getContextPath() %>/ProveedorServlet?accion=eliminar&id=<%= p.getId() %>" class="btn btn-danger btn-sm" onclick="return confirm('¿Seguro de eliminar este proveedor?');">
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
    
    <!-- Modal de Contacto -->
    <div class="modal-overlay" id="modalContacto">
        <div class="modal-container">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="fas fa-envelope"></i> Enviar Correo a Proveedor
                </h5>
                <button type="button" class="modal-close" onclick="cerrarModalContacto()" aria-label="Cerrar">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <form id="formContacto" method="post" action="<%= request.getContextPath() %>/EmailServlet" enctype="multipart/form-data">
                <input type="hidden" name="proveedor_id" id="proveedor_id">
                <div class="modal-body">
                    <div class="form-group">
                        <label for="proveedor_nombre" class="form-label">Proveedor:</label>
                        <input type="text" class="form-control" id="proveedor_nombre" readonly>
                    </div>
                    <div class="form-group">
                        <label for="proveedor_email" class="form-label">Correo Destinatario:</label>
                        <input type="email" class="form-control" id="proveedor_email" name="proveedor_email" required>
                        <small class="form-text text-muted">Correo electrónico del proveedor</small>
                    </div>
                    <div class="form-group">
                        <label for="asunto" class="form-label">Asunto:</label>
                        <input type="text" class="form-control" id="asunto" name="asunto" required placeholder="Asunto del correo">
                    </div>
                    <div class="form-group">
                        <label for="mensaje" class="form-label">Mensaje:</label>
                        <textarea class="form-control" id="mensaje" name="mensaje" rows="6" required placeholder="Escriba su mensaje aquí..."></textarea>
                    </div>
                    <div class="form-group">
                        <label for="archivo_adjunto" class="form-label">
                            <i class="fas fa-paperclip"></i> Adjuntar Archivo (Opcional)
                        </label>
                        <input type="file" class="form-control" id="archivo_adjunto" name="archivo_adjunto" accept=".pdf,.doc,.docx,.xls,.xlsx,.txt,.jpg,.jpeg,.png">
                        <small class="form-text text-muted">
                            Puedes adjuntar cualquier archivo. Tamaño máximo recomendado: 10MB
                        </small>
                        <div id="nombre_archivo" class="file-selected" style="display: none;">
                            <small class="text-success">
                                <i class="fas fa-check-circle"></i> Archivo seleccionado: <span id="nombre_archivo_texto"></span>
                            </small>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="cerrarModalContacto()">
                        <i class="fas fa-times"></i> Cancelar
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-paper-plane"></i> Enviar Correo
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        function abrirModalContacto(button) {
            const id = button.getAttribute('data-proveedor-id');
            const nombre = button.getAttribute('data-proveedor-nombre');
            const email = button.getAttribute('data-proveedor-email');
            
            document.getElementById('proveedor_id').value = id;
            document.getElementById('proveedor_nombre').value = nombre;
            document.getElementById('proveedor_email').value = email || '';
            document.getElementById('asunto').value = 'Contacto desde Minimarket Camvels - ' + nombre;
            document.getElementById('mensaje').value = 'Estimado/a ' + nombre + ',\n\n';
            document.getElementById('archivo_adjunto').value = '';
            document.getElementById('nombre_archivo').style.display = 'none';
            
            const modal = document.getElementById('modalContacto');
            modal.classList.add('modal-show');
            document.body.style.overflow = 'hidden';
        }
        
        function cerrarModalContacto() {
            const modal = document.getElementById('modalContacto');
            modal.classList.remove('modal-show');
            document.body.style.overflow = '';
        }
        
        // Cerrar modal al hacer clic fuera
        document.getElementById('modalContacto').addEventListener('click', function(e) {
            if (e.target === this) {
                cerrarModalContacto();
            }
        });
        
        // Cerrar modal con ESC
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                const modal = document.getElementById('modalContacto');
                if (modal.classList.contains('modal-show')) {
                    cerrarModalContacto();
                }
            }
        });
        
        // Mostrar nombre del archivo cuando se selecciona
        document.getElementById('archivo_adjunto').addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                document.getElementById('nombre_archivo_texto').textContent = file.name + ' (' + (file.size / 1024).toFixed(2) + ' KB)';
                document.getElementById('nombre_archivo').style.display = 'block';
            } else {
                document.getElementById('nombre_archivo').style.display = 'none';
            }
        });
        
        // Manejar envío del formulario
        document.getElementById('formContacto').addEventListener('submit', function(e) {
            const email = document.getElementById('proveedor_email').value;
            if (!email || email.trim() === '') {
                e.preventDefault();
                alert('Por favor, ingrese un correo electrónico válido para el proveedor.');
                return false;
            }
            
            const archivo = document.getElementById('archivo_adjunto').files[0];
            if (archivo && archivo.size > 10 * 1024 * 1024) {
                e.preventDefault();
                alert('El archivo es demasiado grande. El tamaño máximo permitido es 10MB.');
                return false;
            }
        });
    </script>
</body>
</html>