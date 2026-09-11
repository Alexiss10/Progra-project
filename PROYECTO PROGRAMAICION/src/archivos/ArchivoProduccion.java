/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package archivos;

import clases.Produccion;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 *
 * @author ALEXIS
 */
public class ArchivoProduccion {

    private Path ruta = Path.of("datos", "produccion.txt");

    public void guardar(ArrayList<Produccion> producciones)
            throws IOException {

        ArrayList<String> lineas = new ArrayList<>();

        for (Produccion produccion : producciones) {

            String linea = produccion.getIdProduccion()
                    + ";" + produccion.getFecha()
                    + ";" + produccion.getCantProducida()
                    + ";" + produccion.getHora()
                    + ";" + produccion.getTela().getIdTela()
                    + ";" + produccion.getTurno().getIdTurno();

            lineas.add(linea);
        }

        Files.write(
                ruta,
                lineas,
                StandardCharsets.UTF_8
        );
    }
}
