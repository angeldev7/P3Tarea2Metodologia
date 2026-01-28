package service;

import model.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase BibliotecaService.
 * Cubre todas las operaciones CRUD y funcionalidades del servicio.
 */
class BibliotecaServiceTest {

    private BibliotecaService biblioteca;
    private Libro libroValido;
    private Libro otroLibroValido;

    @BeforeEach
    void setUp() {
        biblioteca = new BibliotecaService();
        
        libroValido = new Libro("978-0134685991", "Effective Java", "Joshua Bloch", 
                               LocalDate.of(2017, 12, 27), 412, 45.99);
        
        otroLibroValido = new Libro("978-0596009205", "Head First Design Patterns", 
                                   "Eric Freeman", LocalDate.of(2004, 10, 25), 694, 39.99);
    }

    @Nested
    @DisplayName("Operaciones de Agregado")
    class OperacionesAgregado {

        @Test
        @DisplayName("agregarLibro debe agregar libro válido exitosamente")
        void agregarLibro_DebeAgregarLibroValidoExitosamente() {
            // Act
            boolean resultado = biblioteca.agregarLibro(libroValido);

            // Assert
            assertThat(resultado).isTrue();
            assertThat(biblioteca.obtenerTotalLibros()).isEqualTo(1);
            assertThat(biblioteca.buscarPorIsbn(libroValido.getIsbn())).isEqualTo(libroValido);
        }

