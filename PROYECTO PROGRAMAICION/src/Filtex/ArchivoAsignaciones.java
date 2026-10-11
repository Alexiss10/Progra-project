package Filtex;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Guarda y recupera las relaciones trabajador-turno (idTrabajador;idTurno). */
public class ArchivoAsignaciones {

    private static final String FICHERO = "asignaciones.txt";
    private final Path ruta = Path.of("datos", FICHERO);

    public void guardar(List<Trabajador> trabajadores) throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (Trabajador t : trabajadores) {
            for (Turno turno : t.getTurnos()) {
                lineas.add(t.getIdTrabajador() + ";" + turno.getIdTurno());
            }
        }
        UtilArchivo.escribir(ruta, lineas);
    }

    /** Reconstruye las asignaciones dentro de los trabajadores ya cargados. */
    public void cargar(List<Trabajador> trabajadores, GestionTurnos gestionTurnos)
            throws IOException {
        int numero = 0;

        for (String linea : UtilArchivo.leer(ruta)) {
            numero++;
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] d = UtilArchivo.campos(linea, 2, numero, FICHERO);

            try {
                int idTrabajador = Integer.parseInt(d[0]);
                int idTurno = Integer.parseInt(d[1]);

                Trabajador trabajador = null;
                for (Trabajador t : trabajadores) {
                    if (t.getIdTrabajador() == idTrabajador) {
                        trabajador = t;
                    }
                }
                Turno turno = gestionTurnos.buscarPorId(idTurno);

                if (trabajador == null) {
                    throw new IllegalArgumentException("No existe el trabajador con ID " + idTrabajador + ".");
                }
                if (turno == null) {
                    throw new IllegalArgumentException("No existe el turno con ID " + idTurno + ".");
                }
                if (!trabajador.asignarTurno(turno)) {
                    throw new IllegalArgumentException("La asignación está repetida.");
                }
            } catch (IllegalArgumentException e) {
                throw UtilArchivo.invalido(FICHERO, numero, e.getMessage(), e);
            }
        }
    }
}
