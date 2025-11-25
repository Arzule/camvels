package servlet;

import modelo.Proveedor;
import dao.ProveedorDAO;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class ProveedorServlet extends HttpServlet {
    private ProveedorDAO dao = new ProveedorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";
        switch (accion) {
            case "nuevo" -> request.getRequestDispatcher("/vistas/proveedor_form.jsp").forward(request, response);
            case "editar" -> {
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Proveedor provEditar = dao.buscarPorId(idEditar);
                request.setAttribute("proveedor", provEditar);
                request.getRequestDispatcher("/vistas/proveedor_form.jsp").forward(request, response);
            }
            case "eliminar" -> {
                HttpSession sesion = request.getSession(false);
                modelo.Usuario u = (modelo.Usuario) (sesion != null ? sesion.getAttribute("usuario") : null);
                if (u == null || "supervisor".equals(u.getRol())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "No autorizado para eliminar proveedores");
                    return;
                }
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("ProveedorServlet");
            }
            default -> {
                List<Proveedor> lista = dao.listar();
                request.setAttribute("proveedores", lista);
                request.getRequestDispatcher("/vistas/proveedores.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty() ? Integer.parseInt(request.getParameter("id")) : 0;
        String ruc = request.getParameter("ruc");
        String nombre = request.getParameter("nombre");
        String direccion = request.getParameter("direccion");
        String telefono = request.getParameter("telefono");
        String email = request.getParameter("email");

        Proveedor p = new Proveedor();
        p.setId(id);
        p.setRuc(ruc);
        p.setNombre(nombre);
        p.setDireccion(direccion);
        p.setTelefono(telefono);
        p.setEmail(email);

        if (id == 0) {
            dao.agregar(p);
        } else {
            dao.actualizar(p);
        }
        response.sendRedirect("ProveedorServlet");
    }
} 