package servlet;

import modelo.Usuario;
import dao.UsuarioDAO;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class UsuarioServlet extends HttpServlet {
    private final UsuarioDAO dao = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        modelo.Usuario actual = (modelo.Usuario) (sesion != null ? sesion.getAttribute("usuario") : null);
        if (actual == null || !"admin".equals(actual.getRol())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo el administrador puede acceder a usuarios");
            return;
        }
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";
        switch (accion) {
            case "nuevo" -> request.getRequestDispatcher("/vistas/usuario_form.jsp").forward(request, response);
            case "editar" -> {
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Usuario userEditar = dao.buscarPorId(idEditar);
                request.setAttribute("usuario", userEditar);
                request.getRequestDispatcher("/vistas/usuario_form.jsp").forward(request, response);
            }
            case "eliminar" -> {
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("UsuarioServlet");
            }
            default -> {
                List<Usuario> lista = dao.listar();
                request.setAttribute("usuarios", lista);
                request.getRequestDispatcher("/vistas/usuarios.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        modelo.Usuario actual = (modelo.Usuario) (sesion != null ? sesion.getAttribute("usuario") : null);
        if (actual == null || !"admin".equals(actual.getRol())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo el administrador puede modificar usuarios");
            return;
        }
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty() ? Integer.parseInt(request.getParameter("id")) : 0;
        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");
        String nombre = request.getParameter("nombre");
        String rol = request.getParameter("rol");

        Usuario u = new Usuario();
        u.setId(id);
        u.setUsuario(usuario);
        u.setPassword(password);
        u.setNombre(nombre);
        u.setRol(rol);

        if (id == 0) {
            dao.agregar(u);
        } else {
            dao.actualizar(u);
        }
        response.sendRedirect("UsuarioServlet");
    }
} 