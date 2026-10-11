package Filtex;

import clases.Produccion;
import clases.gestionProduccion;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Formato: idUso;idRecurso;idProduccion;fecha;cantidad */
public class ArchivoUsoRecursos {

    private static final String FICHERO = "usoRecursos.txt";
    private final Path ruta = Path.of("datos", FICHERO);

    public void guardar(List<UsoRecurso> usos) throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (UsoRecurso u : usos) {
            lineas.add(u.getIdUso() + ";" + u.getRecurso().getIdRecurso() + ";"
                    + u.getProduccion().getIdProduccion() + ";" + u.getFecha() + ";"
                    + Recurso.formato(u.getCantidad()));
        }
        UtilArchivo.escribir(ruta, lineas);
    }

    /** Solo reconstruye el historial: nunca descuenta existencias. */
    public ArrayList<UsoRecurso> cargar(GestionRecursos gestionRecursos,
            gestionProduccion produccion) throws IOException {
        ArrayList<UsoRecurso> cargados = new ArrayList<>();
        int numero = 0;

        for (String linea : UtilArchivo.leer(ruta)) {
            numero++;
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] d = UtilArchivo.campos(linea, 5, numero, FICHERO);

            try {
                int id = Integer.parseInt(d[0]);
                int idRecurso = Integer.parseInt(d[1]);
                int idProduccion = Integer.parseInt(d[2]);
                LocalDate fecha = LocalDate.parse(d[3]);
                double cantidad = Double.parseDouble(d[4]);

                Recurso recurso = gestionRecursos.buscarPorId(idRecurso);
                Produccion prod = produccion.buscarPorId(idProduccion);

                if (recurso == null) {
                    throw new IllegalArgumentException("No existe el recurso con ID " + idRecurso + ".");
                }
                if (prod == null) {
                    throw new IllegalArgumentException("No existe la producción con ID " + idProduccion + ".");
                }
                for (UsoRecurso otro : cargados) {
                    if (otro.getIdUso() == id) {
                        throw new IllegalArgumentException("El ID " + id + " está repetido.");
                    }
                }
                cargados.add(new UsoRecurso(id, recurso, prod, fecha, cantidad));

            } catch (DateTimeParseException | IllegalArgumentException e) {
                throw UtilArchivo.invalido(FICHERO, numero, e.getMessage(), e);
            }
        }
        return cargados;
    }
}
