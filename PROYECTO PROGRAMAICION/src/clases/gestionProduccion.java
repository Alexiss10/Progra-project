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
        this.gestionTelas.vincularProduccion(this);
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

    public ArrayList<Produccion> buscarPorFecha(LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException("Debes indicar la fecha que deseas consultar.");
        }

        ArrayList<Produccion> resultados = new ArrayList<>();

        for (Produccion produccion : producciones) {

            if (produccion.getFecha().equals(fecha)) {
                resultados.add(produccion);
            }
        }

        return resultados;
    }

    public ArrayList<Produccion> buscarPorFechaYTurno(LocalDate fecha, int idTurno) {

        if (fecha == null) {
            throw new IllegalArgumentException("Debes indicar la fecha que deseas consultar.");
        }

        if (idTurno <= 0) {
            throw new IllegalArgumentException("Debes seleccionar un turno válido.");
        }

        ArrayList<Produccion> resultados = new ArrayList<>();

        for (Produccion produccion : producciones) {

            if (produccion.getFecha().equals(fecha) && produccion.getTurno().getIdTurno() == idTurno) {
                resultados.add(produccion);
            }
        }

        return resultados;
    }

    public int calcularTotalDiario(LocalDate fecha) {

        ArrayList<Produccion> resultados = buscarPorFecha(fecha);

        int total = 0;

        for (Produccion produccion : resultados) {

            total += produccion.getCantProducida();
        }

        return total;
    }

    private void validarComparacion(LocalDate fecha, int metaDiaria) {

        if (metaDiaria <= 0) {
            throw new IllegalArgumentException("La meta diaria debe ser mayor a cero.");
        }

        if (buscarPorFecha(fecha).isEmpty()) {
            throw new IllegalArgumentException("No hay registros de producción para la fecha indicada.");
        }
    }

    public int calcularDiferenciaDiaria(LocalDate fecha, int metaDiaria) {

        validarComparacion(fecha, metaDiaria);

        int totalProducido = calcularTotalDiario(fecha);

        return totalProducido - metaDiaria;
    }

    public double calcularPorcentajeCumplimiento(LocalDate fecha, int metaDiaria) {

        validarComparacion(fecha, metaDiaria);

        int totalProducido = calcularTotalDiario(fecha);

        return totalProducido * 100.0 / metaDiaria;
    }

    public String obtenerEstadoProduccion(LocalDate fecha, int metaDiaria) {

        int diferencia = calcularDiferenciaDiaria(fecha, metaDiaria);

        if (diferencia < 0) {
            return "Producción por debajo de la meta.";
        } else if (diferencia == 0) {
            return "Meta alcanzada.";
        } else {
            return "Meta superada.";
        }
    }

    public int calcularTotalPorHora(LocalDate fecha, int hora) {

        if (hora < 0 || hora > 23) {
            throw new IllegalArgumentException("La hora debe estar entre 0 y 23.");
        }

        ArrayList<Produccion> resultados = buscarPorFechaYHora(fecha, LocalTime.of(hora, 0));

        int total = 0;

        for (Produccion produccion : resultados) {

            total += produccion.getCantProducida();
        }

        return total;
    }

    public ArrayList<Integer> identificarHorasBajoRendimiento(LocalDate fecha, int metaPorHora) {

        if (metaPorHora <= 0) {
            throw new IllegalArgumentException("La meta por hora debe ser mayor a cero.");
        }

        ArrayList<Produccion> registros = buscarPorFecha(fecha);

        if (registros.isEmpty()) {
            throw new IllegalArgumentException("No hay registros de producción para la fecha indicada.");
        }

        int[] totalesPorHora = new int[24];
        boolean[] horasConRegistro = new boolean[24];

        for (Produccion produccion : registros) {

            int hora = produccion.getHora().getHour();

            totalesPorHora[hora] += produccion.getCantProducida();
            horasConRegistro[hora] = true;
        }

        ArrayList<Integer> horasBajoRendimiento = new ArrayList<>();

        for (int hora = 0; hora < 24; hora++) {

            if (horasConRegistro[hora] && totalesPorHora[hora] < metaPorHora) {
                horasBajoRendimiento.add(hora);
            }
        }

        return horasBajoRendimiento;
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
