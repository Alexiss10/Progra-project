package Filtex;

import java.awt.BorderLayout;
import java.time.LocalTime;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PanelTurnos extends PanelBase {

    private final GestionTurnos gestionTurnos;
    private final GestionTrabajadores gestionTrabajadores;
    private final JTable tabla = crearTabla("ID", "Nombre", "Inicio", "Fin");

    public PanelTurnos(GestionTurnos gestionTurnos, GestionTrabajadores gestionTrabajadores) {
        this.gestionTurnos = gestionTurnos;
        this.gestionTrabajadores = gestionTrabajadores;

        add(barraBotones(new String[]{"Registrar", "Modificar", "Eliminar", "Consultar todos"},
                () -> formularioTurno(false), () -> formularioTurno(true),
                this::eliminar, this::actualizar), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizar();
    }

    private void formularioTurno(boolean editar) {
        Turno t = editar ? gestionTurnos.buscarPorId(idSeleccionado(tabla)) : null;

        String[] valores = editar
                ? new String[]{t.getNombre(), t.getHoraInicio().toString(), t.getHoraFinalizacion().toString()}
                : new String[]{"", "08:00", "16:00"};

        String[] d = formulario("Turno",
                new String[]{"Nombre", "Inicio (HH:mm)", "Fin (HH:mm)"}, valores);
        if (d == null) {
            return;
        }

        if (!d[1].matches("[0-9]{2}:[0-9]{2}") || !d[2].matches("[0-9]{2}:[0-9]{2}")) {
            throw new IllegalArgumentException("Usa el formato HH:mm. Ejemplo: 08:30.");
        }

        LocalTime inicio = LocalTime.parse(d[1]);
        LocalTime fin = LocalTime.parse(d[2]);

        if (editar) {
            gestionTurnos.modificar(t.getIdTurno(), d[0], inicio, fin);
        } else {
            gestionTurnos.registrar(d[0], inicio, fin);
        }
        actualizar();
    }

    private void eliminar() {
        int id = idSeleccionado(tabla);
        if (!confirmar("¿Eliminar el turno y sus asignaciones?")) {
            return;
        }
        // Lanza error (y no elimina nada) si el turno tiene producción.
        Turno eliminado = gestionTurnos.eliminar(id);
        gestionTrabajadores.quitarTurnoDeTodos(eliminado);
        actualizar();
    }

    @Override
    public void actualizar() {
        DefaultTableModel modelo = limpiar(tabla);
        for (Turno t : gestionTurnos.listar()) {
            modelo.addRow(new Object[]{t.getIdTurno(), t.getNombre(),
                t.getHoraInicio(), t.getHoraFinalizacion()});
        }
    }
}
