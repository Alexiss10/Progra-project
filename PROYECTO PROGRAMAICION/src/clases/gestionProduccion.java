package clases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import Filtex.Turno;

/**
 *
 * @author ALEXIS
 */
public class gestionProduccion {

    private ArrayList<Produccion> producciones;
    private int siguienteId = 1;
    private GestionTelas gestionTelas;

    public gestionProduccion(GestionTelas gestionTelas) {

        if (gestionTelas == null) {
            throw new IllegalArgumentException("Debes proporcionar "
                    + "la gestión de telas.");
        }
        this.gestionTelas = gestionTelas;
        producciones = new ArrayList<>();
    }

    public Produccion buscarPorId(int idProduccion) {

        for (Produccion produccion : producciones) {
            if (produccion.getIdProduccion() == idProduccion) {
                return produccion;
            }
        }

        return null;
    }

    public void registrar(LocalDate fecha, int cantidadProducida,
            LocalTime hora, int idTela, Turno turno) {

        Tela tela = gestionTelas.buscarTelaPorId(idTela);

        if (tela == null) {
            throw new IllegalArgumentException("La tela indicada no "
                    + "está registrada.");
        }

        Produccion nuevaProduccion = new Produccion(siguienteId, fecha,
                cantidadProducida, hora, tela, turno);

        producciones.add(nuevaProduccion);
        siguienteId++;
    }

    public void modificar(int idProduccion, LocalDate fecha,
            int cantidadProducida, LocalTime hora, int idTela, Turno turno) {

        Produccion produccion = buscarPorId(idProduccion);

        if (produccion == null) {
            throw new IllegalArgumentException("El registro de "
                    + "producción no existe.");
        }

        Tela tela = gestionTelas.buscarTelaPorId(idTela);

        if (tela == null) {
            throw new IllegalArgumentException("La tela indicada no "
                    + "está registrada.");
        }
        produccion.actualizarDatos(fecha, cantidadProducida, hora, tela, turno);
    }

    public void eliminar(int idProduccion) {

        Produccion produccion = buscarPorId(idProduccion);

        if (produccion == null) {
            throw new IllegalArgumentException("El registro de "
                    + "producción que deseas eliminar no existe.");
        }

        producciones.remove(produccion);
    }

    public ArrayList<Produccion> listar() {
        return new ArrayList<>(producciones);
    }

    
    public ArrayList<Produccion> buscarPorFechaYHora(LocalDate fecha, 
            LocalTime hora) {

        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("Debes indicar la fecha"
                    + " y la hora para buscar.");
        }

        ArrayList<Produccion> resultados = new ArrayList<>();

        for (Produccion produccion : producciones) {

            if (produccion.getFecha().equals(fecha)
                    && produccion.getHora().getHour() == hora.getHour()) {
                resultados.add(produccion);
            }
        }

        return resultados;
    }
}
