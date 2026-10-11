package Filtex;

import java.io.IOException;
import java.util.ArrayList;

/** Gestiona trabajadores y sus asignaciones a turnos. */
public class GestionTrabajadores {

    private final ArrayList<Trabajador> trabajadores = new ArrayList<>();
    private final ArchivoTrabajadores archivoTrabajadores = new ArchivoTrabajadores();
    private final ArchivoAsignaciones archivoAsignaciones = new ArchivoAsignaciones();
    private int siguienteId = 1;
    private boolean cambiosPendientes = false;

    public GestionTrabajadores(GestionTurnos gestionTurnos) throws IOException {
        if (gestionTurnos == null) {
            throw new IllegalArgumentException("Debes proporcionar la gestión de turnos.");
        }

        int mayorId = 0;
        for (Trabajador t : archivoTrabajadores.cargar()) {
            trabajadores.add(t);
            mayorId = Math.max(mayorId, t.getIdTrabajador());
        }
        siguienteId = mayorId + 1;

        archivoAsignaciones.cargar(trabajadores, gestionTurnos);
    }

    public Trabajador buscarPorId(int idTrabajador) {
        for (Trabajador t : trabajadores) {
            if (t.getIdTrabajador() == idTrabajador) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Trabajador> listar() {
        return new ArrayList<>(trabajadores);
    }

    public Trabajador registrar(String nombre, String identificacion,
            String puesto, String area) {
        String[] d = validar(-1, nombre, identificacion, puesto, area);
        Trabajador t = new Trabajador(siguienteId, d[0], d[1], d[2], d[3]);
        trabajadores.add(t);
        siguienteId++;
        cambiosPendientes = true;
        return t;
    }

    public void modificar(int idTrabajador, String nombre, String identificacion,
            String puesto, String area) {
        Trabajador t = buscarPorId(idTrabajador);
        if (t == null) {
            throw new IllegalArgumentException("El trabajador no existe.");
        }
        String[] d = validar(idTrabajador, nombre, identificacion, puesto, area);
        t.modificar(d[0], d[1], d[2], d[3]);
        cambiosPendientes = true;
    }

    /** Sus asignaciones se eliminan junto con él. */
    public void eliminar(int idTrabajador) {
        Trabajador t = buscarPorId(idTrabajador);
        if (t == null) {
            throw new IllegalArgumentException("El trabajador no existe.");
        }
        trabajadores.remove(t);
        cambiosPendientes = true;
    }

    public void asignarTurno(int idTrabajador, Turno turno) {
        Trabajador t = buscarPorId(idTrabajador);
        if (t == null || turno == null) {
            throw new IllegalArgumentException("Selecciona un trabajador y un turno.");
        }
        if (!t.asignarTurno(turno)) {
            throw new IllegalArgumentException("El trabajador ya tiene ese turno.");
        }
        cambiosPendientes = true;
    }

    public void quitarTurno(int idTrabajador, Turno turno) {
        Trabajador t = buscarPorId(idTrabajador);
        if (t == null || turno == null) {
            throw new IllegalArgumentException("La asignación no existe.");
        }
        t.quitarTurno(turno);
        cambiosPendientes = true;
    }

    /** Se usa al eliminar un turno para no dejar asignaciones huérfanas. */
    public void quitarTurnoDeTodos(Turno turno) {
        for (Trabajador t : trabajadores) {
            if (t.getTurnos().contains(turno)) {
                t.quitarTurno(turno);
                cambiosPendientes = true;
            }
        }
    }

    public void guardarCambios() throws IOException {
        archivoTrabajadores.guardar(trabajadores);
        archivoAsignaciones.guardar(trabajadores);
        cambiosPendientes = false;
    }

    public boolean hayCambiosPendientes() {
        return cambiosPendientes;
    }

    private String[] validar(int idPropio, String nombre, String identificacion,
            String puesto, String area) {
        String[] d = {
            UtilArchivo.texto(nombre, "nombre"),
            UtilArchivo.texto(identificacion, "identificación"),
            UtilArchivo.texto(puesto, "puesto"),
            UtilArchivo.texto(area, "área")
        };
        for (Trabajador otro : trabajadores) {
            if (otro.getIdTrabajador() != idPropio
                    && otro.getIdentificacion().equalsIgnoreCase(d[1])) {
                throw new IllegalArgumentException("La identificación ya está registrada.");
            }
        }
        return d;
    }
}