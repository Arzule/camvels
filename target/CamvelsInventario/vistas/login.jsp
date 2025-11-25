<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Camvels</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css?v=125">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <div class="login-logo">
                    <i class="fas fa-cube"></i>
                </div>
                <h1 class="login-title">Camvels</h1>
                <p class="login-subtitle">Sistema de Inventario</p>
            </div>
            
            <div class="login-body">
                <form action="<%= request.getContextPath() %>/LoginServlet" method="post" class="login-form">
                    <div class="form-group">
                        <label for="usuario" class="form-label">Usuario</label>
                        <div class="input-group">
                            <i class="fas fa-user input-icon"></i>
                            <input type="text"
                                   id="usuario"
                                   name="usuario"
                                   class="form-control"
                                   placeholder="Ingresa tu usuario"
                                   required
                                   autocomplete="username">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="password" class="form-label">Contraseña</label>
                        <div class="input-group">
                            <i class="fas fa-lock input-icon"></i>
                            <input type="password"
                                   id="password"
                                   name="password"
                                   class="form-control"
                                   placeholder="Ingresa tu contraseña"
                                   required
                                   autocomplete="current-password">
                        </div>
                    </div>
                    
                    <button type="submit" class="login-btn">
                        <i class="fas fa-sign-in-alt"></i> Iniciar Sesión
                    </button>
                </form>
                
                <%
                    String error = (String) request.getAttribute("error");
                    if (error != null) {
                %>
                <div class="login-error">
                    <i class="fas fa-exclamation-triangle"></i> <%= error %>
                </div>
                <% } %>
            </div>
            
            <div class="login-footer">
                <p>&copy; 2025 Camvels. Todos los derechos reservados.</p>
            </div>
        </div>
    </div>
    
    <script>
        // efectos interactivos
        document.addEventListener('DOMContentLoaded', function() {
            const inputs = document.querySelectorAll('.form-control');
            const loginBtn = document.querySelector('.login-btn');
            const form = document.querySelector('.login-form');
            
            // efectos de enfoque a los inputs
            inputs.forEach(input => {
                input.addEventListener('focus', function() {
                    this.parentElement.classList.add('focused');
                });
                
                input.addEventListener('blur', function() {
                    if (!this.value) {
                        this.parentElement.classList.remove('focused');
                    }
                });
            });
            
            // estado de carga del boton con timeout
            form.addEventListener('submit', function(e) {
                if (form.checkValidity()) {
                    loginBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Iniciando...';
                    loginBtn.disabled = true;
                    
                    // reiniciar boton despues de 10 segundos en caso de problemas
                    setTimeout(function() {
                        if (loginBtn.disabled) {
                            loginBtn.innerHTML = '<i class="fas fa-sign-in-alt"></i> Iniciar Sesión';
                            loginBtn.disabled = false;
                        }
                    }, 10000);
                }
            });
        });
    </script>
</body>
</html>