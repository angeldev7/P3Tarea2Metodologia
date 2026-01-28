package service;

import model.Libro;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que maneja la lógica de negocio para la gestión de libros.
 * Implementa operaciones CRUD y búsquedas avanzadas.
 */
public class BibliotecaService {
    private Map<String, Libro> biblioteca;

    public BibliotecaService() {
        this.biblioteca = new HashMap<>();
    }

    public boolean agregarLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        
        if (!libro.esValido()) {
            throw new IllegalArgumentException("El libro no contiene información válida");
        }

        if (biblioteca.containsKey(libro.getIsbn())) {
            return false; // Ya existe
        }

        biblioteca.put(libro.getIsbn(), libro);
        return true;
    }

    /**
     * Busca un libro por ISBN
     * @param isbn el ISBN a buscar
     * @return el libro encontrado o null si no existe
     */
    public Libro buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN no puede estar vacío");
        }
        return biblioteca.get(isbn);
    }

    /**
     * Busca libros por título (búsqueda parcial, case-insensitive)
     * @param titulo el título o parte del título a buscar
     * @return lista de libros que coinciden
     */
    public List<Libro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String tituloLower = titulo.toLowerCase();
        return biblioteca.values().stream()
                .filter(libro -> libro.getTitulo().toLowerCase().contains(tituloLower))
                .collect(Collectors.toList());
    }

    /**
     * Busca libros por autor (búsqueda parcial, case-insensitive)
     * @param autor el autor o parte del nombre a buscar
     * @return lista de libros del autor
     */
    public List<Libro> buscarPorAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String autorLower = autor.toLowerCase();
        return biblioteca.values().stream()
                .filter(libro -> libro.getAutor().toLowerCase().contains(autorLower))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los libros disponibles
     * @return lista de libros disponibles
     */
    public List<Libro> obtenerLibrosDisponibles() {
        return biblioteca.values().stream()
                .filter(Libro::isDisponible)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza la información de un libro existente
     * @param isbn el ISBN del libro a actualizar
     * @param libroActualizado los nuevos datos del libro
     * @return true si se actualizó, false si no existe
     */
    public boolean actualizarLibro(String isbn, Libro libroActualizado) {
        if (isbn == null || libroActualizado == null) {
            throw new IllegalArgumentException("ISBN y libro no pueden ser nulos");
        }
        
        if (!libroActualizado.esValido()) {
            throw new IllegalArgumentException("El libro actualizado no es válido");
        }

        if (!biblioteca.containsKey(isbn)) {
            return false;
        }

        libroActualizado.setIsbn(isbn);
        biblioteca.put(isbn, libroActualizado);
        return true;
    }

    /**
     * Elimina un libro de la biblioteca
     * @param isbn el ISBN del libro a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarLibro(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN no puede estar vacío");
        }
        
        return biblioteca.remove(isbn) != null;
    }

    /**
     * Cambia el estado de disponibilidad de un libro
     * @param isbn el ISBN del libro
     * @param disponible el nuevo estado de disponibilidad
     * @return true si se cambió exitosamente
     */
    public boolean cambiarDisponibilidad(String isbn, boolean disponible) {
        Libro libro = buscarPorIsbn(isbn);
        if (libro == null) {
            return false;
        }
        
        libro.setDisponible(disponible);
        return true;
    }

    /**
     * Obtiene estadísticas básicas de la biblioteca
     * @return mapa con estadísticas
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalLibros = biblioteca.size();
        long librosDisponibles = biblioteca.values().stream()
                .mapToLong(libro -> libro.isDisponible() ? 1 : 0)
                .sum();
        
        double precioPromedio = biblioteca.values().stream()
                .mapToDouble(Libro::getPrecio)
                .average()
                .orElse(0.0);

        stats.put("totalLibros", totalLibros);
        stats.put("librosDisponibles", librosDisponibles);
        stats.put("librosNoDisponibles", totalLibros - librosDisponibles);
        stats.put("precioPromedio", Math.round(precioPromedio * 100.0) / 100.0);
        
        return stats;
    }

    /**
     * Obtiene el número total de libros en la biblioteca
     * @return número de libros
     */
    public int obtenerTotalLibros() {
        return biblioteca.size();
    }

    /**
     * Limpia toda la biblioteca (útil para pruebas)
     */
    public void limpiarBiblioteca() {
        biblioteca.clear();
    }
}