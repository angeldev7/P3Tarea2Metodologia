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

public class EjecutableTest {
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        Ejecutable.reiniciarBiblioteca();
        System.setOut(new PrintStream(outputStream));
    }
    
    @Nested
    class ConfiguracionYUtilidadesTest {
        
        @Test
        void getBiblioteca_DebeRetornarInstanciaNoNula() {
            assertThat(Ejecutable.getBiblioteca()).isNotNull();
        }
        
        @Test
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
            assertThat(outputStream.toString()).contains("Se han cargado 3 libros de ejemplo");
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
        void obtenerEstadisticasSistema_DebeRetornarEstadisticasCorrectasConLibros() {
            Ejecutable.cargarLibrosEjemplo();
            Map<String, Object> stats = Ejecutable.obtenerEstadisticasSistema();
            
            assertThat(stats.get("totalLibros")).isEqualTo(3);
            assertThat(stats.get("libroMasCaro")).isNotNull();
            assertThat(stats.get("libroMasBarato")).isNotNull();
            assertThat((Double) stats.get("precioPromedio")).isGreaterThan(0.0);
        }
        
        @Test
        void tieneLibrosCargados_DebeRetornarFalseParaSistemaVacio() {
            assertThat(Ejecutable.tieneLibrosCargados()).isFalse();
        }
        
        @Test
        void tieneLibrosCargados_DebeRetornarTrueConLibrosCargados() {
            Ejecutable.cargarLibrosEjemplo();
            assertThat(Ejecutable.tieneLibrosCargados()).isTrue();
        }
    }
    
    @Nested
    class BusquedasMultiplesTest {
        
        @BeforeEach
        void cargarDatos() {
            Ejecutable.cargarLibrosEjemplo();
        }
        
        @Test
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
            assertThat(output).contains("No se puede mostrar el detalle: libro no valido.");
        }
        
        @Test
        @DisplayName("mostrarDetalleLibro() con libro con descuento debe mostrar precio reducido")
        void mostrarDetalleLibro_ConLibroConDescuento_MuestraPrecioReducido() {
            // Libro con muchas páginas para tener descuento (>500 páginas = 20% descuento)
            Libro libroConDescuento = new Libro("978-1111111111", "Libro Extenso", "Autor Prolífico",
                                        LocalDate.of(2010, 1, 1), 600, 50.00);
            
            Ejecutable.mostrarDetalleLibro(libroConDescuento);
            String output = outputStream.toString();
            
            assertThat(output).contains("Libro Extenso");
            // El libro debe mostrar algún tipo de descuento o precio con descuento
            assertThat(output).containsAnyOf("Descuento:", "descuento:", "Precio con descuento:");
        }
        
        @Test
        @DisplayName("mostrarDetalleLibro() con libro reciente no debe mostrar descuento")
        void mostrarDetalleLibro_ConLibroReciente_NoMuestraDescuento() {
            // Libro reciente sin descuento
            Libro libroReciente = new Libro("978-2222222222", "Libro Nuevo", "Autor Moderno",
                                           LocalDate.now().minusMonths(6), 250, 35.00);
            
            Ejecutable.mostrarDetalleLibro(libroReciente);
            String output = outputStream.toString();
            
            assertThat(output).contains("Libro Nuevo");
            assertThat(output).doesNotContain("Descuento:");
            assertThat(output).doesNotContain("Precio con descuento:");
        }
    }
    
    @Nested
    @DisplayName("Tests de métodos privados a través de efectos")
    class MetodosPrivadosTest {
        
        @Test
        @DisplayName("cargarLibrosEjemplo debe cargar exactamente 3 libros")
        void cargarLibrosEjemplo_DebeCargarExactamente3Libros() {
            Ejecutable.cargarLibrosEjemplo();
            
            assertThat(Ejecutable.getBiblioteca().obtenerTotalLibros()).isEqualTo(3);
            String output = outputStream.toString();
            assertThat(output).contains("Se han cargado 3 libros de ejemplo");
        }
        
        @Test
        @DisplayName("cargarLibrosEjemplo debe cargar libros con datos específicos")
        void cargarLibrosEjemplo_DebeCargarLibrosConDatosEspecificos() {
            Ejecutable.cargarLibrosEjemplo();
            
            // Verificar que los libros específicos están cargados
            assertThat(Ejecutable.getBiblioteca().buscarPorIsbn("978-0134685991")).isNotNull();
            assertThat(Ejecutable.getBiblioteca().buscarPorIsbn("978-0596009205")).isNotNull();
            assertThat(Ejecutable.getBiblioteca().buscarPorIsbn("978-0321356680")).isNotNull();
            
            // Verificar datos específicos
            Libro effectiveJava = Ejecutable.getBiblioteca().buscarPorIsbn("978-0134685991");
            assertThat(effectiveJava.getTitulo()).isEqualTo("Effective Java");
            assertThat(effectiveJava.getAutor()).isEqualTo("Joshua Bloch");
            assertThat(effectiveJava.getPrecio()).isEqualTo(45.99);
        }
    }
    
    @Nested
    @DisplayName("Tests de validación avanzada")
    class ValidacionAvanzadaTest {
        
        @Test
        @DisplayName("validarLibroCompleto con libro válido completo")
        void validarLibroCompleto_ConLibroValidoCompleto_RetornaTrue() {
            Libro libroValido = new Libro("978-0134685991", "Effective Java", "Joshua Bloch",
                                         LocalDate.of(2020, 1, 1), 412, 45.99);
            
            boolean resultado = Ejecutable.validarLibroCompleto(libroValido);
            
            assertThat(resultado).isTrue();
        }
        
        @Test
        @DisplayName("validarLibroCompleto con libro null")
        void validarLibroCompleto_ConLibroNull_RetornaFalse() {
            boolean resultado = Ejecutable.validarLibroCompleto(null);
            
            assertThat(resultado).isFalse();
        }
        
        @Test
        @DisplayName("validarLibroCompleto con ISBN inválido")
        void validarLibroCompleto_ConIsbnInvalido_RetornaFalse() {
            Libro libroInvalido = new Libro("", "Título", "Autor",
                                           LocalDate.of(2020, 1, 1), 100, 20.00);
            
            boolean resultado = Ejecutable.validarLibroCompleto(libroInvalido);
            
            assertThat(resultado).isFalse();
        }
        
        @Test
        @DisplayName("validarLibroCompleto con precio inválido")
        void validarLibroCompleto_ConPrecioInvalido_RetornaFalse() {
            Libro libroInvalido = new Libro("978-0134685991", "Título", "Autor",
                                           LocalDate.of(2020, 1, 1), 100, -10.00);
            
            boolean resultado = Ejecutable.validarLibroCompleto(libroInvalido);
            
            assertThat(resultado).isFalse();
        }
        
        @Test
        @DisplayName("validarLibroCompleto con fecha futura")
        void validarLibroCompleto_ConFechaFutura_RetornaFalse() {
            Libro libroInvalido = new Libro("978-0134685991", "Título", "Autor",
                                           LocalDate.now().plusDays(1), 100, 20.00);
            
            boolean resultado = Ejecutable.validarLibroCompleto(libroInvalido);
            
            assertThat(resultado).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Tests de integración con sistema")
    class IntegracionSistemaTest {
        
        @Test
        @DisplayName("Sistema completo: cargar, buscar y generar reporte")
        void sistemaCompleto_CargarBuscarYGenerarReporte_FuncionaCorrectamente() {
            // Cargar datos
            Ejecutable.cargarLibrosEjemplo();
            
            // Buscar por múltiples criterios
            List<Libro> resultados = Ejecutable.buscarLibrosMultiplesCriterios(null, "Effective", null);
            assertThat(resultados).hasSize(2);
            
            // Generar reporte
            String reporte = Ejecutable.generarReporteSistema();
            assertThat(reporte).contains("Total de libros: 3");
            assertThat(reporte).contains("Libro más caro:");
            
            // Verificar estadísticas
            Map<String, Object> stats = Ejecutable.obtenerEstadisticasSistema();
            assertThat(stats.get("totalLibros")).isEqualTo(3);
            assertThat(stats.get("libroMasCaro")).isNotNull();
        }
        
        @Test
        @DisplayName("Flujo completo con biblioteca vacía")
        void flujoCompleto_ConBibliotecaVacia_ManejaCorrectamente() {
            // Verificar estado inicial
            assertThat(Ejecutable.tieneLibrosCargados()).isFalse();
            
            // Buscar en biblioteca vacía
            List<Libro> resultados = Ejecutable.buscarLibrosMultiplesCriterios("978-1111111111", "Test", "Autor");
            assertThat(resultados).isEmpty();
            
            // Generar reporte vacío
            String reporte = Ejecutable.generarReporteSistema();
            assertThat(reporte).contains("No hay libros en el sistema");
            
            // Estadísticas vacías
            Map<String, Object> stats = Ejecutable.obtenerEstadisticasSistema();
            assertThat(stats.get("totalLibros")).isEqualTo(0);
            assertThat(stats.get("libroMasCaro")).isNull();
        }
    }
    
    @Nested
    class MetodosAvanzadosTest {
        
        @Test
        void obtenerLibrosConDescuento_ConBibliotecaVacia_RetornaListaVacia() {
            List<Libro> librosConDescuento = Ejecutable.obtenerLibrosConDescuento();
            
            assertThat(librosConDescuento).isEmpty();
        }
        
        @Test
        void obtenerLibrosConDescuento_ConLibrosDeDiferentesFechas_FiltrarCorrectamente() {
            Libro libroConDescuento = new Libro("978-1111111111", "Libro Grande", "Autor A",
                                        LocalDate.of(2010, 1, 1), 600, 30.00);
            Libro libroSinDescuento = new Libro("978-2222222222", "Libro Pequeño", "Autor B",
                                           LocalDate.now(), 100, 40.00);
            
            Ejecutable.getBiblioteca().agregarLibro(libroConDescuento);
            Ejecutable.getBiblioteca().agregarLibro(libroSinDescuento);
            
            List<Libro> librosConDescuento = Ejecutable.obtenerLibrosConDescuento();
            
            assertThat(librosConDescuento).hasSize(1);
            assertThat(librosConDescuento.get(0).getTitulo()).isEqualTo("Libro Grande");
        }
        
        @Test
        @DisplayName("calcularValorTotalBiblioteca() con biblioteca vacía")
        void calcularValorTotalBiblioteca_ConBibliotecaVacia_RetornaCero() {
            double valorTotal = Ejecutable.calcularValorTotalBiblioteca();
            
            assertThat(valorTotal).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("calcularValorTotalBiblioteca() considerando descuentos")
        void calcularValorTotalBiblioteca_ConsiderandoDescuentos_CalculaCorrectamente() {
            // Agregar libro sin descuento (menos de 300 páginas)
            Libro libroSinDescuento = new Libro("978-1111111111", "Libro Pequeño", "Autor A",
                                        LocalDate.now(), 200, 100.00);
            // Agregar libro con 10% descuento (más de 300 páginas)
            Libro libroConDescuento = new Libro("978-2222222222", "Libro Mediano", "Autor B",
                                        LocalDate.of(2010, 1, 1), 350, 100.00);
            
            Ejecutable.getBiblioteca().agregarLibro(libroSinDescuento);
            Ejecutable.getBiblioteca().agregarLibro(libroConDescuento);
            
            double valorTotal = Ejecutable.calcularValorTotalBiblioteca();
            
            // Libro pequeño: 100.00 (sin descuento)
            // Libro mediano: 90.00 (10% descuento, 100 * 0.9)
            assertThat(valorTotal).isEqualTo(190.00);
        }
        
        @Test
        @DisplayName("contarLibrosPorDecada() con biblioteca vacía")
        void contarLibrosPorDecada_ConBibliotecaVacia_RetornaMapVacio() {
            Map<String, Integer> librosPorDecada = Ejecutable.contarLibrosPorDecada();
            
            assertThat(librosPorDecada).isEmpty();
        }
        
        @Test
        @DisplayName("contarLibrosPorDecada() con libros de diferentes décadas")
        void contarLibrosPorDecada_ConLibrosDeDiferentesDecadas_AgrupraCorrectamente() {
            // Agregar libros de diferentes décadas
            Libro libro2010 = new Libro("978-1111111111", "Libro 2010", "Autor A",
                                       LocalDate.of(2015, 1, 1), 200, 30.00);
            Libro libro2020 = new Libro("978-2222222222", "Libro 2020", "Autor B",
                                       LocalDate.of(2023, 1, 1), 300, 40.00);
            Libro otroLibro2010 = new Libro("978-3333333333", "Otro Libro 2010", "Autor C",
                                          LocalDate.of(2017, 1, 1), 250, 35.00);
            
            Ejecutable.getBiblioteca().agregarLibro(libro2010);
            Ejecutable.getBiblioteca().agregarLibro(libro2020);
            Ejecutable.getBiblioteca().agregarLibro(otroLibro2010);
            
            Map<String, Integer> librosPorDecada = Ejecutable.contarLibrosPorDecada();
            
            assertThat(librosPorDecada.get("2010s")).isEqualTo(2);
            assertThat(librosPorDecada.get("2020s")).isEqualTo(1);
        }
        
        @Test
        @DisplayName("obtenerLibrosRevisionPrecio() con biblioteca vacía")
        void obtenerLibrosRevisionPrecio_ConBibliotecaVacia_RetornaListaVacia() {
            List<Libro> librosRevision = Ejecutable.obtenerLibrosRevisionPrecio();
            
            assertThat(librosRevision).isEmpty();
        }
        
        @Test
        @DisplayName("obtenerLibrosRevisionPrecio() con precios normales")
        void obtenerLibrosRevisionPrecio_ConPreciosNormales_NoDevuelveLibros() {
            // Agregar libros con precios similares (promedio 50)
            Ejecutable.getBiblioteca().agregarLibro(new Libro("978-1111111111", "Libro 1", "Autor A",
                    LocalDate.now(), 200, 45.00));
            Ejecutable.getBiblioteca().agregarLibro(new Libro("978-2222222222", "Libro 2", "Autor B",
                    LocalDate.now(), 300, 50.00));
            Ejecutable.getBiblioteca().agregarLibro(new Libro("978-3333333333", "Libro 3", "Autor C",
                    LocalDate.now(), 250, 55.00));
            
            List<Libro> librosRevision = Ejecutable.obtenerLibrosRevisionPrecio();
            
            assertThat(librosRevision).isEmpty(); // Precios están en rango normal
        }
        
        @Test
        void obtenerLibrosRevisionPrecio_ConPreciosExtremos_DevuelveLibrosProblematicos() {
            Ejecutable.getBiblioteca().agregarLibro(new Libro("978-1111111111", "Libro Caro", "Autor A",
                    LocalDate.now(), 200, 300.00));
            Ejecutable.getBiblioteca().agregarLibro(new Libro("978-2222222222", "Libro Normal", "Autor B",
                    LocalDate.now(), 250, 50.00));
            Ejecutable.getBiblioteca().agregarLibro(new Libro("978-3333333333", "Libro Barato", "Autor C",
                    LocalDate.now(), 150, 5.00));
            
            List<Libro> librosRevision = Ejecutable.obtenerLibrosRevisionPrecio();
            
            assertThat(librosRevision).hasSize(2);
            assertThat(librosRevision).extracting("titulo")
                    .contains("Libro Caro", "Libro Barato");
        }
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}