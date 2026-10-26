package Filtex;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PanelDisponibilidadRecursos extends PanelBase {

    private static final int COL_ESTADO = 6;
    private static final String BAJO = "Bajo el mínimo";

    private final GestionRecursos gestion;
    private final JTable tabla = crearTabla("ID", "Nombre", "Tipo", "Unidad",
            "Disponible", "Mínima", "Estado");
    private final JLabel lblResumen = new JLabel(" ");

    public PanelDisponibilidadRecursos(GestionRecursos gestion) {
        this.gestion = gestion;

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor,
                    boolean seleccionado, boolean foco, int fila, int columna) {
                Component c = super.getTableCellRendererComponent(t, valor, seleccionado, foco, fila, columna);
                boolean bajo = BAJO.equals(t.getValueAt(fila, COL_ESTADO));
                if (!seleccionado) {
                    c.setForeground(bajo ? new Color(180, 0, 0) : t.getForeground());
                }
                return c;
            }
        });

        add(barraBotones(new String[]{"Ver todos", "Solo bajo el mínimo"},
                this::actualizar, () -> mostrar(true)), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblResumen, BorderLayout.SOUTH);
        actualizar();
    }

    private void mostrar(boolean soloBajoMinimo) {
        DefaultTableModel modelo = limpiar(tabla);
        int bajo = 0;
        int total = 0;

        for (Recurso r : gestion.listar()) {
            total++;
            if (r.estaBajoMinimo()) {
                bajo++;
            } else if (soloBajoMinimo) {
                continue;
            }
            modelo.addRow(new Object[]{r.getIdRecurso(), r.getNombre(), r.getTipo(),
                r.getUnidadMedida(), Recurso.formato(r.getCantidadDisponible()),
                Recurso.formato(r.getCantidadMinima()), r.estaBajoMinimo() ? BAJO : "Suficiente"});
        }

        lblResumen.setText("Recursos registrados: " + total + " | Bajo el mínimo: " + bajo);

        if (soloBajoMinimo && bajo == 0) {
            mensaje("Ningún recurso está por debajo de la cantidad mínima.");
        }
    }

    @Override
    public void actualizar() {
        mostrar(false);
    }
}
