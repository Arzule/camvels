package servlet;

import modelo.Categoria;
import dao.CategoriaDAO;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class CategoriaServlet extends HttpServlet {
    private CategoriaDAO dao = new CategoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";
        switch (accion) {
            case "nuevo" -> request.getRequestDispatcher("/vistas/categoria_form.jsp").forward(request, response);
            case "editar" -> {
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Categoria catEditar = dao.buscarPorId(idEditar);
                request.setAttribute("categoria", catEditar);
                request.getRequestDispatcher("/vistas/categoria_form.jsp").forward(request, response);
            }
            case "eliminar" -> {
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                dao.eliminar(idEliminar);
                response.sendRedirect("CategoriaServlet");
            }
            default -> {
                List<Categoria> lista = dao.listar();
                request.setAttribute("categorias", lista);
                request.getRequestDispatcher("/vistas/categorias.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = request.getParameter("id") != null && !request.getParameter("id").isEmpty() ? Integer.parseInt(request.getParameter("id")) : 0;
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre(nombre);
        c.setDescripcion(descripcion);

        if (id == 0) {
            dao.agregar(c);
        } else {
            dao.actualizar(c);
        }
        response.sendRedirect("CategoriaServlet");
    }
} 