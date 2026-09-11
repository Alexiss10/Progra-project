package clases;

import java.time.LocalDate;
import java.time.LocalTime;
import Filtex.Turno;

/**
 *
 * @author ALEXIS
 */
public class Produccion {

    private int idProduccion;
    private LocalDate fecha;
    private int cantProducida;
    private LocalTime hora;
    private Tela tela;
    private Turno turno;

    public Produccion(int idProduccion, LocalDate fecha, int cantProducida,
            LocalTime hora, Tela tela, Turno turno) {

        if (idProduccion <= 0) {
            throw new IllegalArgumentException("El identificador de "
                    + "la produccion debe ser mayor a cero");
        }
        this.idProduccion = idProduccion;

        actualizarDatos(fecha, cantProducida, hora, tela, turno);
    }

    public void actualizarDatos(LocalDate fecha, int cantProducida,
            LocalTime hora, Tela tela, Turno turno) {
        if (fecha == null) {
            throw new IllegalArgumentException("Debe ingresar la fecha"
                    + " de produccion");
        }

        if (cantProducida < 0) {
            throw new IllegalArgumentException("La cantidad producida"
                    + " no puede ser negativa");
        }
        if (hora == null) {
            throw new IllegalArgumentException("Debe ingresar la hora de"
                    + " produccion");
        }
        if (tela == null) {
            throw new IllegalArgumentException("Debe idicar la tela "
                    + "producida");
        }

        if (turno == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el turno de producción."
            );
        }

        validarHorario(hora, turno);

        this.cantProducida = cantProducida;
        this.fecha = fecha;
        this.hora = hora;
        this.tela = tela;
        this.turno = turno;
    }

    public int getIdProduccion() {
        return idProduccion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getCantProducida() {
        return cantProducida;
    }

    public LocalTime getHora() {
        return hora;
    }

    public Tela getTela() {
        return tela;
    }

    public void setFecha(LocalDate fecha) {
        actualizarDatos(fecha, this.cantProducida, this.hora, this.tela, this.turno);
    }

    public void setCantProducida(int cantProducida) {
        actualizarDatos(this.fecha, cantProducida, this.hora, this.tela, this.turno);
    }

    public void setHora(LocalTime hora) {
        actualizarDatos(this.fecha, this.cantProducida, hora, this.tela, this.turno);
    }

    public void setTela(Tela tela) {
        actualizarDatos(this.fecha, this.cantProducida, this.hora, tela, this.turno);
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        actualizarDatos(
                this.fecha, this.cantProducida, this.hora,
                this.tela, turno
        );
    }

    private void validarHorario(LocalTime hora, Turno turno) {

        LocalTime inicio = turno.getHoraInicio();
        LocalTime fin = turno.getHoraFinalizacion();

        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("El turno debe tener "
                    + "horario de inicio y finalización.");
        }

        if (inicio.equals(fin)) {
            throw new IllegalArgumentException("El inicio y la "
                    + "finalización del turno no pueden ser iguales.");
        }

        boolean horaValida;

        if (inicio.isBefore(fin)) {

            horaValida = !hora.isBefore(inicio) && hora.isBefore(fin);

        } else {

            horaValida = !hora.isBefore(inicio) || hora.isBefore(fin);
        }

        if (!horaValida) {
            throw new IllegalArgumentException("La hora de producción"
                    + " no pertenece al turno seleccionado.");
        }

    }

}
