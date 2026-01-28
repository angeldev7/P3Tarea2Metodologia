package integration;

import model.Libro;
import service.BibliotecaService;
import util.ValidadorUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas de integración que verifican la interacción correcta entre
 * las diferentes capas del sistema.
 */
@Tag("integration")
class SistemaGestionLibrosIntegrationTest {

    private BibliotecaService biblioteca;

    @BeforeEach
    void setUp() {
        biblioteca = new BibliotecaService();
    }

    @Test
    @DisplayName("Flujo completo: agregar, buscar, actualizar y eliminar libro")
    void flujoCompleto_DeberiaFuncionarCorrectamente() {
        // 1. Crear y validar libro
        Libro libro = new Libro();
        String isbn = "9780134685991";
        String titulo = "Effective Java";
        String autor = "Joshua Bloch";
        
        // Validar datos antes de asignar
        assertThat(ValidadorUtil.validarTextoNoVacio(titulo)).isTrue();
        assertThat(ValidadorUtil.validarTextoNoVacio(autor)).isTrue();
        
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setFechaPublicacion(LocalDate.of(2017, 12, 27));
        libro.setNumeroPaginas(412);
        libro.setPrecio(45.99);

        // 2. Verificar que el libro es válido
        assertThat(libro.esValido()).isTrue();

        // 3. Agregar libro a la biblioteca
        boolean agregado = biblioteca.agregarLibro(libro);
        assertThat(agregado).isTrue();
        assertThat(biblioteca.obtenerTotalLibros()).isEqualTo(1);

        // 4. Buscar libro por ISBN
        Libro libroEncontrado = biblioteca.buscarPorIsbn(isbn);
        assertThat(libroEncontrado).isNotNull();
        assertThat(libroEncontrado.getTitulo()).isEqualTo(titulo);

        // 5. Buscar por título y autor
        List<Libro> porTitulo = biblioteca.buscarPorTitulo("Java");
        assertThat(porTitulo).hasSize(1);
        assertThat(porTitulo.get(0)).isEqualTo(libro);

        List<Libro> porAutor = biblioteca.buscarPorAutor("Joshua");
        assertThat(porAutor).hasSize(1);
        assertThat(porAutor.get(0)).isEqualTo(libro);

        // 6. Actualizar libro
        Libro libroActualizado = new Libro(isbn, "Effective Java 3rd Edition", 
                                          autor, LocalDate.of(2018, 1, 1), 450, 49.99);
        
        boolean actualizado = biblioteca.actualizarLibro(isbn, libroActualizado);
        assertThat(actualizado).isTrue();
        
        Libro libroModificado = biblioteca.buscarPorIsbn(isbn);
        assertThat(libroModificado.getTitulo()).isEqualTo("Effective Java 3rd Edition");
        assertThat(libroModificado.getPrecio()).isEqualTo(49.99);

        // 7. Cambiar disponibilidad
        boolean cambioDisponibilidad = biblioteca.cambiarDisponibilidad(isbn, false);
        assertThat(cambioDisponibilidad).isTrue();
        assertThat(libroModificado.isDisponible()).isFalse();

        List<Libro> disponibles = biblioteca.obtenerLibrosDisponibles();
        assertThat(disponibles).isEmpty();

        // 8. Eliminar libro
        boolean eliminado = biblioteca.eliminarLibro(isbn);
        assertThat(eliminado).isTrue();
        assertThat(biblioteca.obtenerTotalLibros()).isZero();
        assertThat(biblioteca.buscarPorIsbn(isbn)).isNull();
    }

