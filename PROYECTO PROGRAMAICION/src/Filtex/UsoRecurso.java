package Filtex;

import clases.Produccion;
import java.time.LocalDate;

public class UsoRecurso {

    private final int idUso;
    private final Recurso recurso;
    private final Produccion produccion;
    private final LocalDate fecha;
    private final double cantidad;

    public UsoRecurso(int idUso, Recurso recurso, Produccion produccion,
            LocalDate fecha, double cantidad) {
        if (idUso <= 0) {
            throw new IllegalArgumentException("El ID del uso debe ser mayor a cero.");
        }
        if (recurso == null) {
            throw new IllegalArgumentException("Debes seleccionar un recurso.");
        }
        if (produccion == null) {
            throw new IllegalArgumentException("Debes seleccionar un registro de producción.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("Debes indicar la fecha de utilización.");
        }
        if (Double.isNaN(cantidad) || Double.isInfinite(cantidad) || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad utilizada debe ser mayor que cero.");
        }
        this.idUso = idUso;
        this.recurso = recurso;
        this.produccion = produccion;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public int getIdUso() {
        return idUso;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public Produccion getProduccion() {
        return produccion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getCantidad() {
        return cantidad;
    }
}

