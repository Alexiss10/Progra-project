package Filtex;
import java.time.LocalTime;

public class Turno {

    private final int idTurno;
    private String nombre;
    private LocalTime horaInicio;
    private LocalTime horaFinalizacion;

    public Turno(int id, String nombre, LocalTime inicio, LocalTime fin) {
        idTurno = id;
        modificar(nombre, inicio, fin);
    }

    public void modificar(String nombre, LocalTime inicio, LocalTime fin) {
        this.nombre = nombre;
        horaInicio = inicio;
        horaFinalizacion = fin;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFinalizacion() {
        return horaFinalizacion;
    }

    
    @Override
    public String toString() {
        return idTurno + " - " + nombre + " ("
                + horaInicio + " - " + horaFinalizacion + ")";
    }
}