package Principal;

import model.Libro;
import service.BibliotecaService;
import util.ValidadorUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase principal que proporciona una interfaz interactiva para el sistema de gestión de biblioteca.
 */
public class Ejecutable {
    
    private static BibliotecaService biblioteca = new BibliotecaService();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Obtiene la instancia del servicio de biblioteca para testing.
     * @return La instancia de BibliotecaService
     */
    public static BibliotecaService getBiblioteca() {
        return biblioteca;
    }

    /**
     * Reinicia la biblioteca (útil para testing).
     */
    public static void reiniciarBiblioteca() {
        biblioteca = new BibliotecaService();
    }

    /**
     * Obtiene el formatter de fecha utilizado por el sistema.
     * @return El DateTimeFormatter configurado
     */
    public static DateTimeFormatter getFormatter() {
        return formatter;
    }

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        SISTEMA DE BIBLIOTECA          ║");
        System.out.println("║          ¡Bienvenido!                 ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        cargarLibrosEjemplo();
        mostrarMenuPrincipal();
        
        System.out.println("\n¡Gracias por usar el Sistema de Biblioteca!");
        scanner.close();
    }
    
    public static void cargarLibrosEjemplo() {
        Libro libro1 = new Libro("978-0134685991", "Effective Java", "Joshua Bloch", 
                                 LocalDate.of(2017, 12, 27), 412, 45.99);
        
        Libro libro2 = new Libro("978-0596009205", "Head First Design Patterns", 
                                 "Eric Freeman", LocalDate.of(2004, 10, 25), 694, 39.99);
        
        Libro libro3 = new Libro("978-0321356680", "Effective C++", 
                                 "Scott Meyers", LocalDate.of(2005, 5, 22), 297, 42.50);
        
        biblioteca.agregarLibro(libro1);
        biblioteca.agregarLibro(libro2);
        biblioteca.agregarLibro(libro3);
        
        System.out.println("✓ Se han cargado " + biblioteca.obtenerTotalLibros() + " libros de ejemplo.\n");
    }
    
