package Filtex;

import clases.gestionProduccion;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class GestionTurnos {

    private final ArrayList<Turno> turnos = new ArrayList<>();
    private final ArchivoTurnos archivo = new ArchivoTurnos();
    private int siguienteId = 1;
    private boolean cambiosPendientes = false;
    private gestionProduccion produccion;

    public GestionTurnos() throws IOException {
        int mayorId = 0;
        for (Turno t : archivo.cargar()) {
            turnos.add(t);
            mayorId = Math.max(mayorId, t.getIdTurno());
        }
        siguienteId = mayorId + 1;
    }

    /**
     * Producción se carga después que los turnos (los necesita), por eso se
     * conecta aquí una vez creada. Activa la protección de turnos utilizados.
     */
    public void setGestionProduccion(gestionProduccion produccion) {
        this.produccion = produccion;
    }

    public Turno buscarPorId(int idTurno) {
        for (Turno t : turnos) {
            if (t.getIdTurno() == idTurno) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<Turno> listar() {
        return new ArrayList<>(turnos);
    }

    public Turno registrar(String nombre, LocalTime inicio, LocalTime fin) {
        String nombreValido = validar(nombre, inicio, fin);
        Turno turno = new Turno(siguienteId, nombreValido, inicio, fin);
        turnos.add(turno);
        siguienteId++;
        cambiosPendientes = true;
        return turno;
    }

    public void modificar(int idTurno, String nombre, LocalTime inicio, LocalTime fin) {
        Turno turno = buscarPorId(idTurno);
        if (turno == null) {
            throw new IllegalArgumentException("El turno no existe.");
        }

        String nombreValido = validar(nombre, inicio, fin);

        boolean cambiaHorario = !inicio.equals(turno.getHoraInicio())
                || !fin.equals(turno.getHoraFinalizacion());

        if (cambiaHorario && estaUtilizado(idTurno)) {
            throw new IllegalArgumentException("No se puede cambiar el horario: "
                    + "el turno tiene producción registrada y se alterarían los "
                    + "registros históricos. Solo puedes cambiar el nombre.");
        }

        turno.modificar(nombreValido, inicio, fin);
        cambiosPendientes = true;
    }

    public Turno eliminar(int idTurno) {
        Turno turno = buscarPorId(idTurno);
        if (turno == null) {
            throw new IllegalArgumentException("El turno no existe.");
        }
        if (estaUtilizado(idTurno)) {
            throw new IllegalArgumentException("No se puede eliminar el turno: "
                    + "tiene producción registrada.");
        }
        turnos.remove(turno);
        cambiosPendientes = true;
        return turno;
    }

    public void guardarCambios() throws IOException {
        archivo.guardar(turnos);
        cambiosPendientes = false;
    }

    public boolean hayCambiosPendientes() {
        return cambiosPendientes;
    }

    private boolean estaUtilizado(int idTurno) {
        return produccion != null && produccion.tieneProduccionesDeTurno(idTurno);
    }

    private String validar(String nombre, LocalTime inicio, LocalTime fin) {
        String nombreValido = UtilArchivo.texto(nombre, "nombre");
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Debes indicar la hora de inicio y fin.");
        }
        if (inicio.equals(fin)) {
            throw new IllegalArgumentException("Las horas deben ser distintas.");
        }
        return nombreValido;
    }
}
