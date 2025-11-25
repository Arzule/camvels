package servlet;

import modelo.Usuario;
import dao.UsuarioDAO;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("=== LOGIN ATTEMPT ===");
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");
        System.out.println("Usuario: " + usuario);
        System.out.println("Password: " + (password != null ? "***" : "null"));
        
        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario u = dao.validar(usuario, password);
            System.out.println("Usuario encontrado: " + (u != null ? u.getNombre() : "null"));

            if (u != null) {
                HttpSession sesion = request.getSession();
                sesion.setAttribute("usuario", u);
                System.out.println("Redirigiendo a DashboardServlet...");
                // Redirigir al DashboardServlet en lugar de directamente al JSP
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
            } else {
                System.out.println("Credenciales incorrectas");
                request.setAttribute("error", "Usuario o contraseña incorrectos");
                request.getRequestDispatcher("/vistas/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("Error en LoginServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error interno del servidor");
            request.getRequestDispatcher("/vistas/login.jsp").forward(request, response);
        }
    }
} 