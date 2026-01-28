package util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas unitarias para la clase ValidadorUtil.
 * Cubre todas las utilidades de validación del sistema.
 */
class ValidadorUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "9780134685991",
        "0134685997"
    })
    @DisplayName("validarISBN debe aceptar ISBNs válidos")
    void validarISBN_DebeAceptarISBNsValidos(String isbn) {
        // Act & Assert
        assertThat(ValidadorUtil.validarISBN(isbn)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
        "   ",
        "123",
        "invalid-isbn",
        "978-invalid",
        "12345"
    })
    @DisplayName("validarISBN debe rechazar ISBNs inválidos")
    void validarISBN_DebeRechazarISBNsInvalidos(String isbn) {
        // Act & Assert
        assertThat(ValidadorUtil.validarISBN(isbn)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Texto válido",
        "Java Programming",
        "   Texto con espacios   ",
        "A",
        "123"
    })
    @DisplayName("validarTextoNoVacio debe aceptar textos válidos")
    void validarTextoNoVacio_DebeAceptarTextosValidos(String texto) {
        // Act & Assert
        assertThat(ValidadorUtil.validarTextoNoVacio(texto)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("validarTextoNoVacio debe rechazar textos vacíos o nulos")
    void validarTextoNoVacio_DebeRechazarTextosVaciosONulos(String texto) {
        // Act & Assert
        assertThat(ValidadorUtil.validarTextoNoVacio(texto)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, Integer.MAX_VALUE})
    @DisplayName("validarNumeroPositivo debe aceptar números positivos")
    void validarNumeroPositivo_DebeAceptarNumerosPositivos(int numero) {
        // Act & Assert
        assertThat(ValidadorUtil.validarNumeroPositivo(numero)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10, -100, Integer.MIN_VALUE})
    @DisplayName("validarNumeroPositivo debe rechazar números no positivos")
    void validarNumeroPositivo_DebeRechazarNumerosNoPositivos(int numero) {
        // Act & Assert
        assertThat(ValidadorUtil.validarNumeroPositivo(numero)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.01, 1.0, 10.99, 100.0, Double.MAX_VALUE})
    @DisplayName("validarPrecio debe aceptar precios válidos")
    void validarPrecio_DebeAceptarPreciosValidos(double precio) {
        // Act & Assert
        assertThat(ValidadorUtil.validarPrecio(precio)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, -1.0, -10.0})
    @DisplayName("validarPrecio debe rechazar precios negativos")
    void validarPrecio_DebeRechazarPreciosNegativos(double precio) {
        // Act & Assert
        assertThat(ValidadorUtil.validarPrecio(precio)).isFalse();
    }

    @Test
    @DisplayName("validarFechaNoFutura debe aceptar fecha nula")
    void validarFechaNoFutura_DebeAceptarFechaNula() {
        // Act & Assert
        assertThat(ValidadorUtil.validarFechaNoFutura(null)).isTrue();
    }

    @Test
    @DisplayName("validarFechaNoFutura debe aceptar fecha pasada")
    void validarFechaNoFutura_DebeAceptarFechaPasada() {
        // Arrange
        LocalDate fechaPasada = LocalDate.now().minusDays(1);

        // Act & Assert
        assertThat(ValidadorUtil.validarFechaNoFutura(fechaPasada)).isTrue();
    }

    @Test
    @DisplayName("validarFechaNoFutura debe aceptar fecha presente")
    void validarFechaNoFutura_DebeAceptarFechaPresente() {
        // Arrange
        LocalDate fechaHoy = LocalDate.now();

        // Act & Assert
        assertThat(ValidadorUtil.validarFechaNoFutura(fechaHoy)).isTrue();
    }

    @Test
    @DisplayName("validarFechaNoFutura debe rechazar fecha futura")
    void validarFechaNoFutura_DebeRechazarFechaFutura() {
        // Arrange
        LocalDate fechaFutura = LocalDate.now().plusDays(1);

        // Act & Assert
        assertThat(ValidadorUtil.validarFechaNoFutura(fechaFutura)).isFalse();
    }

    @Test
    @DisplayName("limpiarTexto debe retornar cadena vacía para null")
    void limpiarTexto_DebeRetornarCadenaVaciaParaNull() {
        // Act & Assert
        assertThat(ValidadorUtil.limpiarTexto(null)).isEqualTo("");
    }

    @Test
    @DisplayName("limpiarTexto debe limpiar espacios en blanco")
    void limpiarTexto_DebeLimpiarEspaciosEnBlanco() {
        // Act & Assert
        assertThat(ValidadorUtil.limpiarTexto("  texto  ")).isEqualTo("texto");
        assertThat(ValidadorUtil.limpiarTexto("\ttexto\n")).isEqualTo("texto");
        assertThat(ValidadorUtil.limpiarTexto("texto")).isEqualTo("texto");
    }

    @Test
    @DisplayName("validarRangoPaginas debe validar rango correctamente")
    void validarRangoPaginas_DebeValidarRangoCorrectamente() {
        // Act & Assert
        assertThat(ValidadorUtil.validarRangoPaginas(100, 50, 200)).isTrue();
        assertThat(ValidadorUtil.validarRangoPaginas(50, 50, 200)).isTrue(); // límite inferior
        assertThat(ValidadorUtil.validarRangoPaginas(200, 50, 200)).isTrue(); // límite superior
        
        assertThat(ValidadorUtil.validarRangoPaginas(49, 50, 200)).isFalse(); // por debajo
        assertThat(ValidadorUtil.validarRangoPaginas(201, 50, 200)).isFalse(); // por encima
    }
}