    @Test
    @DisplayName("Integración con múltiples libros y estadísticas")
    void integracionMultiplesLibrosYEstadisticas() {
        // 1. Crear múltiples libros con diferentes características
        Libro libro1 = crearLibroValido("9780134685991", "Effective Java", "Joshua Bloch", 412, 45.99);
        Libro libro2 = crearLibroValido("9780596009205", "Head First Design Patterns", "Eric Freeman", 694, 39.99);
        Libro libro3 = crearLibroValido("9780321356680", "Effective C++", "Scott Meyers", 290, 42.99);

        // 2. Agregar todos los libros
        assertThat(biblioteca.agregarLibro(libro1)).isTrue();
        assertThat(biblioteca.agregarLibro(libro2)).isTrue();
        assertThat(biblioteca.agregarLibro(libro3)).isTrue();

        // 3. Verificar estadísticas iniciales
        Map<String, Object> stats = biblioteca.obtenerEstadisticas();
        assertThat(stats.get("totalLibros")).isEqualTo(3);
        assertThat(stats.get("librosDisponibles")).isEqualTo(3L);
        assertThat(stats.get("librosNoDisponibles")).isEqualTo(0L);

        // 4. Hacer algunos libros no disponibles
        biblioteca.cambiarDisponibilidad(libro1.getIsbn(), false);
        biblioteca.cambiarDisponibilidad(libro2.getIsbn(), false);

        // 5. Verificar estadísticas actualizadas
        stats = biblioteca.obtenerEstadisticas();
        assertThat(stats.get("totalLibros")).isEqualTo(3);
        assertThat(stats.get("librosDisponibles")).isEqualTo(1L);
        assertThat(stats.get("librosNoDisponibles")).isEqualTo(2L);

        // 6. Verificar libros disponibles
        List<Libro> disponibles = biblioteca.obtenerLibrosDisponibles();
        assertThat(disponibles).hasSize(1);
        assertThat(disponibles.get(0)).isEqualTo(libro3);

        // 7. Probar descuentos y precios finales
        double descuentoLibro1 = libro1.calcularDescuento();
        double descuentoLibro2 = libro2.calcularDescuento();
        double descuentoLibro3 = libro3.calcularDescuento();

        // libro1: 412 páginas -> 10% descuento
        assertThat(descuentoLibro1).isEqualTo(0.10);
        assertThat(libro1.calcularPrecioFinal()).isEqualTo(45.99 * 0.90);

        // libro2: 694 páginas -> 20% descuento  
        assertThat(descuentoLibro2).isEqualTo(0.20);
        assertThat(libro2.calcularPrecioFinal()).isEqualTo(39.99 * 0.80);

        // libro3: 290 páginas -> sin descuento  
        assertThat(descuentoLibro3).isEqualTo(0.0);
        assertThat(libro3.calcularPrecioFinal()).isEqualTo(42.99);
    }

