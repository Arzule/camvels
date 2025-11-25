package servlet;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import modelo.Producto;
import modelo.Movimiento;
import modelo.Proveedor;
import dao.ProductoDAO;
import dao.MovimientoDAO;
import dao.ProveedorDAO;

public class ReporteServlet extends HttpServlet {
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        modelo.Usuario actual = (modelo.Usuario) (sesion != null ? sesion.getAttribute("usuario") : null);
        if (actual == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No autorizado");
            return;
        }
        
        String tipo = request.getParameter("tipo");
        if (tipo == null) {
            List<Proveedor> proveedores = proveedorDAO.listar();
            request.setAttribute("proveedores", proveedores);
            request.getRequestDispatcher("/vistas/reportes.jsp").forward(request, response);
            return;
        }
        
        response.setContentType("application/pdf");
        String nombreArchivo = "reporte_" + tipo;
        if ("proveedores_productos".equals(tipo)) {
            String proveedorIdStr = request.getParameter("proveedor_id");
            String stockBajoParam = request.getParameter("incluir_stock_bajo");
            String malEstadoParam = request.getParameter("incluir_mal_estado");
            boolean incluirStockBajo = stockBajoParam != null && "1".equals(stockBajoParam);
            boolean incluirMalEstado = malEstadoParam != null && "1".equals(malEstadoParam);
            
            if (proveedorIdStr != null && !proveedorIdStr.isEmpty() && !"0".equals(proveedorIdStr)) {
                nombreArchivo += "_proveedor_" + proveedorIdStr;
            } else {
                nombreArchivo += "_todos";
            }
            
            // Agregar sufijo según tipo de productos
            if (incluirStockBajo && incluirMalEstado) {
                nombreArchivo += "_stock_mal";
            } else if (incluirStockBajo) {
                nombreArchivo += "_stock";
            } else {
                nombreArchivo += "_mal";
            }
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo + ".pdf");
        
