package Principal;

import model.Libro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests para la clase Ejecutable
 */
public class EjecutableTest {
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        Ejecutable.reiniciarBiblioteca();
        System.setOut(new PrintStream(outputStream));
    }
    
    @Nested
    @DisplayName("Tests de configuración y utilidades")
    class ConfiguracionYUtilidadesTest {
        
        @Test
        @DisplayName("getBiblioteca() debe retornar instancia no nula")
        void getBiblioteca_DebeRetornarInstanciaNoNula() {
            assertThat(Ejecutable.getBiblioteca()).isNotNull();
        }
        
        @Test
        @DisplayName("getFormatter() debe retornar formatter configurado correctamente")
        void getFormatter_DebeRetornarFormatterConfigurado() {
            DateTimeFormatter formatter = Ejecutable.getFormatter();
            
            assertThat(formatter).isNotNull();
            LocalDate fecha = LocalDate.of(2024, 12, 25);
            assertThat(formatter.format(fecha)).isEqualTo("25/12/2024");
        }
        
        @Test
        @DisplayName("reiniciarBiblioteca() debe limpiar la biblioteca")
        void reiniciarBiblioteca_DebeLimpiarBiblioteca() {
            Ejecutable.cargarLibrosEjemplo();
            assertThat(Ejecutable.getBiblioteca().obtenerTotalLibros()).isGreaterThan(0);
            
            Ejecutable.reiniciarBiblioteca();
            
            assertThat(Ejecutable.getBiblioteca().obtenerTotalLibros()).isEqualTo(0);
        }
    }
    
    @Nested
    @DisplayName("Tests de carga de datos")
    class CargaDeDatosTest {
        
        @Test
        @DisplayName("cargarLibrosEjemplo() debe cargar libros correctamente")
        void cargarLibrosEjemplo_DebeCargarLibrosCorrectamente() {
            assertThat(Ejecutable.getBiblioteca().obtenerTotalLibros()).isEqualTo(0);
            
            Ejecutable.cargarLibrosEjemplo();
            
            assertThat(Ejecutable.getBiblioteca().obtenerTotalLibros()).isEqualTo(3);
            assertThat(outputStream.toString()).contains("✓ Se han cargado 3 libros de ejemplo");
        }
        
        @Test
        @DisplayName("cargarLibrosEjemplo() debe cargar libros específicos conocidos")
        void cargarLibrosEjemplo_DebeCargarLibrosEspecificos() {
            Ejecutable.cargarLibrosEjemplo();
            
            Libro effectiveJava = Ejecutable.getBiblioteca().buscarPorIsbn("978-0134685991");
            assertThat(effectiveJava).isNotNull();
            assertThat(effectiveJava.getTitulo()).isEqualTo("Effective Java");
            assertThat(effectiveJava.getAutor()).isEqualTo("Joshua Bloch");
            
            Libro headFirst = Ejecutable.getBiblioteca().buscarPorIsbn("978-0596009205");
            assertThat(headFirst).isNotNull();
            assertThat(headFirst.getTitulo()).isEqualTo("Head First Design Patterns");
            
            Libro effectiveCpp = Ejecutable.getBiblioteca().buscarPorIsbn("978-0321356680");
            assertThat(effectiveCpp).isNotNull();
            assertThat(effectiveCpp.getAutor()).isEqualTo("Scott Meyers");
        }
    }
    
    @Nested
    @DisplayName("Tests de validación de libros")
    class ValidacionLibrosTest {
        
        @Test
        @DisplayName("validarLibroCompleto() debe retornar true para libro válido")
        void validarLibroCompleto_DebeRetornarTrueParaLibroValido() {
            Libro libro = new Libro("978-0134685991", "Effective Java", "Joshua Bloch",
                                    LocalDate.of(2017, 12, 27), 412, 45.99);
            
            assertThat(Ejecutable.validarLibroCompleto(libro)).isTrue();
        }
        
        @Test
        @DisplayName("validarLibroCompleto() debe retornar false para libro nulo")
        void validarLibroCompleto_DebeRetornarFalseParaLibroNulo() {
            assertThat(Ejecutable.validarLibroCompleto(null)).isFalse();
        }
        
        @Test
        @DisplayName("validarLibroCompleto() debe retornar false para libro incompleto")
        void validarLibroCompleto_DebeRetornarFalseParaLibroIncompleto() {
            Libro libroIncompleto = new Libro();
            libroIncompleto.setTitulo("Solo título");
            
            assertThat(Ejecutable.validarLibroCompleto(libroIncompleto)).isFalse();
        }
        
        @Test
        @DisplayName("validarLibroCompleto() debe retornar false para fecha futura")
        void validarLibroCompleto_DebeRetornarFalseParaFechaFutura() {
            Libro libroFechaFutura = new Libro("978-0134685991", "Test Book", "Test Author",
                                               LocalDate.now().plusDays(30), 300, 25.99);
            
            assertThat(Ejecutable.validarLibroCompleto(libroFechaFutura)).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Tests de estadísticas del sistema")
    class EstadisticasSistemaTest {
        
        @Test
        @DisplayName("obtenerEstadisticasSistema() debe retornar estadísticas vacías para sistema vacío")
        void obtenerEstadisticasSistema_DebeRetornarEstadisticasVaciasParaSistemaVacio() {
            Map<String, Object> stats = Ejecutable.obtenerEstadisticasSistema();
            
            assertThat(stats.get("totalLibros")).isEqualTo(0);
            assertThat(stats.get("libroMasCaro")).isNull();
            assertThat(stats.get("libroMasBarato")).isNull();
            assertThat(stats.get("precioPromedio")).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("obtenerEstadisticasSistema() debe retornar estadísticas correctas con libros")
        void obtenerEstadisticasSistema_DebeRetornarEstadisticasCorrectasConLibros() {
            Ejecutable.cargarLibrosEjemplo();
            Map<String, Object> stats = Ejecutable.obtenerEstadisticasSistema();
            
            assertThat(stats.get("totalLibros")).isEqualTo(3);
            assertThat(stats.get("libroMasCaro")).isNotNull();
            assertThat(stats.get("libroMasBarato")).isNotNull();
            assertThat((Double) stats.get("precioPromedio")).isGreaterThan(0.0);
        }
        
        @Test
        @DisplayName("tieneLibrosCargados() debe retornar false para sistema vacío")
        void tieneLibrosCargados_DebeRetornarFalseParaSistemaVacio() {
            assertThat(Ejecutable.tieneLibrosCargados()).isFalse();
        }
        
        @Test
        @DisplayName("tieneLibrosCargados() debe retornar true con libros cargados")
        void tieneLibrosCargados_DebeRetornarTrueConLibrosCargados() {
            Ejecutable.cargarLibrosEjemplo();
            assertThat(Ejecutable.tieneLibrosCargados()).isTrue();
        }
    }
    
    @Nested
    @DisplayName("Tests de búsquedas múltiples")
    class BusquedasMultiplesTest {
        
        @BeforeEach
        void cargarDatos() {
            Ejecutable.cargarLibrosEjemplo();
        }
        
        @Test
        @DisplayName("buscarLibrosMultiplesCriterios() debe encontrar por ISBN")
        void buscarLibrosMultiplesCriterios_DebeEncontrarPorISBN() {
            List<Libro> resultados = Ejecutable.buscarLibrosMultiplesCriterios("978-0134685991", null, null);
            
            assertThat(resultados).hasSize(1);
            assertThat(resultados.get(0).getTitulo()).isEqualTo("Effective Java");
        }
        
        @Test
        @DisplayName("buscarLibrosMultiplesCriterios() debe encontrar por título")
        void buscarLibrosMultiplesCriterios_DebeEncontrarPorTitulo() {
            List<Libro> resultados = Ejecutable.buscarLibrosMultiplesCriterios(null, "Effective", null);
            
            assertThat(resultados).hasSize(2); // "Effective Java" y "Effective C++"
        }
        
        @Test
        @DisplayName("buscarLibrosMultiplesCriterios() debe encontrar por autor")
        void buscarLibrosMultiplesCriterios_DebeEncontrarPorAutor() {
            List<Libro> resultados = Ejecutable.buscarLibrosMultiplesCriterios(null, null, "Scott Meyers");
            
            assertThat(resultados).hasSize(1);
            assertThat(resultados.get(0).getTitulo()).isEqualTo("Effective C++");
        }
        
        @Test
        @DisplayName("buscarLibrosMultiplesCriterios() debe retornar lista vacía sin criterios válidos")
        void buscarLibrosMultiplesCriterios_DebeRetornarListaVaciaSinCriteriosValidos() {
            List<Libro> resultados = Ejecutable.buscarLibrosMultiplesCriterios(null, "", "   ");
            
            assertThat(resultados).isEmpty();
        }
        
        @Test
        @DisplayName("buscarLibrosMultiplesCriterios() debe evitar duplicados en múltiples criterios")
        void buscarLibrosMultiplesCriterios_DebeEvitarDuplicadosEnMultiplesCriterios() {
            List<Libro> resultados = Ejecutable.buscarLibrosMultiplesCriterios("978-0134685991", "Effective Java", "Joshua Bloch");
            
            assertThat(resultados).hasSize(1); // Mismo libro encontrado por 3 criterios
            assertThat(resultados.get(0).getTitulo()).isEqualTo("Effective Java");
        }
    }
    
    @Nested
    @DisplayName("Tests de reportes")
    class ReportesTest {
        
        @Test
        @DisplayName("generarReporteSistema() debe generar reporte para sistema vacío")
        void generarReporteSistema_DebeGenerarReporteParaSistemaVacio() {
            String reporte = Ejecutable.generarReporteSistema();
            
            assertThat(reporte).contains("=== REPORTE DEL SISTEMA ===");
            assertThat(reporte).contains("Total de libros: 0");
            assertThat(reporte).contains("No hay libros en el sistema");
        }
        
        @Test
        @DisplayName("generarReporteSistema() debe generar reporte completo con libros")
        void generarReporteSistema_DebeGenerarReporteCompletoConLibros() {
            Ejecutable.cargarLibrosEjemplo();
            String reporte = Ejecutable.generarReporteSistema();
            
            assertThat(reporte).contains("=== REPORTE DEL SISTEMA ===");
            assertThat(reporte).contains("Total de libros: 3");
            assertThat(reporte).contains("Libro más caro:");
            assertThat(reporte).contains("Libro más barato:");
            assertThat(reporte).contains("Precio promedio:");
        }
    }
    
    @Nested
    @DisplayName("Tests de UI (salida de consola)")
    class UITest {
        
        @Test
        @DisplayName("mostrarDetalleLibro() debe mostrar información del libro")
        void mostrarDetalleLibro_DebeMostrarInformacionDelLibro() {
            Libro libro = new Libro("978-0134685991", "Effective Java", "Joshua Bloch",
                                    LocalDate.of(2017, 12, 27), 412, 45.99);
            
            Ejecutable.mostrarDetalleLibro(libro);
            String output = outputStream.toString();
            
            assertThat(output).contains("Effective Java");
            assertThat(output).contains("Joshua Bloch");
            assertThat(output).contains("978-0134685991");
            assertThat(output).containsAnyOf("45.99", "45,99"); // Aceptar tanto formato US como español
        }
        
        @Test
        @DisplayName("mostrarDetalleLibro() no debe fallar con libro nulo")
        void mostrarDetalleLibro_NoDebeFallarConLibroNulo() {
            assertThatCode(() -> Ejecutable.mostrarDetalleLibro(null))
                .doesNotThrowAnyException();
            
            // Verificar que muestra mensaje de error
            String output = outputStream.toString();
            assertThat(output).contains("❌ No se puede mostrar el detalle: libro no válido.");
        }
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}