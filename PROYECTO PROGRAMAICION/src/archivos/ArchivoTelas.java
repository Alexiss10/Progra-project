package archivos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JOptionPane;

public class ArchivoTelas {

    public String leerContenido() throws IOException {

        Path ruta = Path.of("datos", "telas.txt");

        String contenido = Files.readString(
                ruta,
                StandardCharsets.UTF_8
        );

        return contenido;
    }

    public static void main(String[] args) {

        ArchivoTelas archivo = new ArchivoTelas();

        try {

            String contenido = archivo.leerContenido();

            JOptionPane.showMessageDialog(
                    null,
                    contenido,
                    "Contenido de telas.txt",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo leer el archivo:\n" + e.getMessage(),
                    "Error de lectura",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}