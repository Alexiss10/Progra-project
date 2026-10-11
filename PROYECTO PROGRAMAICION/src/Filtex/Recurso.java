package Filtex;

import java.math.BigDecimal;

public class Recurso {

    private final int idRecurso;
    private String nombre;
    private String tipo;
    private String unidadMedida;
    private double cantidadDisponible;
    private double cantidadMinima;

    public Recurso(int idRecurso, String nombre, String tipo, String unidadMedida,
            double cantidadDisponible, double cantidadMinima) {
        if (idRecurso <= 0) {
            throw new IllegalArgumentException("El ID del recurso debe ser mayor a cero.");
        }
        this.idRecurso = idRecurso;
        this.nombre = UtilArchivo.texto(nombre, "nombre");
        this.tipo = UtilArchivo.texto(tipo, "tipo");
        this.unidadMedida = UtilArchivo.texto(unidadMedida, "unidad de medida");
        this.cantidadDisponible = validarCantidad(cantidadDisponible, "cantidad disponible");
        this.cantidadMinima = validarCantidad(cantidadMinima, "cantidad mínima");
    }

    private static double validarCantidad(double valor, String campo) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new IllegalArgumentException("La " + campo + " no puede ser negativa.");
        }
        return valor;
    }

    /** Descuenta existencia. Solo se debe llamar al registrar un uso nuevo. */
    public void descontar(double cantidad) {
        if (Double.isNaN(cantidad) || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad utilizada debe ser mayor que cero.");
        }
        if (cantidad > cantidadDisponible) {
            throw new IllegalArgumentException("La cantidad supera la existencia disponible ("
                    + formato(cantidadDisponible) + " " + unidadMedida + ").");
        }
        double restante = cantidadDisponible - cantidad;
        cantidadDisponible = Math.round(restante * 10000.0) / 10000.0;
    }

    public boolean estaBajoMinimo() {
        return cantidadDisponible < cantidadMinima;
    }

    public static String formato(double valor) {
        return BigDecimal.valueOf(valor).stripTrailingZeros().toPlainString();
    }

    public int getIdRecurso() {
        return idRecurso;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public double getCantidadDisponible() {
        return cantidadDisponible;
    }

    public double getCantidadMinima() {
        return cantidadMinima;
    }

    @Override
    public String toString() {
        return idRecurso + " - " + nombre + " (" + formato(cantidadDisponible)
                + " " + unidadMedida + ")";
    }
}
