package Filtex;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ArchivoRecursos {

    private static final String FICHERO = "recursos.txt";
    private final Path ruta = Path.of("datos", FICHERO);

    public void guardar(List<Recurso> recursos) throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (Recurso r : recursos) {
            lineas.add(r.getIdRecurso() + ";" + r.getNombre() + ";" + r.getTipo() + ";"
                    + r.getUnidadMedida() + ";" + Recurso.formato(r.getCantidadDisponible())
                    + ";" + Recurso.formato(r.getCantidadMinima()));
        }
        UtilArchivo.escribir(ruta, lineas);
    }

    public ArrayList<Recurso> cargar() throws IOException {
        ArrayList<Recurso> cargados = new ArrayList<>();
        int numero = 0;

        for (String linea : UtilArchivo.leer(ruta)) {
            numero++;
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] d = UtilArchivo.campos(linea, 6, numero, FICHERO);

            try {
                int id = Integer.parseInt(d[0]);
                for (Recurso otro : cargados) {
                    if (otro.getIdRecurso() == id) {
                        throw new IllegalArgumentException("El ID " + id + " está repetido.");
                    }
                }
                cargados.add(new Recurso(id, d[1], d[2], d[3],
                        Double.parseDouble(d[4]), Double.parseDouble(d[5])));

            } catch (IllegalArgumentException e) {
                throw UtilArchivo.invalido(FICHERO, numero, e.getMessage(), e);
            }
        }
        return cargados;
    }
}
