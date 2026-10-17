package Filtex;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class GuardadoRecursos {

    private static final Path CARPETA = Path.of("datos");

    private static final Path RESPALDO
            = CARPETA.resolve("respaldoRecursos.zip");

    private static final String RECURSOS = "recursos.txt";
    private static final String USOS = "usoRecursos.txt";

    private GuardadoRecursos() {
    }

    public static void guardar(List<Recurso> recursos,
            List<UsoRecurso> usos) throws IOException {

        // Resuelve primero cualquier guardado anterior incompleto.
        recuperarPendiente();

        crearRespaldo();

        try {
            new ArchivoRecursos().guardar(recursos);
            new ArchivoUsoRecursos().guardar(usos);

            // El guardado termina cuando ambos archivos están escritos.
            Files.delete(RESPALDO);

        } catch (IOException errorGuardado) {

            try {
                recuperarPendiente();

            } catch (IOException errorRecuperacion) {

                IOException error = new IOException(
                        "Falló el guardado y no se pudo recuperar el respaldo. "
                        + "No borres datos/respaldoRecursos.zip. "
                        + "El sistema intentará recuperarlo al iniciar.",
                        errorGuardado
                );

                error.addSuppressed(errorRecuperacion);
                throw error;
            }

            throw new IOException(
                    "No se guardaron los cambios de recursos y usos. "
                    + "Se recuperaron los archivos anteriores. "
                    + "Puedes volver a intentar guardar.",
                    errorGuardado
            );
        }
    }

    private static void crearRespaldo() throws IOException {

        Files.createDirectories(CARPETA);

        Path temporal = CARPETA.resolve("respaldoRecursos.tmp");

        try (ZipOutputStream salida = new ZipOutputStream(
                Files.newOutputStream(temporal))) {

            agregarAlRespaldo(salida, RECURSOS);
            agregarAlRespaldo(salida, USOS);
        }

        // Publica el respaldo únicamente cuando está completo.
        Files.move(
                temporal,
                RESPALDO,
                StandardCopyOption.ATOMIC_MOVE
        );
    }

    private static void agregarAlRespaldo(ZipOutputStream salida,
            String nombre) throws IOException {

        Path ruta = CARPETA.resolve(nombre);

        if (Files.notExists(ruta)) {
            return;
        }

        salida.putNextEntry(new ZipEntry(nombre));
        Files.copy(ruta, salida);
        salida.closeEntry();
    }

    public static void recuperarPendiente() throws IOException {

        if (Files.notExists(RESPALDO)) {
            return;
        }

        byte[] recursos;
        byte[] usos;

        // Lee el respaldo completo antes de restaurar los archivos.
        try (ZipFile zip = new ZipFile(RESPALDO.toFile())) {

            recursos = leerEntrada(zip, RECURSOS);
            usos = leerEntrada(zip, USOS);
        }

        restaurar(RECURSOS, recursos);
        restaurar(USOS, usos);

        // Si alguna restauración falla, el respaldo se conserva.
        Files.delete(RESPALDO);
    }

    private static byte[] leerEntrada(ZipFile zip, String nombre)
            throws IOException {

        ZipEntry entrada = zip.getEntry(nombre);

        if (entrada == null) {
            return null;
        }

        try (InputStream entradaDatos = zip.getInputStream(entrada)) {
            return entradaDatos.readAllBytes();
        }
    }

    private static void restaurar(String nombre, byte[] contenido)
            throws IOException {

        Path ruta = CARPETA.resolve(nombre);

        // Si antes no existía, vuelve a dejarlo sin crear.
        if (contenido == null) {
            Files.deleteIfExists(ruta);
            return;
        }

        Path temporal = CARPETA.resolve(nombre + ".restaurando");

        Files.write(temporal, contenido);

        Files.move(
                temporal,
                ruta,
                StandardCopyOption.REPLACE_EXISTING
        );
    }
}