    @Test
    @DisplayName("Validación integrada de datos y manejo de errores")
    void validacionIntegradaDeDatosYManejoErrores() {
        // 1. Intentar agregar libro con datos inválidos
        Libro libroInvalido = new Libro();
        
        assertThatThrownBy(() -> biblioteca.agregarLibro(libroInvalido))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("El libro no contiene información válida");

        // 2. Crear libro válido paso a paso con validaciones
        Libro libro = new Libro();
        
        // Validar y asignar ISBN
        String isbn = "9780134685991";
        libro.setIsbn(isbn);
        
        // Validar y asignar título
        String titulo = "Effective Java";
        assertThat(ValidadorUtil.validarTextoNoVacio(titulo)).isTrue();
        libro.setTitulo(titulo);
        
        // Validar y asignar autor
        String autor = "Joshua Bloch";
        assertThat(ValidadorUtil.validarTextoNoVacio(autor)).isTrue();
        libro.setAutor(autor);
        
        // Validar y asignar fecha
        LocalDate fecha = LocalDate.of(2017, 12, 27);
        assertThat(ValidadorUtil.validarFechaNoFutura(fecha)).isTrue();
        libro.setFechaPublicacion(fecha);
        
        // Validar y asignar páginas
        int paginas = 412;
        assertThat(ValidadorUtil.validarNumeroPositivo(paginas)).isTrue();
        assertThat(ValidadorUtil.validarRangoPaginas(paginas, 1, 2000)).isTrue();
        libro.setNumeroPaginas(paginas);
        
        // Validar y asignar precio
        double precio = 45.99;
        assertThat(ValidadorUtil.validarPrecio(precio)).isTrue();
        libro.setPrecio(precio);

        // 3. Verificar que el libro es válido y agregarlo
        assertThat(libro.esValido()).isTrue();
        assertThat(biblioteca.agregarLibro(libro)).isTrue();

        // 4. Intentar agregar libro duplicado
        assertThat(biblioteca.agregarLibro(libro)).isFalse();

        // 5. Probar operaciones con ISBN inválido
        assertThatThrownBy(() -> biblioteca.buscarPorIsbn(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ISBN no puede estar vacío");

        assertThatThrownBy(() -> biblioteca.eliminarLibro(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ISBN no puede estar vacío");

        // 6. Probar búsquedas con parámetros límite
        assertThat(biblioteca.buscarPorTitulo(null)).isEmpty();
        assertThat(biblioteca.buscarPorTitulo("")).isEmpty();
        assertThat(biblioteca.buscarPorAutor(null)).isEmpty();
        assertThat(biblioteca.buscarPorAutor("")).isEmpty();
    }

    @Test
    @DisplayName("Integración de flujo de trabajo real de biblioteca")
    void integracionFlujoTrabajoRealBiblioteca() {
        // Simular un día típico en una biblioteca

        // 1. Inicio del día - biblioteca vacía
        assertThat(biblioteca.obtenerTotalLibros()).isZero();

        // 2. Llegada de libros nuevos
        Libro[] librosNuevos = {
            crearLibroValido("9780134685991", "Effective Java", "Joshua Bloch", 412, 45.99),
            crearLibroValido("9780596009205", "Head First Design Patterns", "Eric Freeman", 694, 39.99),
            crearLibroValido("9780321356680", "Effective C++", "Scott Meyers", 290, 42.99),
            crearLibroValido("9780135166307", "Clean Code", "Robert Martin", 464, 49.99),
            crearLibroValido("9780201633610", "Design Patterns", "Gang of Four", 395, 54.99)
        };

        // 3. Catalogar libros
        for (Libro libro : librosNuevos) {
            assertThat(libro.esValido()).isTrue();
            assertThat(biblioteca.agregarLibro(libro)).isTrue();
        }

        assertThat(biblioteca.obtenerTotalLibros()).isEqualTo(5);

        // 4. Consulta de usuario: buscar libros sobre Java
        List<Libro> librosJava = biblioteca.buscarPorTitulo("Java");
        assertThat(librosJava).hasSize(1);

        // 5. Consulta de usuario: buscar libros de diseño
        List<Libro> librosDiseno = biblioteca.buscarPorTitulo("Design");
        assertThat(librosDiseno).hasSize(2);

        // 6. Préstamo de libros (marcar como no disponibles)
        String[] librosPrestados = {"9780134685991", "9780596009205"};
        for (String isbn : librosPrestados) {
            assertThat(biblioteca.cambiarDisponibilidad(isbn, false)).isTrue();
        }

        // 7. Verificar libros disponibles para préstamo
        List<Libro> disponibles = biblioteca.obtenerLibrosDisponibles();
        assertThat(disponibles).hasSize(3);

        // 8. Estadísticas del día
        Map<String, Object> stats = biblioteca.obtenerEstadisticas();
        assertThat(stats.get("totalLibros")).isEqualTo(5);
        assertThat(stats.get("librosDisponibles")).isEqualTo(3L);
        assertThat(stats.get("librosNoDisponibles")).isEqualTo(2L);

        // 9. Devolución de libro
        assertThat(biblioteca.cambiarDisponibilidad("9780134685991", true)).isTrue();
        
        disponibles = biblioteca.obtenerLibrosDisponibles();
        assertThat(disponibles).hasSize(4);

        // 10. Actualización de información de libro
        Libro libroActualizado = crearLibroValido("9780321356680", "Effective C++ 3rd Edition", 
                                                  "Scott Meyers", 350, 44.99);
        assertThat(biblioteca.actualizarLibro("9780321356680", libroActualizado)).isTrue();

        Libro verificacion = biblioteca.buscarPorIsbn("9780321356680");
        assertThat(verificacion.getTitulo()).isEqualTo("Effective C++ 3rd Edition");
    }

    /**
     * Método auxiliar para crear libros válidos para las pruebas
     */
    private Libro crearLibroValido(String isbn, String titulo, String autor, int paginas, double precio) {
        return new Libro(isbn, titulo, autor, LocalDate.of(2020, 1, 1), paginas, precio);
    }
}