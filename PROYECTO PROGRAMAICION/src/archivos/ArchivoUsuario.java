package archivos;

import seguridad.Usuario;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ArchivoUsuario {

    private final Path ruta = Path.of("datos", "usuario.txt");

    public Usuario cargar() throws IOException {

        List<String> lineas;

        try {

            lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);

        } catch (NoSuchFileException ex) {

            return null;
        }

        if (lineas.size() != 1) {
            throw new IOException(
                    "usuario.txt debe contener una sola cuenta."
            );
        }

        String[] datos = lineas.get(0).split(";", -1);

        if (datos.length != 4) {
            throw new IOException(
                    "El formato de usuario.txt no es válido."
            );
        }

        try {

            return new Usuario(
                    datos[0],
                    datos[1],
                    Integer.parseInt(datos[2]),
                    Long.parseLong(datos[3])
            );

        } catch (IllegalArgumentException ex) {

            throw new IOException(
                    "No se pudo cargar el usuario: " + ex.getMessage(),
                    ex
            );
        }
    }

    public void guardar(Usuario usuario) throws IOException {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar un usuario."
            );
        }

        String linea = usuario.getNombreUsuario()
                + ";" + usuario.getHashClave()
                + ";" + usuario.getIntentosFallidos()
                + ";" + usuario.getBloqueadoHasta();

        Files.createDirectories(ruta.getParent());

        Path temporal = Files.createTempFile(
                ruta.getParent(),
                "usuario-",
                ".tmp"
        );

        try {

            Files.write(
                    temporal,
                    List.of(linea),
                    StandardCharsets.UTF_8
            );

            try {

                Files.move(
                        temporal,
                        ruta,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );

            } catch (AtomicMoveNotSupportedException ex) {

                Files.move(
                        temporal,
                        ruta,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } finally {

            Files.deleteIfExists(temporal);
        }
    }
}
