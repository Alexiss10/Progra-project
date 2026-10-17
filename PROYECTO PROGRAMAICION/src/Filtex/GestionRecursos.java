package Filtex;

import java.io.IOException;
import java.util.ArrayList;

public class GestionRecursos {

    private final ArrayList<Recurso> recursos = new ArrayList<>();
    private final ArchivoRecursos archivo = new ArchivoRecursos();
    private int siguienteId = 1;
    private boolean cambiosPendientes = false;

    public GestionRecursos() throws IOException {

        GuardadoRecursos.recuperarPendiente();

        int mayorId = 0;

        for (Recurso r : archivo.cargar()) {
            recursos.add(r);
            mayorId = Math.max(mayorId, r.getIdRecurso());
        }

        siguienteId = mayorId + 1;
    }

    public Recurso registrar(String nombre, String tipo, String unidadMedida,
            double cantidadDisponible, double cantidadMinima) {
        Recurso nuevo = new Recurso(siguienteId, nombre, tipo, unidadMedida,
                cantidadDisponible, cantidadMinima);

        for (Recurso r : recursos) {
            if (r.getNombre().equalsIgnoreCase(nuevo.getNombre())) {
                throw new IllegalArgumentException("Ya existe un recurso con ese nombre.");
            }
        }

        recursos.add(nuevo);
        siguienteId++;
        cambiosPendientes = true;
        return nuevo;
    }

    public Recurso buscarPorId(int idRecurso) {
        for (Recurso r : recursos) {
            if (r.getIdRecurso() == idRecurso) {
                return r;
            }
        }
        return null;
    }

    public ArrayList<Recurso> listar() {
        return new ArrayList<>(recursos);
    }

    /**
     * Lo usa GestionUsoRecursos cuando descuenta existencia.
     */
    void marcarCambios() {
        cambiosPendientes = true;
    }

    public void guardarCambios() throws IOException {
        archivo.guardar(recursos);
        cambiosPendientes = false;
    }

    public boolean hayCambiosPendientes() {
        return cambiosPendientes;
    }

    void confirmarGuardado() {
        cambiosPendientes = false;
    }
}