        @Test
        @DisplayName("agregarLibro debe rechazar libro nulo")
        void agregarLibro_DebeRechazarLibroNulo() {
            // Assert
            assertThatThrownBy(() -> biblioteca.agregarLibro(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El libro no puede ser nulo");
        }

        @Test
        @DisplayName("agregarLibro debe rechazar libro inválido")
        void agregarLibro_DebeRechazarLibroInvalido() {
            // Arrange
            Libro libroInvalido = new Libro();
            // No setear propiedades requeridas

            // Assert
            assertThatThrownBy(() -> biblioteca.agregarLibro(libroInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El libro no contiene información válida");
        }

        @Test
        @DisplayName("agregarLibro debe rechazar libro duplicado")
        void agregarLibro_DebeRechazarLibroDuplicado() {
            // Arrange
            biblioteca.agregarLibro(libroValido);

            // Act
            boolean resultado = biblioteca.agregarLibro(libroValido);

            // Assert
            assertThat(resultado).isFalse();
            assertThat(biblioteca.obtenerTotalLibros()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Operaciones de Búsqueda")
    class OperacionesBusqueda {

        @BeforeEach
        void agregarLibrosParaPruebas() {
            biblioteca.agregarLibro(libroValido);
            biblioteca.agregarLibro(otroLibroValido);
        }

        @Test
        @DisplayName("buscarPorIsbn debe encontrar libro existente")
        void buscarPorIsbn_DebeEncontrarLibroExistente() {
            // Act
            Libro encontrado = biblioteca.buscarPorIsbn(libroValido.getIsbn());

            // Assert
            assertThat(encontrado).isEqualTo(libroValido);
        }

        @Test
        @DisplayName("buscarPorIsbn debe retornar null para libro inexistente")
        void buscarPorIsbn_DebeRetornarNullParaLibroInexistente() {
            // Act
            Libro encontrado = biblioteca.buscarPorIsbn("ISBN-INEXISTENTE");

            // Assert
            assertThat(encontrado).isNull();
        }

        @Test
        @DisplayName("buscarPorIsbn debe rechazar ISBN nulo o vacío")
        void buscarPorIsbn_DebeRechazarISBNNuloOVacio() {
            // Assert
            assertThatThrownBy(() -> biblioteca.buscarPorIsbn(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN no puede estar vacío");

            assertThatThrownBy(() -> biblioteca.buscarPorIsbn(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN no puede estar vacío");
        }

        @Test
        @DisplayName("buscarPorTitulo debe encontrar libros por coincidencia parcial")
        void buscarPorTitulo_DebeEncontrarLibrosPorCoincidenciaParcial() {
            // Act
            List<Libro> resultados = biblioteca.buscarPorTitulo("Java");

            // Assert
            assertThat(resultados).hasSize(1);
            assertThat(resultados.get(0)).isEqualTo(libroValido);
        }

        @Test
        @DisplayName("buscarPorTitulo debe ser case-insensitive")
        void buscarPorTitulo_DebeSerCaseInsensitive() {
            // Act
            List<Libro> resultados = biblioteca.buscarPorTitulo("JAVA");

            // Assert
            assertThat(resultados).hasSize(1);
            assertThat(resultados.get(0)).isEqualTo(libroValido);
        }

        @Test
        @DisplayName("buscarPorTitulo debe retornar lista vacía para título nulo o vacío")
        void buscarPorTitulo_DebeRetornarListaVaciaParaTituloNuloOVacio() {
            // Act & Assert
            assertThat(biblioteca.buscarPorTitulo(null)).isEmpty();
            assertThat(biblioteca.buscarPorTitulo("")).isEmpty();
            assertThat(biblioteca.buscarPorTitulo("   ")).isEmpty();
        }

        @Test
        @DisplayName("buscarPorAutor debe encontrar libros por autor")
        void buscarPorAutor_DebeEncontrarLibrosPorAutor() {
            // Act
            List<Libro> resultados = biblioteca.buscarPorAutor("Joshua");

            // Assert
            assertThat(resultados).hasSize(1);
            assertThat(resultados.get(0)).isEqualTo(libroValido);
        }

        @Test
        @DisplayName("obtenerLibrosDisponibles debe retornar solo libros disponibles")
        void obtenerLibrosDisponibles_DebeRetornarSoloLibrosDisponibles() {
            // Arrange
            libroValido.setDisponible(false);

            // Act
            List<Libro> disponibles = biblioteca.obtenerLibrosDisponibles();

            // Assert
            assertThat(disponibles).hasSize(1);
            assertThat(disponibles.get(0)).isEqualTo(otroLibroValido);
        }
    }

    @Nested
    @DisplayName("Operaciones de Actualización")
    class OperacionesActualizacion {

        @BeforeEach
        void agregarLibroParaPruebas() {
            biblioteca.agregarLibro(libroValido);
        }

        @Test
        @DisplayName("actualizarLibro debe actualizar libro existente")
        void actualizarLibro_DebeActualizarLibroExistente() {
            // Arrange
            Libro libroActualizado = new Libro(libroValido.getIsbn(), "Título Actualizado", 
                                              libroValido.getAutor(), libroValido.getFechaPublicacion(),
                                              libroValido.getNumeroPaginas(), libroValido.getPrecio());

            // Act
            boolean resultado = biblioteca.actualizarLibro(libroValido.getIsbn(), libroActualizado);

            // Assert
            assertThat(resultado).isTrue();
            Libro libroEnBiblioteca = biblioteca.buscarPorIsbn(libroValido.getIsbn());
            assertThat(libroEnBiblioteca.getTitulo()).isEqualTo("Título Actualizado");
        }

        @Test
        @DisplayName("actualizarLibro debe rechazar parámetros nulos")
        void actualizarLibro_DebeRechazarParametrosNulos() {
            // Assert
            assertThatThrownBy(() -> biblioteca.actualizarLibro(null, libroValido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN y libro no pueden ser nulos");

            assertThatThrownBy(() -> biblioteca.actualizarLibro(libroValido.getIsbn(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN y libro no pueden ser nulos");
        }

        @Test
        @DisplayName("actualizarLibro debe retornar false para libro inexistente")
        void actualizarLibro_DebeRetornarFalseParaLibroInexistente() {
            // Act
            boolean resultado = biblioteca.actualizarLibro("ISBN-INEXISTENTE", otroLibroValido);

            // Assert
            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("cambiarDisponibilidad debe cambiar estado de disponibilidad")
        void cambiarDisponibilidad_DebioCambiarEstadoDeDisponibilidad() {
            // Act
            boolean resultado = biblioteca.cambiarDisponibilidad(libroValido.getIsbn(), false);

            // Assert
            assertThat(resultado).isTrue();
            assertThat(libroValido.isDisponible()).isFalse();
        }

        @Test
        @DisplayName("cambiarDisponibilidad debe retornar false para libro inexistente")
        void cambiarDisponibilidad_DebeRetornarFalseParaLibroInexistente() {
            // Act
            boolean resultado = biblioteca.cambiarDisponibilidad("ISBN-INEXISTENTE", false);

            // Assert
            assertThat(resultado).isFalse();
        }
    }

    @Nested
    @DisplayName("Operaciones de Eliminación")
    class OperacionesEliminacion {

        @BeforeEach
        void agregarLibroParaPruebas() {
            biblioteca.agregarLibro(libroValido);
        }

        @Test
        @DisplayName("eliminarLibro debe eliminar libro existente")
        void eliminarLibro_DebeEliminarLibroExistente() {
            // Act
            boolean resultado = biblioteca.eliminarLibro(libroValido.getIsbn());

            // Assert
            assertThat(resultado).isTrue();
            assertThat(biblioteca.obtenerTotalLibros()).isZero();
            assertThat(biblioteca.buscarPorIsbn(libroValido.getIsbn())).isNull();
        }

        @Test
        @DisplayName("eliminarLibro debe retornar false para libro inexistente")
        void eliminarLibro_DebeRetornarFalseParaLibroInexistente() {
            // Act
            boolean resultado = biblioteca.eliminarLibro("ISBN-INEXISTENTE");

            // Assert
            assertThat(resultado).isFalse();
            assertThat(biblioteca.obtenerTotalLibros()).isEqualTo(1);
        }

        @Test
        @DisplayName("eliminarLibro debe rechazar ISBN nulo o vacío")
        void eliminarLibro_DebeRechazarISBNNuloOVacio() {
            // Assert
            assertThatThrownBy(() -> biblioteca.eliminarLibro(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN no puede estar vacío");

            assertThatThrownBy(() -> biblioteca.eliminarLibro(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISBN no puede estar vacío");
        }
    }

    @Nested
    @DisplayName("Estadísticas")
    class Estadisticas {

        @BeforeEach
        void agregarLibrosParaPruebas() {
            biblioteca.agregarLibro(libroValido);
            biblioteca.agregarLibro(otroLibroValido);
            libroValido.setDisponible(false);
        }

        @Test
        @DisplayName("obtenerEstadisticas debe calcular estadísticas correctamente")
        void obtenerEstadisticas_DebeCalcularEstadisticasCorrectamente() {
            // Act
            Map<String, Object> stats = biblioteca.obtenerEstadisticas();

            // Assert
            assertThat(stats.get("totalLibros")).isEqualTo(2);
            assertThat(stats.get("librosDisponibles")).isEqualTo(1L);
            assertThat(stats.get("librosNoDisponibles")).isEqualTo(1L);
            
            double precioPromedio = (Double) stats.get("precioPromedio");
            assertThat(precioPromedio).isBetween(42.0, 43.0);
        }

        @Test
        @DisplayName("obtenerTotalLibros debe retornar número correcto")
        void obtenerTotalLibros_DebeRetornarNumeroComorrecto() {
            // Act & Assert
            assertThat(biblioteca.obtenerTotalLibros()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("limpiarBiblioteca debe eliminar todos los libros")
    void limpiarBiblioteca_DebeEliminarTodosLosLibros() {
        // Arrange
        biblioteca.agregarLibro(libroValido);
        biblioteca.agregarLibro(otroLibroValido);

        // Act
        biblioteca.limpiarBiblioteca();

        // Assert
        assertThat(biblioteca.obtenerTotalLibros()).isZero();
    }
}