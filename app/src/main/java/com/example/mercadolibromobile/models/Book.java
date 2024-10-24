package com.example.mercadolibromobile.models;

import com.google.gson.annotations.SerializedName;

public class Book {
    @SerializedName("id_libro")  // Mapeo del campo 'id_libro'
    private int idLibro;  // Campo renombrado a idLibro

    @SerializedName("titulo")  // Mapeo del campo 'titulo'
    private String titulo;

    @SerializedName("autor")  // Mapeo del campo 'autor'
    private String autor;

    @SerializedName("categoria")  // Mapeo del campo 'categoria'
    private String categoria;

    @SerializedName("precio")  // Mapeo del campo 'precio'
    private double precio;

    @SerializedName("stock")  // Mapeo del campo 'stock'
    private int stock;

    @SerializedName("portada")  // Mapeo del campo 'portada'
    private String portada;

    @SerializedName("descripcion")  // Mapeo del campo 'descripcion'
    private String descripcion;

    // Constructor
    public Book(int idLibro, String titulo, String autor, String categoria, double precio, int stock, String portada, String descripcion) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.portada = portada;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Book{" +
                "idLibro=" + idLibro +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", portada='" + portada + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
