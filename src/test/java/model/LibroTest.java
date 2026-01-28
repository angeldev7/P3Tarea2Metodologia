package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase Libro.
 * Cubre todos los métodos y casos edge de la clase.
 */
class LibroTest {

    private Libro libro;

    @BeforeEach
    void setUp() {
        libro = new Libro();
    }

    @Test
    @DisplayName("Constructor vacío debe crear libro con disponibilidad verdadera")
    void constructorVacio_DebeCrearLibroConDisponibilidadVerdadera() {
        // Act & Assert
        assertThat(libro.isDisponible()).isTrue();
        assertThat(libro.getIsbn()).isNull();
        assertThat(libro.getTitulo()).isNull();
        assertThat(libro.getAutor()).isNull();
    }

    @Test
    @DisplayName("Constructor completo debe asignar todos los valores correctamente")
    void constructorCompleto_DebeAsignarTodosLosValoresCorrectamente() {
        // Arrange
        String isbn = "978-0134685991";
        String titulo = "Effective Java";
        String autor = "Joshua Bloch";
        LocalDate fecha = LocalDate.of(2017, 12, 27);
        int paginas = 412;
        double precio = 45.99;

        // Act
        Libro libroCompleto = new Libro(isbn, titulo, autor, fecha, paginas, precio);

        // Assert
        assertThat(libroCompleto.getIsbn()).isEqualTo(isbn);
        assertThat(libroCompleto.getTitulo()).isEqualTo(titulo);
        assertThat(libroCompleto.getAutor()).isEqualTo(autor);
        assertThat(libroCompleto.getFechaPublicacion()).isEqualTo(fecha);
        assertThat(libroCompleto.getNumeroPaginas()).isEqualTo(paginas);
        assertThat(libroCompleto.getPrecio()).isEqualTo(precio);
        assertThat(libroCompleto.isDisponible()).isTrue();
    }

