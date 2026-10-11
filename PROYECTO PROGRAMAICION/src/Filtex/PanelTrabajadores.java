package Filtex;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PanelTrabajadores extends PanelBase {

    private final GestionTrabajadores gestion;
    private final JTable tabla = crearTabla("ID", "Nombre", "Identificacion", "Puesto", "Area");

    public PanelTrabajadores(GestionTrabajadores gestion) {
        this.gestion = gestion;

        add(barraBotones(new String[]{"Registrar", "Modificar", "Eliminar", "Consultar todos"},
                () -> formularioTrabajador(false), () -> formularioTrabajador(true),
                this::eliminar, this::actualizar), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizar();
    }

    private void formularioTrabajador(boolean editar) {
        Trabajador t = editar ? gestion.buscarPorId(idSeleccionado(tabla)) : null;

        String[] valores = editar
                ? new String[]{t.getNombre(), t.getIdentificacion(), t.getPuestoTrabajo(), t.getAreaAsignada()}
                : new String[]{"", "", "", ""};

        String[] d = formulario("Trabajador",
                new String[]{"Nombre", "Identificacion", "Puesto", "Area"}, valores);
        if (d == null) {
            return;
        }

        if (editar) {
            gestion.modificar(t.getIdTrabajador(), d[0], d[1], d[2], d[3]);
        } else {
            gestion.registrar(d[0], d[1], d[2], d[3]);
        }
        actualizar();
    }

    private void eliminar() {
        int id = idSeleccionado(tabla);
        if (!confirmar("¿Eliminar el trabajador y sus asignaciones?")) {
            return;
        }
        gestion.eliminar(id);
        actualizar();
    }

    @Override
    public void actualizar() {
        DefaultTableModel modelo = limpiar(tabla);
        for (Trabajador t : gestion.listar()) {
            modelo.addRow(new Object[]{t.getIdTrabajador(), t.getNombre(),
                t.getIdentificacion(), t.getPuestoTrabajo(), t.getAreaAsignada()});
        }
    }
}