        try {
            switch (tipo) {
                case "productos" -> generarReporteProductos(response);
                case "stock_bajo" -> generarReporteStockBajo(response);
                case "movimientos" -> generarReporteMovimientos(response);
                case "proveedores" -> generarReporteProveedores(response);
                case "proveedores_productos" -> {
                    String proveedorIdStr = request.getParameter("proveedor_id");
                    Integer proveedorId = null;
                    if (proveedorIdStr != null && !proveedorIdStr.isEmpty() && !"0".equals(proveedorIdStr)) {
                        try {
                            proveedorId = Integer.valueOf(proveedorIdStr);
                        } catch (NumberFormatException e) {
                            proveedorId = null;
                        }
                    }
                    
                    // Leer parámetros de checkboxes
                    String stockBajoParam = request.getParameter("incluir_stock_bajo");
                    String malEstadoParam = request.getParameter("incluir_mal_estado");
                    
                    // Los campos ocultos siempre se envían, así que podemos verificar directamente
                    boolean incluirStockBajo = stockBajoParam != null && ("1".equals(stockBajoParam));
                    boolean incluirMalEstado = malEstadoParam != null && ("1".equals(malEstadoParam));
                    
                    // Valida que al menos uno esté seleccionado
                    if (!incluirStockBajo && !incluirMalEstado) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debe seleccionar al menos un tipo de producto.");
                        return;
                    }
                    
                    generarReporteProveedoresProductos(response, proveedorId, incluirStockBajo, incluirMalEstado);
                }
                default -> {
                    request.setAttribute("mensaje", "Tipo de reporte no válido.");
                    request.setAttribute("tipo_mensaje", "danger");
                    List<Proveedor> proveedores = proveedorDAO.listar();
                    request.setAttribute("proveedores", proveedores);
                    request.getRequestDispatcher("/vistas/reportes.jsp").forward(request, response);
                }
            }
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            request.setAttribute("mensaje", "Error al generar el reporte: " + e.getMessage());
            request.setAttribute("tipo_mensaje", "danger");
            List<Proveedor> proveedores = proveedorDAO.listar();
            request.setAttribute("proveedores", proveedores);
            request.getRequestDispatcher("/vistas/reportes.jsp").forward(request, response);
        }
    }
    
    private void generarReporteProductos(HttpServletResponse response) throws IOException {
        List<Producto> productos = productoDAO.listar();
        
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Título
        Paragraph titulo = new Paragraph("REPORTE DE PRODUCTOS - CAMVELS")
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold();
        document.add(titulo);
        
        // Fecha
        Paragraph fecha = new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(fecha);
        
        document.add(new Paragraph(" ")); // Espacio
        
        // Tabla
        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 30, 20, 15, 15, 10}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("ID")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Nombre")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Categoría")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Stock")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Precio")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Mínimo")).setBold());
        
        // Datos
        for (Producto p : productos) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getId()))));
            table.addCell(new Cell().add(new Paragraph(p.getNombre())));
            table.addCell(new Cell().add(new Paragraph(p.getCategoria())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getStock()))));
            table.addCell(new Cell().add(new Paragraph("S/. " + String.format("%.2f", p.getPrecio()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getMinimo()))));
        }
        
        document.add(table);
        document.close();
    }
    
    
    private void generarReporteStockBajo(HttpServletResponse response) throws IOException {
        List<Producto> stockBajo = productoDAO.listarStockBajo();
        
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Título
        Paragraph titulo = new Paragraph("REPORTE DE STOCK BAJO - CAMVELS")
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold();
        document.add(titulo);
        
        // Fecha
        Paragraph fecha = new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(fecha);
        
        document.add(new Paragraph(" "));
        
        // Tabla
        Table table = new Table(UnitValue.createPercentArray(new float[]{15, 35, 20, 15, 15}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Código")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Producto")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Categoría")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Stock")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Mínimo")).setBold());
        
        // Datos
        for (Producto p : stockBajo) {
            table.addCell(new Cell().add(new Paragraph(p.getCodigo())));
            table.addCell(new Cell().add(new Paragraph(p.getNombre())));
            table.addCell(new Cell().add(new Paragraph(p.getCategoria())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getStock()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getMinimo()))));
        }
        
        document.add(table);
        document.close();
    }
    
    private void generarReporteMovimientos(HttpServletResponse response) throws IOException {
        List<Movimiento> movimientos = movimientoDAO.listar();
        
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Título
        Paragraph titulo = new Paragraph("REPORTE DE MOVIMIENTOS - CAMVELS")
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold();
        document.add(titulo);
        
        // Fecha
        Paragraph fecha = new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(fecha);
        
        document.add(new Paragraph(" "));
        
        // Tabla
        Table table = new Table(UnitValue.createPercentArray(new float[]{12, 12, 25, 15, 15, 21}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("Fecha")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Tipo")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Producto")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Cantidad")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Usuario")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Observaciones")).setBold());
        
        // Datos
        for (Movimiento m : movimientos) {
            String fechaStr = m.getFecha() != null ? 
                m.getFecha().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
            table.addCell(new Cell().add(new Paragraph(fechaStr)));
            
            String tipoStr;
            switch (m.getTipo()) {
                case "ENTRADA" -> tipoStr = "Entrada";
                case "SALIDA" -> tipoStr = "Salida";
                case "AJUSTE" -> tipoStr = "Ajuste";
                default -> tipoStr = m.getTipo();
            }
            table.addCell(new Cell().add(new Paragraph(tipoStr)));
            
            String productoStr = (m.getProductoCodigo() != null ? m.getProductoCodigo() : "") + " - " + 
                                 (m.getProductoNombre() != null ? m.getProductoNombre() : "");
            table.addCell(new Cell().add(new Paragraph(productoStr)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(m.getCantidad()))));
            
            String usuarioStr = m.getUsuarioNombre() != null ? m.getUsuarioNombre() : "";
            table.addCell(new Cell().add(new Paragraph(usuarioStr)));
            
            String obsStr = m.getObservaciones() != null && !m.getObservaciones().isEmpty() ? m.getObservaciones() : "Sin observaciones";
            table.addCell(new Cell().add(new Paragraph(obsStr.length() > 30 ? obsStr.substring(0, 30) + "..." : obsStr)));
        }
        
        document.add(table);
        document.close();
    }
    
    private void generarReporteProveedores(HttpServletResponse response) throws IOException {
        List<Proveedor> proveedores = proveedorDAO.listar();
        
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Título
        Paragraph titulo = new Paragraph("REPORTE DE PROVEEDORES - CAMVELS")
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold();
        document.add(titulo);
        
        // Fecha
        Paragraph fecha = new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(fecha);
        
        document.add(new Paragraph(" "));
        
        // Tabla
        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 15, 30, 25, 20}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        table.addHeaderCell(new Cell().add(new Paragraph("ID")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("RUC")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Nombre")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Teléfono")).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Email")).setBold());
        
        // Datos
        for (Proveedor p : proveedores) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getId()))));
            table.addCell(new Cell().add(new Paragraph(p.getRuc())));
            table.addCell(new Cell().add(new Paragraph(p.getNombre())));
            table.addCell(new Cell().add(new Paragraph(p.getTelefono() != null ? p.getTelefono() : "")));
            table.addCell(new Cell().add(new Paragraph(p.getEmail() != null ? p.getEmail() : "")));
        }
        
        document.add(table);
        document.close();
    }
    
    private void generarReporteProveedoresProductos(HttpServletResponse response, Integer proveedorIdFiltro, 
                     boolean incluirStockBajo, boolean incluirMalEstado) throws IOException {
        Map<Integer, List<Object[]>> productosPorProveedor = productoDAO.obtenerProductosConProblemasPorProveedor();
        
        Map<Integer, List<Object[]>> productosFiltrados = new java.util.LinkedHashMap<>();
        
        for (Map.Entry<Integer, List<Object[]>> entry : productosPorProveedor.entrySet()) {
            Integer proveedorId = entry.getKey();
            List<Object[]> productos = entry.getValue();
            List<Object[]> productosValidos = new java.util.ArrayList<>();
            
            // Si se especificó un proveedor, solo procesar ese proveedor
            if (proveedorIdFiltro != null && proveedorIdFiltro > 0 && !proveedorIdFiltro.equals(proveedorId)) {
                continue;
            }
            
            for (Object[] producto : productos) {
                if (producto == null || producto.length < 10) continue;
                
                int stock = producto[4] != null ? (Integer) producto[4] : 0;
                int minimo = producto[5] != null ? (Integer) producto[5] : 0;
                int stockMalEstado = producto[7] != null ? (Integer) producto[7] : 0;
                String estado = producto[9] != null ? (String) producto[9] : "";
                
                boolean tieneStockBajo = stock <= minimo;
                boolean estaMalEstado = stockMalEstado > 0 || "mal_estado".equals(estado);
                
                if ((incluirStockBajo && tieneStockBajo) || (incluirMalEstado && estaMalEstado)) {
                    productosValidos.add(producto);
                }
            }
            
            if (!productosValidos.isEmpty()) {
                productosFiltrados.put(proveedorId, productosValidos);
            }
        }
        
        productosPorProveedor = productosFiltrados;
        
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Título
        Paragraph titulo = new Paragraph("REPORTE PARA PROVEEDORES - CAMVELS")
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold();
        document.add(titulo);
        
        StringBuilder subtituloText = new StringBuilder("Productos: ");
        if (incluirStockBajo && incluirMalEstado) {
            subtituloText.append("Stock Bajo y Mal Estado");
        } else if (incluirStockBajo) {
            subtituloText.append("Stock Bajo");
        } else {
            subtituloText.append("Mal Estado");
        }
        Paragraph subtitulo = new Paragraph(subtituloText.toString())
            .setFontSize(14)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold();
        document.add(subtitulo);
        
        // Fecha
        Paragraph fecha = new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(fecha);
        
        document.add(new Paragraph(" ")); // Espacio
        
        if (productosPorProveedor.isEmpty()) {
            Paragraph sinDatos = new Paragraph("No hay productos con problemas que reportar.")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER);
            document.add(sinDatos);
        } else {
            // Para cada proveedor
            for (Map.Entry<Integer, List<Object[]>> entry : productosPorProveedor.entrySet()) {
                List<Object[]> productos = entry.getValue();
                if (productos.isEmpty()) continue;
                
                Object[] primerProducto = productos.get(0);
                if (primerProducto == null || primerProducto.length < 16) continue;
                
                String proveedorNombre = primerProducto[11] != null ? (String) primerProducto[11] : "N/A";
                String proveedorRuc = primerProducto[12] != null ? (String) primerProducto[12] : "N/A";
                String proveedorTelefono = primerProducto[13] != null ? (String) primerProducto[13] : "N/A";
                String proveedorEmail = primerProducto[14] != null ? (String) primerProducto[14] : "N/A";
                
                document.add(new Paragraph(" ")); 
                
                Paragraph provTitulo = new Paragraph("PROVEEDOR: " + proveedorNombre)
                    .setFontSize(14)
                    .setBold()
                    .setMarginTop(10);
                document.add(provTitulo);
                
                Paragraph provInfo = new Paragraph("RUC: " + (proveedorRuc != null ? proveedorRuc : "N/A") + 
                    " | Tel: " + (proveedorTelefono != null ? proveedorTelefono : "N/A") + 
                    " | Email: " + (proveedorEmail != null ? proveedorEmail : "N/A"))
                    .setFontSize(10)
                    .setMarginBottom(5);
                document.add(provInfo);
                
                List<Object[]> productosStockBajo = new java.util.ArrayList<>();
                List<Object[]> productosMalEstado = new java.util.ArrayList<>();
                
                for (Object[] producto : productos) {
                    if (producto == null || producto.length < 10) continue;
                    
                    int stock = producto[4] != null ? (Integer) producto[4] : 0;
                    int minimo = producto[5] != null ? (Integer) producto[5] : 0;
                    int stockMalEstado = producto[7] != null ? (Integer) producto[7] : 0;
                    String estado = producto[9] != null ? (String) producto[9] : "";
                    
                    boolean tieneStockBajo = stock <= minimo;
                    boolean estaMalEstado = stockMalEstado > 0 || "mal_estado".equals(estado);
                    
                    // Solo agregar a cada lista si está seleccionado ese tipo Y el producto cumple ese criterio
                    if (incluirStockBajo && tieneStockBajo) {
                        productosStockBajo.add(producto);
                    }
                    if (incluirMalEstado && estaMalEstado) {
                        productosMalEstado.add(producto);
                    }
                }
                
                // Productos con Stock Bajo
                if (!productosStockBajo.isEmpty() && incluirStockBajo) {
                    Paragraph stockTitulo = new Paragraph("PRODUCTOS CON STOCK BAJO:")
                        .setFontSize(12)
                        .setBold()
                        .setMarginTop(8);
                    document.add(stockTitulo);
                    
                    Table tablaStock = new Table(new float[]{80, 180, 100, 70, 70, 80});
                    tablaStock.setWidth(UnitValue.createPercentValue(100));
                    tablaStock.setMarginBottom(10);
                    
                    tablaStock.addHeaderCell(new Cell().add(new Paragraph("Código"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    tablaStock.addHeaderCell(new Cell().add(new Paragraph("Producto"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    tablaStock.addHeaderCell(new Cell().add(new Paragraph("Categoría"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    tablaStock.addHeaderCell(new Cell().add(new Paragraph("Stock\nActual"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    tablaStock.addHeaderCell(new Cell().add(new Paragraph("Stock\nMínimo"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    tablaStock.addHeaderCell(new Cell().add(new Paragraph("Cantidad\nFaltante"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    
                    // Datos
                    for (Object[] producto : productosStockBajo) {
                        if (producto == null || producto.length < 10) continue;
                        
                        int stock = producto[4] != null ? (Integer) producto[4] : 0;
                        int minimo = producto[5] != null ? (Integer) producto[5] : 0;
                        int cantidadFaltante = Math.max(0, minimo - stock); // Calcular cantidad faltante
                        
                        String codigo = producto[1] != null ? ((String) producto[1]).trim() : "";
                        String nombre = producto[2] != null ? ((String) producto[2]).trim() : "";
                        String categoria = producto[3] != null ? ((String) producto[3]).trim() : "";
                        
                        if (nombre.length() > 40) {
                            nombre = nombre.substring(0, 37) + "...";
                        }
                        if (categoria.length() > 20) {
                            categoria = categoria.substring(0, 17) + "...";
                        }
                        
                        tablaStock.addCell(new Cell().add(new Paragraph(codigo))
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.LEFT));
                        tablaStock.addCell(new Cell().add(new Paragraph(nombre))
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.LEFT));
                        tablaStock.addCell(new Cell().add(new Paragraph(categoria))
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.LEFT));
                        tablaStock.addCell(new Cell().add(new Paragraph(String.valueOf(stock)))
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.CENTER));
                        tablaStock.addCell(new Cell().add(new Paragraph(String.valueOf(minimo)))
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.CENTER));
                        tablaStock.addCell(new Cell().add(new Paragraph(String.valueOf(cantidadFaltante)))
                            .setBold()
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.CENTER));
                    }
                    
                    document.add(tablaStock);
                }
                
                // Productos en Mal Estado
                if (!productosMalEstado.isEmpty() && incluirMalEstado) {
                    Paragraph estadoTitulo = new Paragraph("PRODUCTOS EN MAL ESTADO:")
                        .setFontSize(12)
                        .setBold()
                        .setMarginTop(8);
                    document.add(estadoTitulo);
                    
                    Table tablaEstado = new Table(new float[]{80, 200, 120, 90, 90});
                    tablaEstado.setWidth(UnitValue.createPercentValue(100));
                    tablaEstado.setMarginBottom(10);
                    
                    tablaEstado.addHeaderCell(new Cell().add(new Paragraph("Código"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    tablaEstado.addHeaderCell(new Cell().add(new Paragraph("Producto"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    tablaEstado.addHeaderCell(new Cell().add(new Paragraph("Categoría"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    tablaEstado.addHeaderCell(new Cell().add(new Paragraph("Stock\nMal Estado"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    tablaEstado.addHeaderCell(new Cell().add(new Paragraph("Stock\nTotal"))
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
                    
                    // Datos
                    for (Object[] producto : productosMalEstado) {
                        if (producto == null || producto.length < 10) continue;
                        
                        int stockMalEstado = producto[7] != null ? (Integer) producto[7] : 0; 
                        int stock = producto[4] != null ? (Integer) producto[4] : 0;
                        
                        String codigo = producto[1] != null ? ((String) producto[1]).trim() : "";
                        String nombre = producto[2] != null ? ((String) producto[2]).trim() : "";
                        String categoria = producto[3] != null ? ((String) producto[3]).trim() : "";
                        
                        if (nombre.length() > 50) {
                            nombre = nombre.substring(0, 47) + "...";
                        }
                        if (categoria.length() > 25) {
                            categoria = categoria.substring(0, 22) + "...";
                        }
                        
                        tablaEstado.addCell(new Cell().add(new Paragraph(codigo))
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.LEFT));
                        tablaEstado.addCell(new Cell().add(new Paragraph(nombre))
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.LEFT));
                        tablaEstado.addCell(new Cell().add(new Paragraph(categoria))
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.LEFT));
                        tablaEstado.addCell(new Cell().add(new Paragraph(String.valueOf(stockMalEstado)))
                            .setBold()
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.CENTER));
                        tablaEstado.addCell(new Cell().add(new Paragraph(String.valueOf(stock)))
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.CENTER));
                    }
                    
                    document.add(tablaEstado);
                }
                
                document.add(new Paragraph(" "));
            }
        }
        
        // Nota al final
        document.add(new Paragraph(" "));
        StringBuilder notaText = new StringBuilder("Este reporte contiene productos que requieren atención: ");
        if (incluirStockBajo && incluirMalEstado) {
            notaText.append("reposición de stock (cantidad faltante calculada según stock mínimo) y/o revisión por mal estado.");
        } else if (incluirStockBajo) {
            notaText.append("reposición de stock. La cantidad faltante se calcula restando el stock actual del stock mínimo requerido.");
        } else {
            notaText.append("revisión por mal estado.");
        }
        Paragraph nota = new Paragraph(notaText.toString())
            .setFontSize(9)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(15);
        document.add(nota);
        
        document.close();
    }
}