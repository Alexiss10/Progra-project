package Filtex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Utilidades compartidas por los ficheros y gestores de Francisco. */
final class UtilArchivo {

    private UtilArchivo() {
    }

    /** Devuelve las líneas del fichero, o una lista vacía si todavía no existe. */
    static List<String> leer(Path ruta) throws IOException {
        if (!Files.exists(ruta)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(ruta, StandardCharsets.UTF_8);
    }

    /** Escribe en un temporal y luego reemplaza, para no dejar el fichero a medias. */
    static void escribir(Path ruta, List<String> lineas) throws IOException {
        Files.createDirectories(ruta.getParent());
        Path temporal = ruta.resolveSibling(ruta.getFileName() + ".tmp");
        Files.write(temporal, lineas, StandardCharsets.UTF_8);
        Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
    }

    static String[] campos(String linea, int esperados, int numeroLinea,
            String fichero) throws IOException {
        String[] datos = linea.split(";", -1);
        if (datos.length != esperados) {
            throw new IOException(fichero + ", línea " + numeroLinea
                    + ": debe contener " + esperados + " datos.");
        }
        for (int i = 0; i < datos.length; i++) {
            datos[i] = datos[i].trim();
        }
        return datos;
    }

    static IOException invalido(String fichero, int numeroLinea,
            String motivo, Exception causa) {
        return new IOException(fichero + ", línea " + numeroLinea
                + ": " + motivo, causa);
    }

    /** Texto obligatorio, sin punto y coma ni saltos de línea (formato del fichero). */
    static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo
                    + " es obligatorio.");
        }
        if (valor.contains(";") || valor.contains("\n") || valor.contains("\r")) {
            throw new IllegalArgumentException("El campo " + campo
                    + " no puede tener punto y coma ni saltos de línea.");
        }
        return valor.trim();
    }
}
