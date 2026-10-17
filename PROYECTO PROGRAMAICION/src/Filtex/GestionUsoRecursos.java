package Filtex;

import clases.Produccion;
import clases.gestionProduccion;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GestionUsoRecursos {

    private final ArrayList<UsoRecurso> usos;
    private final GestionRecursos gestionRecursos;
    private final gestionProduccion produccion;
    private final ArchivoUsoRecursos archivo = new ArchivoUsoRecursos();
    private int siguienteId = 1;
    private boolean cambiosPendientes = false;

    public GestionUsoRecursos(GestionRecursos gestionRecursos,
            gestionProduccion produccion) throws IOException {
        if (gestionRecursos == null || produccion == null) {
            throw new IllegalArgumentException("Debes proporcionar la gestión de recursos y de producción.");
        }
        this.gestionRecursos = gestionRecursos;
        this.produccion = produccion;

        // Se carga el historial sin tocar las existencias (ya vienen descontadas).
        usos = archivo.cargar(gestionRecursos, produccion);
        int mayorId = 0;
        for (UsoRecurso u : usos) {
            mayorId = Math.max(mayorId, u.getIdUso());
        }
        siguienteId = mayorId + 1;
    }

    public UsoRecurso registrar(int idRecurso, int idProduccion,
            LocalDate fecha, double cantidad) {
        Recurso recurso = gestionRecursos.buscarPorId(idRecurso);
        Produccion prod = produccion.buscarPorId(idProduccion);

        if (recurso == null) {
            throw new IllegalArgumentException("El recurso seleccionado no existe.");
        }
        if (prod == null) {
            throw new IllegalArgumentException("El registro de producción seleccionado no existe.");
        }

        // Primero se validan los datos; el descuento es lo último que puede fallar.
        UsoRecurso uso = new UsoRecurso(siguienteId, recurso, prod, fecha, cantidad);
        recurso.descontar(cantidad);

        usos.add(uso);
        siguienteId++;
        gestionRecursos.marcarCambios();
        cambiosPendientes = true;
        return uso;
    }

    public ArrayList<UsoRecurso> listar() {
        return new ArrayList<>(usos);
    }

    /**
     * F8: lo usa Alexis para impedir borrar producciones con usos asociados.
     */
    public boolean tieneUsosDeProduccion(int idProduccion) {
        for (UsoRecurso u : usos) {
            if (u.getProduccion().getIdProduccion() == idProduccion) {
                return true;
            }
        }
        return false;
    }

    /**
     * Guarda existencias e historial juntos para que no queden inconsistentes.
     */
    public void guardarCambios() throws IOException {

        GuardadoRecursos.guardar(gestionRecursos.listar(), usos);

        gestionRecursos.confirmarGuardado();
        cambiosPendientes = false;
    }

    public boolean hayCambiosPendientes() {
        return cambiosPendientes || gestionRecursos.hayCambiosPendientes();
    }
}
