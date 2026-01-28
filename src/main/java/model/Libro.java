package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase que representa un libro en el sistema.
 * Contiene información básica del libro y métodos para su manipulación.
 */
public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private LocalDate fechaPublicacion;
    private int numeroPaginas;
    private double precio;
    private boolean disponible;

    public Libro() {
        this.disponible = true;
    }

    public Libro(String isbn, String titulo, String autor, LocalDate fechaPublicacion, 
                 int numeroPaginas, double precio) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.fechaPublicacion = fechaPublicacion;
        this.numeroPaginas = numeroPaginas;
        this.precio = precio;
        this.disponible = true;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN no puede estar vacío");
        }
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título no puede estar vacío");
        }
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("Autor no puede estar vacío");
        }
        this.autor = autor;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        if (fechaPublicacion != null && fechaPublicacion.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha de publicación no puede ser futura");
        }
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    public void setNumeroPaginas(int numeroPaginas) {
        if (numeroPaginas <= 0) {
            throw new IllegalArgumentException("Número de páginas debe ser positivo");
        }
        this.numeroPaginas = numeroPaginas;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("Precio no puede ser negativo");
        }
        this.precio = precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    /**
     * Calcula el descuento aplicable basado en el número de páginas
     * @return porcentaje de descuento (0-20)
     */
    public double calcularDescuento() {
        if (numeroPaginas > 500) {
            return 0.20;
        } else if (numeroPaginas > 300) {
            return 0.10;
        }
        return 0.0;
    }

    /**
     * Calcula el precio final con descuento aplicado
     * @return precio con descuento
     */
    public double calcularPrecioFinal() {
        return precio * (1 - calcularDescuento());
    }

    /**
     * Valida si el libro tiene toda la información requerida
     * @return true si está completo, false en caso contrario
     */
    public boolean esValido() {
        return isbn != null && !isbn.trim().isEmpty() &&
               titulo != null && !titulo.trim().isEmpty() &&
               autor != null && !autor.trim().isEmpty() &&
               numeroPaginas > 0 &&
               precio >= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Libro libro = (Libro) obj;
        return Objects.equals(isbn, libro.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("Libro{isbn='%s', titulo='%s', autor='%s', precio=%.2f, disponible=%s}",
                isbn, titulo, autor, precio, disponible);
    }
}