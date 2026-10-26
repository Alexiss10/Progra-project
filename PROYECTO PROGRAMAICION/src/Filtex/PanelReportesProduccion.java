package Filtex;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

public class PanelReportesProduccion extends PanelReporteBase {

    private final GestionReportes reportes;
    private final JTable tablaDiaria = tablaCumplimiento(null);
    private final JTable tablaHora = tablaCumplimiento("Hora");
    private final JTable tablaTurno = tablaCumplimiento("Turno");
    private final JTabbedPane pestanias = new JTabbedPane();

    public PanelReportesProduccion(GestionReportes reportes, GestionMetas metas) {
        super(metas);
        this.reportes = reportes;

        pestanias.addTab("Diario", new JScrollPane(tablaDiaria));
        pestanias.addTab("Por hora", new JScrollPane(tablaHora));
        pestanias.addTab("Por turno", new JScrollPane(tablaTurno));

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

        ArrayList<FilaReporte> diario = reportes.reporteDiario(p[0], p[1]);
        if (diario.isEmpty()) {
            limpiarTablas();
            lblResumen.setText("Sin registros en el período.");
            mensaje("No existen registros de producción entre " + p[0] + " y " + p[1] + ".");
            return;
        }

        llenarCumplimiento(tablaDiaria, diario, false);
        llenarCumplimiento(tablaHora, reportes.reportePorHora(p[0], p[1]), true);
        llenarCumplimiento(tablaTurno, reportes.reportePorTurno(p[0], p[1]), true);

        int total = 0;
        int cumplidos = 0;
        for (FilaReporte f : diario) {
            total += f.getReal();
            if (f.cumpleMeta()) {
                cumplidos++;
            }
        }
        lblResumen.setText("Total producido: " + total + " | Días con registros: " + diario.size()
                + " | Meta diaria alcanzada o superada: " + cumplidos + " de " + diario.size());
    }

    private void exportarActual() {
        JTable[] tablas = {tablaDiaria, tablaHora, tablaTurno};
        exportar(tablas[pestanias.getSelectedIndex()]);
    }

    private void limpiarTablas() {
        limpiar(tablaDiaria);
        limpiar(tablaHora);
        limpiar(tablaTurno);
    }

    @Override
    public void actualizar() {
        mostrarMetas();
        limpiarTablas();
        lblResumen.setText(" ");
    }
}
