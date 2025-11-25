 package servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.MultipartConfig;
import java.io.*;

import modelo.Proveedor;
import dao.ProveedorDAO;
import util.EmailUtil;

@MultipartConfig(
    maxFileSize = 10485760,
    maxRequestSize = 10485760
)
public class EmailServlet extends HttpServlet {
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        modelo.Usuario usuario = (modelo.Usuario) (sesion != null ? sesion.getAttribute("usuario") : null);
        if (usuario == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No autorizado");
            return;
        }

        try {
            String proveedorIdStr = request.getParameter("proveedor_id");
            String proveedorEmail = request.getParameter("proveedor_email");
            String asunto = request.getParameter("asunto");
            String mensaje = request.getParameter("mensaje");
            
            // Procesar archivo adjunto si existe
            byte[] archivoAdjunto = null;
            String nombreArchivoAdjunto = null;
            Part archivoPart = request.getPart("archivo_adjunto");
            
            if (archivoPart != null && archivoPart.getSize() > 0) {
                String nombreArchivo = archivoPart.getSubmittedFileName();
                if (nombreArchivo != null && !nombreArchivo.trim().isEmpty()) {
                    InputStream inputStream = archivoPart.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    archivoAdjunto = baos.toByteArray();
                    nombreArchivoAdjunto = nombreArchivo;
                    System.out.println("Archivo adjunto recibido: " + nombreArchivoAdjunto + " (" + archivoAdjunto.length + " bytes)");
                }
            }

            if (proveedorIdStr == null || proveedorIdStr.isEmpty() || 
                proveedorEmail == null || proveedorEmail.trim().isEmpty() ||
                asunto == null || asunto.trim().isEmpty() ||
                mensaje == null || mensaje.trim().isEmpty()) {
                request.setAttribute("mensaje", "Todos los campos son obligatorios.");
                request.setAttribute("tipo_mensaje", "danger");
                response.sendRedirect(request.getContextPath() + "/ProveedorServlet");
                return;
            }

            int proveedorId = Integer.parseInt(proveedorIdStr);
            Proveedor proveedor = proveedorDAO.buscarPorId(proveedorId);
            
            if (proveedor == null) {
                request.getSession().setAttribute("mensaje", "Proveedor no encontrado.");
                request.getSession().setAttribute("tipo_mensaje", "danger");
                response.sendRedirect(request.getContextPath() + "/ProveedorServlet");
                return;
            }

            // Usar el correo del proveedor de la BD si el campo está vacío o usar el proporcionado
            String emailDestinatario = proveedorEmail.trim();
            if (emailDestinatario.isEmpty() && proveedor.getEmail() != null && !proveedor.getEmail().trim().isEmpty()) {
                emailDestinatario = proveedor.getEmail().trim();
            }
            
            if (emailDestinatario.isEmpty()) {
                request.getSession().setAttribute("mensaje", "El proveedor no tiene un correo electrónico configurado. Por favor, ingrese un correo válido.");
                request.getSession().setAttribute("tipo_mensaje", "danger");
                response.sendRedirect(request.getContextPath() + "/ProveedorServlet");
                return;
            }

            // Enviar correo con el archivo adjunto si está disponible
            System.out.println("Intentando enviar correo a: " + emailDestinatario);
            if (archivoAdjunto != null) {
                System.out.println("Adjuntando archivo: " + nombreArchivoAdjunto + " (" + archivoAdjunto.length + " bytes)");
            }
            
            boolean enviado = EmailUtil.enviarCorreo(
                emailDestinatario,
                asunto,
                mensaje,
                archivoAdjunto,
                nombreArchivoAdjunto
            );

            if (enviado) {
                // Limpiar cualquier mensaje previo de la sesión
                request.getSession().removeAttribute("mensaje");
                request.getSession().removeAttribute("tipo_mensaje");
            } else {
                // Obtener el error específico de EmailUtil
                String errorDetallado = util.EmailUtil.getUltimoError();
                String mensajeError = "✗ Error al enviar el correo a " + emailDestinatario + ". ";
                
                if (errorDetallado != null && !errorDetallado.isEmpty()) {
                    mensajeError += errorDetallado;
                } else {
                    mensajeError += "Posibles causas: 1) Credenciales incorrectas, 2) Cuenta bloqueada, 3) Problema de conexión, 4) Formato de correo inválido. " +
                                  "SOLUCIÓN: Cambia a Gmail con contraseña de aplicación (más confiable).";
                }
                
                request.getSession().setAttribute("mensaje", mensajeError);
                request.getSession().setAttribute("tipo_mensaje", "danger");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("mensaje", "Error al procesar la solicitud: " + e.getMessage());
            request.getSession().setAttribute("tipo_mensaje", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/ProveedorServlet");
    }
}

