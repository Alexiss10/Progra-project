package Filtex;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ArchivoTurnos {

    private static final String FICHERO = "turnos.txt";
    private final Path ruta = Path.of("datos", FICHERO);

    public void guardar(List<Turno> turnos) throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (Turno t : turnos) {
            lineas.add(t.getIdTurno() + ";" + t.getNombre() + ";"
                    + t.getHoraInicio() + ";" + t.getHoraFinalizacion());
        }
        UtilArchivo.escribir(ruta, lineas);
    }

    public ArrayList<Turno> cargar() throws IOException {
        ArrayList<Turno> cargados = new ArrayList<>();
        int numero = 0;

        for (String linea : UtilArchivo.leer(ruta)) {
            numero++;
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] d = UtilArchivo.campos(linea, 4, numero, FICHERO);

            try {
                int id = Integer.parseInt(d[0]);
                if (id <= 0) {
                    throw new IllegalArgumentException("El ID debe ser mayor a cero.");
                }
                String nombre = UtilArchivo.texto(d[1], "nombre");
                LocalTime inicio = LocalTime.parse(d[2]);
                LocalTime fin = LocalTime.parse(d[3]);

                if (inicio.equals(fin)) {
                    throw new IllegalArgumentException("Inicio y fin no pueden ser iguales.");
                }
                for (Turno otro : cargados) {
                    if (otro.getIdTurno() == id) {
                        throw new IllegalArgumentException("El ID " + id + " está repetido.");
                    }
                }
                cargados.add(new Turno(id, nombre, inicio, fin));

            } catch (DateTimeParseException | IllegalArgumentException e) {
                throw UtilArchivo.invalido(FICHERO, numero, e.getMessage(), e);
            }
        }
        return cargados;
    }
}

