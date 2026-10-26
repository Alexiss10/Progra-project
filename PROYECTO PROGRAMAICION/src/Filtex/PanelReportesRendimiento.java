package Filtex;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PanelReportesRendimiento extends PanelReporteBase {

    private final GestionReportes reportes;
    private final JTable tablaBajas = tablaCumplimiento("Hora");
    private final JTable tablaVariaciones = crearTabla("Fecha", "Producción",
            "Día anterior con registros", "Producción anterior", "Variación", "Variación %");
    private final JTable tablaMetas = tablaCumplimiento(null);
    private final JTabbedPane pestanias = new JTabbedPane();

    public PanelReportesRendimiento(GestionReportes reportes, GestionMetas metas) {
        super(metas);
        this.reportes = reportes;

        pestanias.addTab("Horas de bajo rendimiento", new JScrollPane(tablaBajas));
        pestanias.addTab("Variaciones de producción", new JScrollPane(tablaVariaciones));
        pestanias.addTab("Cumplimiento de metas", new JScrollPane(tablaMetas));

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(barraPeriodo(boton("Generar reportes", this::generar),
                boton("Metas", this::definirMetas),
                boton("Exportar a CSV", this::exportarActual)), BorderLayout.NORTH);
        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT));
        info.add(lblMetas);
        norte.add(info, BorderLayout.CENTER);

        add(norte, BorderLayout.NORTH);
        add(pestanias, BorderLayout.CENTER);
        add(lblResumen, BorderLayout.SOUTH);
        actualizar();
    }

    private void generar() {
        LocalDate[] p = leerPeriodo();

        ArrayList<FilaReporte> horas = reportes.reportePorHora(p[0], p[1]);
        if (horas.isEmpty()) {
            limpiarTablas();
            lblResumen.setText("Sin registros en el período.");
            mensaje("No existen registros de producción entre " + p[0] + " y " + p[1] + ".");
            return;
        }

        ArrayList<FilaReporte> bajas = reportes.horasBajoRendimiento(p[0], p[1]);
        ArrayList<FilaReporte> diario = reportes.reporteDiario(p[0], p[1]);

        llenarCumplimiento(tablaBajas, bajas, true);
        llenarCumplimiento(tablaMetas, diario, false);

        DefaultTableModel modelo = limpiar(tablaVariaciones);
        for (Object[] fila : reportes.variaciones(p[0], p[1])) {
            modelo.addRow(fila);
        }

        int cumplidos = 0;
        for (FilaReporte f : diario) {
            if (f.cumpleMeta()) {
                cumplidos++;
            }
        }

        lblResumen.setText("Horas con registros: " + horas.size()
                + " | Bajo la meta por hora: " + bajas.size()
                + " | Días con registros: " + diario.size()
                + " | Meta diaria alcanzada o superada: " + cumplidos
                + (diario.size() < 2 ? " | Variaciones: se necesitan al menos dos días con registros" : ""));
    }

    private void exportarActual() {
        JTable[] tablas = {tablaBajas, tablaVariaciones, tablaMetas};
        exportar(tablas[pestanias.getSelectedIndex()]);
    }

    private void limpiarTablas() {
        limpiar(tablaBajas);
        limpiar(tablaVariaciones);
        limpiar(tablaMetas);
    }

    @Override
    public void actualizar() {
        mostrarMetas();
        limpiarTablas();
        lblResumen.setText(" ");
    }
}
