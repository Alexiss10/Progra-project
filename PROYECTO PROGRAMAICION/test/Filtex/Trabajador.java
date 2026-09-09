package Filtex;

import java.util.ArrayList;

public class Trabajador {

    private final int idTrabajador;
    private String nombre;
    private String identificacion;
    private String puestoTrabajo;
    private String areaAsignada;

    private final ArrayList<Turno> turnos = new ArrayList<>();

    public Trabajador(int id, String nombre, String identificacion,
            String puesto, String area) {

        idTrabajador = id;
        modificar(nombre, identificacion, puesto, area);
    }

    public void modificar(String nombre, String identificacion,
            String puesto, String area) {

        this.nombre = nombre;
        this.identificacion = identificacion;
        puestoTrabajo = puesto;
        areaAsignada = area;
    }

    public boolean asignarTurno(Turno turno) {
        if (turnos.contains(turno)) {
            return false;
        }

        turnos.add(turno);
        return true;
    }

    public void quitarTurno(Turno turno) {
        turnos.remove(turno);
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getPuestoTrabajo() {
        return puestoTrabajo;
    }

    public String getAreaAsignada() {
        return areaAsignada;
    }

    public ArrayList<Turno> getTurnos() {
        return new ArrayList<>(turnos);
    }

    @Override
    public String toString() {
        return idTrabajador + " - " + nombre;
    }
}