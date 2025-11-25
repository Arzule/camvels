package modelo;

public class Producto {
    private int id;
    private String codigo;
    private String nombre;
    private String categoria;
    private int stock; // Stock total (stock_buen_estado + stock_mal_estado)
    private int stockBuenEstado;
    private int stockMalEstado;
    private int minimo;
    private double precio;
    private String estado;
    private Integer proveedorId;

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getStockBuenEstado() { return stockBuenEstado; }
    public void setStockBuenEstado(int stockBuenEstado) { this.stockBuenEstado = stockBuenEstado; }
    public int getStockMalEstado() { return stockMalEstado; }
    public void setStockMalEstado(int stockMalEstado) { this.stockMalEstado = stockMalEstado; }
    public int getMinimo() { return minimo; }
    public void setMinimo(int minimo) { this.minimo = minimo; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }
    
    // Método auxiliar para calcular stock total si no está actualizado
    public void actualizarStockTotal() {
        this.stock = this.stockBuenEstado + this.stockMalEstado;
    }
} 