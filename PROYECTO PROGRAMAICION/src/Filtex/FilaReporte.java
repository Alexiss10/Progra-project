package Filtex;

import java.time.LocalDate;

public class FilaReporte {

    private final LocalDate fecha;
    private final String detalle;
    private final int real;
    private final int meta;

    public FilaReporte(LocalDate fecha, String detalle, int real, int meta) {
        this.fecha = fecha;
        this.detalle = detalle;
        this.real = real;
        this.meta = meta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getDetalle() {
        return detalle;
    }

    public int getReal() {
        return real;
    }

    public int getMeta() {
        return meta;
    }

    public int getDiferencia() {
        return real - meta;
    }

    public double getPorcentaje() {
        return meta > 0 ? real * 100.0 / meta : 0;
    }

    public String getEstado() {
        if (real > meta) {
            return "Superada";
        }
        return real == meta ? "Alcanzada" : "No alcanzada";
    }

    public boolean cumpleMeta() {
        return real >= meta;
    }
}

