package util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utilidades para validación de datos en el sistema.
 */
public class ValidadorUtil {
    
    private static final Pattern ISBN_PATTERN = Pattern.compile("^[0-9]{10}$|^[0-9]{13}$|^[0-9]{9}[0-9X]$");
    
    public static boolean validarISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        return ISBN_PATTERN.matcher(isbn.trim()).matches();
    }


    public static boolean validarTextoNoVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Valida si un número es positivo
     * @param numero el número a validar
     * @return true si es positivo, false en caso contrario
     */
    public static boolean validarNumeroPositivo(int numero) {
        return numero > 0;
    }

    /**
     * Valida si un precio es válido (no negativo)
     * @param precio el precio a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean validarPrecio(double precio) {
        return precio >= 0;
    }

    /**
     * Valida si una fecha no es futura
     * @param fecha la fecha a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean validarFechaNoFutura(LocalDate fecha) {
        if (fecha == null) {
            return true;
        }
        return !fecha.isAfter(LocalDate.now());
    }

    /**
     * Limpia y normaliza un texto
     * @param texto el texto a limpiar
     * @return el texto limpio o cadena vacía si es nulo
     */
    public static String limpiarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim();
    }

    /**
     * Valida si un rango de páginas es válido
     * @param paginas el número de páginas
     * @param minimo mínimo número de páginas permitido
     * @param maximo máximo número de páginas permitido
     * @return true si está en el rango válido
     */
    public static boolean validarRangoPaginas(int paginas, int minimo, int maximo) {
        return paginas >= minimo && paginas <= maximo;
    }
}