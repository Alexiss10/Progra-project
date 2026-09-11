package archivos;

import clases.Tela;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ArchivoTelas {

    private Path ruta = Path.of("datos", "telas.txt");

    public ArrayList<Tela> cargar() throws IOException {

        ArrayList<Tela> telasCargadas = new ArrayList<>();

        List<String> lineas = Files.readAllLines(
                ruta,
                StandardCharsets.UTF_8
        );

        int numeroLinea = 0;

        for (String linea : lineas) {

            numeroLinea++;

            String[] datos = linea.split(";", -1);

            if (datos.length != 4) {
                throw new IOException(
                        "La línea " + numeroLinea
                        + " debe contener cuatro datos."
                );
            }

            try {

                int idTela = Integer.parseInt(datos[0].trim());

                Tela tela = new Tela(
                        idTela,
                        datos[1],
                        datos[2],
                        datos[3]
                );

                telasCargadas.add(tela);

            } catch (IllegalArgumentException e) {

                throw new IOException(
                        "Datos inválidos en la línea "
                        + numeroLinea + ": " + e.getMessage(),
                        e
                );
            }
        }

        return telasCargadas;
    }

    public void guardar(ArrayList<Tela> telas) throws IOException {

        ArrayList<String> lineas = new ArrayList<>();

        for (Tela tela : telas) {

            String linea = tela.getIdTela()
                    + ";" + tela.getCodigo()
                    + ";" + tela.getNombre()
                    + ";" + tela.getDescripcion();

            lineas.add(linea);
        }
        
        Files.write(ruta, lineas, StandardCharsets.UTF_8);
    }
    
    
}
