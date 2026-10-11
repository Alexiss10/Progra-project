package Filtex;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** RF11: registrar y consultar recursos. */
public class PanelRecursos extends PanelBase {

    private final GestionRecursos gestion;
    private final JTable tabla = crearTabla("ID", "Nombre", "Tipo", "Unidad",
            "Disponible", "Mínima");

    public PanelRecursos(GestionRecursos gestion) {
        this.gestion = gestion;

        add(barraBotones(new String[]{"Registrar", "Consultar todos"},
                this::registrar, this::actualizar), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizar();
    }

    private void registrar() {
        String[] d = formulario("Recurso",
                new String[]{"Nombre", "Tipo", "Unidad de medida (kg, litros, unidades)",
                    "Cantidad disponible", "Cantidad mínima"},
                "", "", "", "", "");
        if (d == null) {
            return;
        }

        gestion.registrar(d[0], d[1], d[2],
                decimal("cantidad disponible", d[3]), decimal("cantidad mínima", d[4]));
        actualizar();
    }

    @Override
    public void actualizar() {
        DefaultTableModel modelo = limpiar(tabla);
        for (Recurso r : gestion.listar()) {
            modelo.addRow(new Object[]{r.getIdRecurso(), r.getNombre(), r.getTipo(),
                r.getUnidadMedida(), Recurso.formato(r.getCantidadDisponible()),
                Recurso.formato(r.getCantidadMinima())});
        }
    }
}