    private static void mostrarMenuPrincipal() {
        boolean continuar = true;
        
        while (continuar) {
            System.out.println("\n┌─────────────────────────────────────┐");
            System.out.println("│             MENÚ PRINCIPAL          │");
            System.out.println("├─────────────────────────────────────┤");
            System.out.println("│ 1. Agregar libro                    │");
            System.out.println("│ 2. Buscar libros                    │");
            System.out.println("│ 3. Listar todos los libros          │");
            System.out.println("│ 4. Ver estadísticas                 │");
            System.out.println("│ 5. Mostrar libro con descuento      │");
            System.out.println("│ 6. Demo de validaciones             │");
            System.out.println("│ 0. Salir                            │");
            System.out.println("└─────────────────────────────────────┘");
            System.out.print("Seleccione una opción: ");
            
            try {
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                
                switch (opcion) {
                    case 1:
                        agregarLibroInteractivo();
                        break;
                    case 2:
                        menuBusquedas();
                        break;
                    case 3:
                        listarTodosLosLibros();
                        break;
                    case 4:
                        mostrarEstadisticas();
                        break;
                    case 5:
                        mostrarLibroConDescuento();
                        break;
                    case 6:
                        demostrarValidaciones();
                        break;
                    case 0:
                        continuar = false;
                        break;
                    default:
                        System.out.println("❌ Opción no válida. Por favor, intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, ingrese un número válido.");
            }
        }
    }
    
    private static void agregarLibroInteractivo() {
        System.out.println("\n═══ AGREGAR NUEVO LIBRO ═══");
        
        try {
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine().trim();
            
            if (!ValidadorUtil.validarISBN(isbn)) {
                System.out.println("❌ ISBN no válido. Formato esperado: 978-XXXXXXXXX o XXX-XXX-XXX");
                return;
            }
            
            if (biblioteca.buscarPorIsbn(isbn) != null) {
                System.out.println("❌ Ya existe un libro con este ISBN.");
                return;
            }
            
            System.out.print("Título: ");
            String titulo = scanner.nextLine().trim();
            
            if (!ValidadorUtil.validarTextoNoVacio(titulo)) {
                System.out.println("❌ El título no puede estar vacío.");
                return;
            }
            
            System.out.print("Autor: ");
            String autor = scanner.nextLine().trim();
            
            if (!ValidadorUtil.validarTextoNoVacio(autor)) {
                System.out.println("❌ El autor no puede estar vacío.");
                return;
            }
            
            System.out.print("Número de páginas: ");
            int numPaginas = Integer.parseInt(scanner.nextLine().trim());
            
            if (numPaginas <= 0) {
                System.out.println("❌ El número de páginas debe ser positivo.");
                return;
            }
            
            System.out.print("Precio: $");
            double precio = Double.parseDouble(scanner.nextLine().trim());
            
            if (!ValidadorUtil.validarPrecio(precio)) {
                System.out.println("❌ El precio debe ser un número positivo.");
                return;
            }
            
            System.out.print("Fecha de publicación (dd/MM/yyyy): ");
            String fechaStr = scanner.nextLine().trim();
            LocalDate fecha = LocalDate.parse(fechaStr, formatter);
            
            Libro nuevoLibro = new Libro(isbn, titulo, autor, fecha, numPaginas, precio);
            boolean agregado = biblioteca.agregarLibro(nuevoLibro);
            
            if (agregado) {
                System.out.println("✅ Libro agregado exitosamente!");
                System.out.println("📖 " + nuevoLibro.getTitulo() + " por " + nuevoLibro.getAutor());
                System.out.println("📄 " + numPaginas + " páginas - $" + String.format("%.2f", precio));
            } else {
                System.out.println("❌ Error al agregar el libro.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: El precio o número de páginas debe ser un número válido.");
        } catch (DateTimeParseException e) {
            System.out.println("❌ Error: Formato de fecha incorrecto. Use dd/MM/yyyy");
        } catch (Exception e) {
            System.out.println("❌ Error inesperado: " + e.getMessage());
        }
    }
    
    private static void menuBusquedas() {
        System.out.println("\n═══ BUSCAR LIBROS ═══");
        System.out.println("1. Buscar por ISBN");
        System.out.println("2. Buscar por título");
        System.out.println("3. Buscar por autor");
        System.out.println("0. Volver al menú principal");
        System.out.print("Seleccione tipo de búsqueda: ");
        
        try {
            int opcion = Integer.parseInt(scanner.nextLine().trim());
            
            switch (opcion) {
                case 1:
                    buscarPorIsbn();
                    break;
                case 2:
                    buscarPorTitulo();
                    break;
                case 3:
                    buscarPorAutor();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ Opción no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Por favor, ingrese un número válido.");
        }
    }
    
    private static void buscarPorIsbn() {
        System.out.print("Ingrese ISBN: ");
        String isbn = scanner.nextLine().trim();
        
        Libro libro = biblioteca.buscarPorIsbn(isbn);
        if (libro != null) {
            System.out.println("\n✅ Libro encontrado:");
            mostrarDetalleLibro(libro);
        } else {
            System.out.println("❌ No se encontró ningún libro con ese ISBN.");
        }
    }
    
    private static void buscarPorTitulo() {
        System.out.print("Ingrese título (o parte del título): ");
        String titulo = scanner.nextLine().trim();
        
        List<Libro> libros = biblioteca.buscarPorTitulo(titulo);
        mostrarResultadosBusqueda(libros, "título");
    }
    
    private static void buscarPorAutor() {
        System.out.print("Ingrese autor (o parte del nombre): ");
        String autor = scanner.nextLine().trim();
        
        List<Libro> libros = biblioteca.buscarPorAutor(autor);
        mostrarResultadosBusqueda(libros, "autor");
    }
    
    private static void mostrarResultadosBusqueda(List<Libro> libros, String criterio) {
        if (libros.isEmpty()) {
            System.out.println("❌ No se encontraron libros con ese " + criterio + ".");
        } else {
            System.out.println("\n✅ Se encontraron " + libros.size() + " libro(s):");
            for (int i = 0; i < libros.size(); i++) {
                System.out.println("\n--- Libro " + (i + 1) + " ---");
                mostrarDetalleLibro(libros.get(i));
            }
        }
    }
    
    private static void listarTodosLosLibros() {
        System.out.println("\n═══ TODOS LOS LIBROS ═══");
        List<Libro> libros = biblioteca.obtenerLibrosDisponibles();
        
        if (libros.isEmpty()) {
            System.out.println("📚 No hay libros en la biblioteca.");
        } else {
            System.out.println("📚 Total de libros: " + libros.size());
            for (int i = 0; i < libros.size(); i++) {
                System.out.println("\n--- Libro " + (i + 1) + " ---");
                mostrarDetalleLibro(libros.get(i));
            }
        }
    }
    
    public static void mostrarDetalleLibro(Libro libro) {
        if (libro == null) {
            System.out.println("❌ No se puede mostrar el detalle: libro no válido.");
            return;
        }
        
        double descuento = libro.calcularDescuento();
        double precioFinal = libro.getPrecio() * (1 - descuento);
        
        System.out.println("📖 Título: " + libro.getTitulo());
        System.out.println("👤 Autor: " + libro.getAutor());
        System.out.println("🏷️  ISBN: " + libro.getIsbn());
        System.out.println("💰 Precio original: $" + String.format("%.2f", libro.getPrecio()));
        
        if (descuento > 0) {
            System.out.println("🎯 Descuento: " + (descuento * 100) + "%");
            System.out.println("💸 Precio con descuento: $" + String.format("%.2f", precioFinal));
        }
        
        System.out.println("📅 Fecha publicación: " + libro.getFechaPublicacion().format(formatter));
    }
    
    private static void mostrarEstadisticas() {
        System.out.println("\n═══ ESTADÍSTICAS DE LA BIBLIOTECA ═══");
        
        Map<String, Object> stats = biblioteca.obtenerEstadisticas();
        
        System.out.println("📚 Total de libros: " + stats.get("totalLibros"));
        System.out.println("📖 Libros disponibles: " + stats.get("librosDisponibles"));
        
        if ((Integer) stats.get("totalLibros") > 0) {
            System.out.println("💰 Precio promedio: $" + String.format("%.2f", stats.get("precioPromedio")));
            
            List<Libro> libros = biblioteca.obtenerLibrosDisponibles();
            Libro masCaro = libros.get(0);
            Libro masBarato = libros.get(0);
            
            for (Libro libro : libros) {
                if (libro.getPrecio() > masCaro.getPrecio()) {
                    masCaro = libro;
                }
                if (libro.getPrecio() < masBarato.getPrecio()) {
                    masBarato = libro;
                }
            }
            
            System.out.println("🏆 Libro más caro: " + masCaro.getTitulo() + " ($" + String.format("%.2f", masCaro.getPrecio()) + ")");
            System.out.println("💸 Libro más barato: " + masBarato.getTitulo() + " ($" + String.format("%.2f", masBarato.getPrecio()) + ")");
        }
    }
    
    private static void mostrarLibroConDescuento() {
        System.out.println("\n═══ LIBROS CON DESCUENTO ═══");
        List<Libro> libros = biblioteca.obtenerLibrosDisponibles();
        boolean hayDescuentos = false;
        
        for (Libro libro : libros) {
            double descuento = libro.calcularDescuento();
            if (descuento > 0) {
                if (!hayDescuentos) {
                    System.out.println("🎯 Libros con descuento disponible:");
                    hayDescuentos = true;
                }
                System.out.println("\n📖 " + libro.getTitulo());
                System.out.println("   Descuento: " + (descuento * 100) + "%");
                System.out.println("   Precio original: $" + String.format("%.2f", libro.getPrecio()));
                System.out.println("   Precio con descuento: $" + String.format("%.2f", libro.getPrecio() * (1 - descuento)));
            }
        }
        
        if (!hayDescuentos) {
            System.out.println("❌ No hay libros con descuento disponible en este momento.");
            System.out.println("💡 Los libros con 5+ años tienen 15% de descuento");
            System.out.println("💡 Los libros con 10+ años tienen 30% de descuento");
        }
    }
    
    private static void demostrarValidaciones() {
        System.out.println("\n═══ DEMOSTRACIÓN DE VALIDACIONES ═══");
        
        String isbnValido = "978-0134685991";
        String isbnInvalido = "123-456-789";
        
        System.out.println("📝 Validación de ISBN:");
        System.out.println("  ✅ " + isbnValido + " - Válido: " + ValidadorUtil.validarISBN(isbnValido));
        System.out.println("  ❌ " + isbnInvalido + " - Válido: " + ValidadorUtil.validarISBN(isbnInvalido));
        
        System.out.println("\n📝 Validación de texto:");
        System.out.println("  ✅ 'Effective Java' - Válido: " + ValidadorUtil.validarTextoNoVacio("Effective Java"));
        System.out.println("  ❌ '' (vacío) - Válido: " + ValidadorUtil.validarTextoNoVacio(""));
        System.out.println("  ❌ '   ' (espacios) - Válido: " + ValidadorUtil.validarTextoNoVacio("   "));
        
        System.out.println("\n📝 Validación de números:");
        System.out.println("  ✅ 45.99 - Válido: " + ValidadorUtil.validarPrecio(45.99));
        System.out.println("  ❌ -10.5 - Válido: " + ValidadorUtil.validarPrecio(-10.5));
        System.out.println("  ❌ 0 - Válido: " + ValidadorUtil.validarNumeroPositivo(0));
        
        LocalDate fechaValida = LocalDate.of(2020, 1, 15);
        LocalDate fechaFutura = LocalDate.now().plusDays(30);
        
        System.out.println("\n📝 Validación de fechas:");
        System.out.println("  ✅ " + fechaValida.format(formatter) + " - Válida: " + ValidadorUtil.validarFechaNoFutura(fechaValida));
        System.out.println("  ❌ " + fechaFutura.format(formatter) + " (futura) - Válida: " + ValidadorUtil.validarFechaNoFutura(fechaFutura));
        
        System.out.println("\n💡 Presione Enter para continuar...");
        scanner.nextLine();
    }
    
    private static void mostrarDespedida() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│      ¡Gracias por usar el Sistema   │");
        System.out.println("│         de Biblioteca Digital!      │");
        System.out.println("│                                     │");
        System.out.println("│        📚 Hasta la próxima 📚       │");
        System.out.println("└─────────────────────────────────────┘");
    }

    // ========== MÉTODOS DE UTILIDAD PARA TESTING ==========
    
    /**
     * Valida si un libro es completo y válido para el sistema.
     * @param libro El libro a validar
     * @return true si el libro es válido, false en caso contrario
     */
    public static boolean validarLibroCompleto(Libro libro) {
        if (libro == null) return false;
        
        return ValidadorUtil.validarTextoNoVacio(libro.getIsbn()) &&
               ValidadorUtil.validarTextoNoVacio(libro.getTitulo()) &&
               ValidadorUtil.validarTextoNoVacio(libro.getAutor()) &&
               libro.getFechaPublicacion() != null &&
               ValidadorUtil.validarFechaNoFutura(libro.getFechaPublicacion()) &&
               ValidadorUtil.validarNumeroPositivo(libro.getNumeroPaginas()) &&
               ValidadorUtil.validarPrecio(libro.getPrecio());
    }

    /**
     * Calcula estadísticas básicas del sistema.
     * @return Map con las estadísticas del sistema
     */
    public static Map<String, Object> obtenerEstadisticasSistema() {
        Map<String, Object> stats = biblioteca.obtenerEstadisticas();
        
        if (biblioteca.obtenerTotalLibros() > 0) {
            // Encontrar libro más caro y más barato
            Libro libroMasCaro = null;
            Libro libroMasBarato = null;
            double precioMaximo = Double.MIN_VALUE;
            double precioMinimo = Double.MAX_VALUE;
            
            for (Libro libro : biblioteca.obtenerLibrosDisponibles()) {
                if (libro.getPrecio() > precioMaximo) {
                    precioMaximo = libro.getPrecio();
                    libroMasCaro = libro;
                }
                if (libro.getPrecio() < precioMinimo) {
                    precioMinimo = libro.getPrecio();
                    libroMasBarato = libro;
                }
            }
            
            stats.put("libroMasCaro", libroMasCaro);
            stats.put("libroMasBarato", libroMasBarato);
        } else {
            stats.put("libroMasCaro", null);
            stats.put("libroMasBarato", null);
        }
        
        return stats;
    }

    /**
     * Verifica si el sistema tiene libros cargados.
     * @return true si hay al menos un libro, false en caso contrario
     */
    public static boolean tieneLibrosCargados() {
        return biblioteca.obtenerTotalLibros() > 0;
    }

    /**
     * Busca libros por múltiples criterios.
     * @param isbn ISBN a buscar (puede ser null)
     * @param titulo Título a buscar (puede ser null)
     * @param autor Autor a buscar (puede ser null)
     * @return Lista de libros que coinciden con los criterios
     */
    public static List<Libro> buscarLibrosMultiplesCriterios(String isbn, String titulo, String autor) {
        List<Libro> resultados = new java.util.ArrayList<>();
        
        if (ValidadorUtil.validarTextoNoVacio(isbn)) {
            Libro libroPorIsbn = biblioteca.buscarPorIsbn(isbn);
            if (libroPorIsbn != null) {
                resultados.add(libroPorIsbn);
            }
        }
        
        if (ValidadorUtil.validarTextoNoVacio(titulo)) {
            List<Libro> librosPorTitulo = biblioteca.buscarPorTitulo(titulo);
            for (Libro libro : librosPorTitulo) {
                if (!resultados.contains(libro)) {
                    resultados.add(libro);
                }
            }
        }
        
        if (ValidadorUtil.validarTextoNoVacio(autor)) {
            List<Libro> librosPorAutor = biblioteca.buscarPorAutor(autor);
            for (Libro libro : librosPorAutor) {
                if (!resultados.contains(libro)) {
                    resultados.add(libro);
                }
            }
        }
        
        return resultados;
    }

    /**
     * Genera un reporte resumen del estado del sistema.
     * @return String con el reporte formateado
     */
    public static String generarReporteSistema() {
        StringBuilder reporte = new StringBuilder();
        Map<String, Object> stats = obtenerEstadisticasSistema();
        
        reporte.append("=== REPORTE DEL SISTEMA ===\n");
        reporte.append("Total de libros: ").append(stats.get("totalLibros")).append("\n");
        
        Integer totalLibros = (Integer) stats.get("totalLibros");
        if (totalLibros != null && totalLibros > 0) {
            Libro masCaro = (Libro) stats.get("libroMasCaro");
            Libro masBarato = (Libro) stats.get("libroMasBarato");
            
            if (masCaro != null && masBarato != null) {
                reporte.append("Libro más caro: ").append(masCaro.getTitulo())
                       .append(" ($").append(masCaro.getPrecio()).append(")\n");
                reporte.append("Libro más barato: ").append(masBarato.getTitulo())
                       .append(" ($").append(masBarato.getPrecio()).append(")\n");
            }
            
            Object precioPromedio = stats.get("precioPromedio");
            if (precioPromedio != null) {
                reporte.append("Precio promedio: $").append(String.format("%.2f", precioPromedio)).append("\n");
            }
        } else {
            reporte.append("No hay libros en el sistema\n");
        }
        
        return reporte.toString();
    }
}
