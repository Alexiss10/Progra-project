package clases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import Filtex.Turno;
import archivos.ArchivoProduccion;
import java.io.IOException;

/**
 *
 * @author ALEXIS
 */
public class gestionProduccion {

    private ArrayList<Produccion> producciones;
    private int siguienteId = 1;
    private GestionTelas gestionTelas;
    private ArchivoProduccion archivo;
    private boolean cambiosPendientes = false;

    public gestionProduccion(GestionTelas gestionTelas, ArrayList<Turno> turnos) throws IOException {

        if (gestionTelas == null) {
            throw new IllegalArgumentException("Debes proporcionar la gestión de telas.");
        }

        if (turnos == null) {
            throw new IllegalArgumentException("Debes proporcionar la lista de turnos.");
        }

        this.gestionTelas = gestionTelas;
        producciones = new ArrayList<>();
        archivo = new ArchivoProduccion();

        cargarProducciones(turnos);
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
        cambiosPendientes = true;
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
        cambiosPendientes = true;
    }

    public void eliminar(int idProduccion) {

        Produccion produccion = buscarPorId(idProduccion);

        if (produccion == null) {
            throw new IllegalArgumentException("El registro de "
                    + "producción que deseas eliminar no existe.");
        }

        producciones.remove(produccion);
        cambiosPendientes = true;
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

    public void guardarCambios() throws IOException {

        archivo.guardar(producciones);
        cambiosPendientes = false;
    }

    public boolean hayCambiosPendientes() {

        return cambiosPendientes;
    }


    public boolean tieneProduccionesDeTela(int idTela) {

        for (Produccion produccion : producciones) {

            if (produccion.getTela().getIdTela() == idTela) {
                return true;
            }
        }

        return false;
    }

    public boolean tieneProduccionesDeTurno(int idTurno) {

        for (Produccion produccion : producciones) {

            if (produccion.getTurno().getIdTurno() == idTurno) {
                return true;
            }
        }

        return false;
    }

    private void cargarProducciones(ArrayList<Turno> turnos) throws IOException {

        ArrayList<Produccion> produccionesCargadas = archivo.cargar(gestionTelas, turnos);

        int mayorId = 0;

        for (Produccion produccion : produccionesCargadas) {

            if (produccion.getIdProduccion() > mayorId) {
                mayorId = produccion.getIdProduccion();
            }
        }

        producciones = produccionesCargadas;
        siguienteId = mayorId + 1;
        cambiosPendientes = false;
    }

}
