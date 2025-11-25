package util;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import modelo.Usuario;

public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        if (requestURI.contains("/login.jsp") ||
            requestURI.contains("/LoginServlet") ||
            requestURI.contains("/css/") ||
            requestURI.contains("/js/") ||
            requestURI.contains("/images/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar si el usuario está autenticado
        if (session == null || session.getAttribute("usuario") == null) {
            httpResponse.sendRedirect(contextPath + "/vistas/login.jsp");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        // Verificar permisos según el rol
        if (!tienePermiso(usuario, requestURI, contextPath)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean tienePermiso(Usuario usuario, String requestURI, String contextPath) {
        String rol = usuario.getRol();
        
        // Usuarios: solo admin
        if (requestURI.contains("/UsuarioServlet") && !"admin".equals(rol)) {
            return false;
        }
        
        // Eliminar: solo admin en cualquier módulo
        if (requestURI.contains("accion=eliminar") && !"admin".equals(rol)) {
            return false;
        }
        
        // Categorías: admin y supervisor
        if (requestURI.contains("/CategoriaServlet") && !("admin".equals(rol) || "supervisor".equals(rol))) {
            return false;
        }

        // Reportes: admin, supervisor y almacen
        if (requestURI.contains("/ReporteServlet") && !("admin".equals(rol) || "supervisor".equals(rol) || "almacen".equals(rol))) {
            return false;
        }

        
        // Permisos específicos por rol
        switch (rol) {
            case "admin" -> {
                // Admin tiene acceso completo
                return true;
            }
                
            case "almacen" -> {
                if (requestURI.contains("/ProductoServlet") ||
                        requestURI.contains("/ProveedorServlet") ||
                        requestURI.contains("/ReporteServlet") ||
                        requestURI.contains("/MovimientoServlet") ||
                        requestURI.contains("/DashboardServlet") ||
                        requestURI.contains("/EmailServlet") ||
                        requestURI.contains("/LogoutServlet")) {
                    return true;
                }
                return false;
            }
                
            case "supervisor" -> {
                if (requestURI.contains("/DashboardServlet") ||
                        requestURI.contains("/ProductoServlet") ||
                        requestURI.contains("/ProveedorServlet") ||
                        requestURI.contains("/ReporteServlet") ||
                        requestURI.contains("/CategoriaServlet") ||
                        requestURI.contains("/MovimientoServlet") ||
                        requestURI.contains("/EmailServlet") ||
                        requestURI.contains("/vistas/movimientos.jsp") ||
                        requestURI.contains("/LogoutServlet")) {
                    return true;
                }
                return false;
            }
                
            default -> {
                if (requestURI.contains("/DashboardServlet") ||
                        requestURI.contains("/LogoutServlet")) {
                    return true;
                }
                return false;
            }
        }
    }
    
    @Override
    public void destroy() {
    }
}