    @Test
    @DisplayName("setIsbn debe rechazar valores nulos o vacíos")
    void setIsbn_DebeRechazarValoresNulosOVacios() {
        // Assert
        assertThatThrownBy(() -> libro.setIsbn(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ISBN no puede estar vacío");

        assertThatThrownBy(() -> libro.setIsbn(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ISBN no puede estar vacío");

        assertThatThrownBy(() -> libro.setIsbn("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ISBN no puede estar vacío");
    }

    @Test
    @DisplayName("setIsbn debe aceptar valores válidos")
    void setIsbn_DebeAceptarValoresValidos() {
        // Arrange
        String isbnValido = "978-0134685991";

        // Act
        libro.setIsbn(isbnValido);

        // Assert
        assertThat(libro.getIsbn()).isEqualTo(isbnValido);
    }

    @Test
    @DisplayName("setTitulo debe rechazar valores nulos o vacíos")
    void setTitulo_DebeRechazarValoresNulosOVacios() {
        // Assert
        assertThatThrownBy(() -> libro.setTitulo(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Título no puede estar vacío");

        assertThatThrownBy(() -> libro.setTitulo(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Título no puede estar vacío");
    }

    @Test
    @DisplayName("setAutor debe rechazar valores nulos o vacíos")
    void setAutor_DebeRechazarValoresNulosOVacios() {
        // Assert
        assertThatThrownBy(() -> libro.setAutor(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Autor no puede estar vacío");

        assertThatThrownBy(() -> libro.setAutor(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Autor no puede estar vacío");
    }

    @Test
    @DisplayName("setFechaPublicacion debe rechazar fechas futuras")
    void setFechaPublicacion_DebeRechazarFechasFuturas() {
        // Arrange
        LocalDate fechaFutura = LocalDate.now().plusDays(1);

        // Assert
        assertThatThrownBy(() -> libro.setFechaPublicacion(fechaFutura))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Fecha de publicación no puede ser futura");
    }

    @Test
    @DisplayName("setFechaPublicacion debe aceptar fechas pasadas y presentes")
    void setFechaPublicacion_DebeAceptarFechasPasadosYPresentes() {
        // Arrange
        LocalDate fechaPasada = LocalDate.now().minusDays(1);
        LocalDate fechaHoy = LocalDate.now();

        // Act & Assert
        assertDoesNotThrow(() -> libro.setFechaPublicacion(fechaPasada));
        assertDoesNotThrow(() -> libro.setFechaPublicacion(fechaHoy));
        assertDoesNotThrow(() -> libro.setFechaPublicacion(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    @DisplayName("setNumeroPaginas debe rechazar números no positivos")
    void setNumeroPaginas_DebeRechazarNumerosNoPositivos(int paginasInvalidas) {
        // Assert
        assertThatThrownBy(() -> libro.setNumeroPaginas(paginasInvalidas))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Número de páginas debe ser positivo");
    }

    @Test
    @DisplayName("setNumeroPaginas debe aceptar números positivos")
    void setNumeroPaginas_DebeAceptarNumerosPositivos() {
        // Arrange & Act
        libro.setNumeroPaginas(100);

        // Assert
        assertThat(libro.getNumeroPaginas()).isEqualTo(100);
    }

    @Test
    @DisplayName("setPrecio debe rechazar valores negativos")
    void setPrecio_DebeRechazarValoresNegativos() {
        // Assert
        assertThatThrownBy(() -> libro.setPrecio(-1.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Precio no puede ser negativo");
    }

    @Test
    @DisplayName("setPrecio debe aceptar cero y valores positivos")
    void setPrecio_DebeAceptarCeroYValoresPositivos() {
        // Act & Assert
        assertDoesNotThrow(() -> libro.setPrecio(0.0));
        assertDoesNotThrow(() -> libro.setPrecio(29.99));

        libro.setPrecio(29.99);
        assertThat(libro.getPrecio()).isEqualTo(29.99);
    }

    @Test
    @DisplayName("calcularDescuento debe retornar 20% para libros de más de 500 páginas")
    void calcularDescuento_DebeRetornar20PorCientoParaLibrosDeMasDe500Paginas() {
        // Arrange
        libro.setNumeroPaginas(600);

        // Act
        double descuento = libro.calcularDescuento();

        // Assert
        assertThat(descuento).isEqualTo(0.20);
    }

    @Test
    @DisplayName("calcularDescuento debe retornar 10% para libros entre 301-500 páginas")
    void calcularDescuento_DebeRetornar10PorCientoParaLibrosEntre301Y500Paginas() {
        // Arrange
        libro.setNumeroPaginas(400);

        // Act
        double descuento = libro.calcularDescuento();

        // Assert
        assertThat(descuento).isEqualTo(0.10);
    }

    @Test
    @DisplayName("calcularDescuento debe retornar 0% para libros de 300 páginas o menos")
    void calcularDescuento_DebeRetornar0PorCientoParaLibrosDe300PaginasOMenos() {
        // Arrange
        libro.setNumeroPaginas(300);

        // Act
        double descuento = libro.calcularDescuento();

        // Assert
        assertThat(descuento).isEqualTo(0.0);
    }

    @Test
    @DisplayName("calcularPrecioFinal debe aplicar descuento correctamente")
    void calcularPrecioFinal_DebeAplicarDescuentoCorrectamente() {
        // Arrange
        libro.setPrecio(100.0);
        libro.setNumeroPaginas(600); // 20% descuento

        // Act
        double precioFinal = libro.calcularPrecioFinal();

        // Assert
        assertThat(precioFinal).isEqualTo(80.0);
    }

    @Test
    @DisplayName("esValido debe retornar false para libro incompleto")
    void esValido_DebeRetornarFalseParaLibroIncompleto() {
        // Act & Assert
        assertThat(libro.esValido()).isFalse();
    }

    @Test
    @DisplayName("esValido debe retornar true para libro completo")
    void esValido_DebeRetornarTrueParaLibroCompleto() {
        // Arrange
        libro.setIsbn("978-0134685991");
        libro.setTitulo("Effective Java");
        libro.setAutor("Joshua Bloch");
        libro.setNumeroPaginas(412);
        libro.setPrecio(45.99);

        // Act & Assert
        assertThat(libro.esValido()).isTrue();
    }

    @Test
    @DisplayName("equals debe comparar por ISBN")
    void equals_DebeCompararPorISBN() {
        // Arrange
        String isbn = "978-0134685991";
        Libro libro1 = new Libro();
        libro1.setIsbn(isbn);
        
        Libro libro2 = new Libro();
        libro2.setIsbn(isbn);

        // Act & Assert
        assertThat(libro1).isEqualTo(libro2);
        assertThat(libro1.hashCode()).isEqualTo(libro2.hashCode());
    }

    @Test
    @DisplayName("toString debe incluir información principal del libro")
    void toString_DebeIncluirInformacionPrincipalDelLibro() {
        // Arrange
        libro.setIsbn("978-0134685991");
        libro.setTitulo("Effective Java");
        libro.setAutor("Joshua Bloch");
        libro.setPrecio(45.99);

        // Act
        String resultado = libro.toString();

        // Assert
        assertThat(resultado)
            .contains("978-0134685991")
            .contains("Effective Java")
            .contains("Joshua Bloch")
            .contains("45")
            .contains("true");
    }
}