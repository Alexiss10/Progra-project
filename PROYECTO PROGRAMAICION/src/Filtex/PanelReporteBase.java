package Filtex;
 
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
 
/** Elementos comunes de los paneles de reportes: período, metas y exportación. */
public abstract class PanelReporteBase extends PanelBase {
 
    protected final GestionMetas metas;
    protected final DatePicker dpDesde = crearCalendario();
    protected final DatePicker dpHasta = crearCalendario();
    protected final JLabel lblMetas = new JLabel(" ");
    protected final JLabel lblResumen = new JLabel(" ");
 
    protected PanelReporteBase(GestionMetas metas) {
        this.metas = metas;
    }
 
    /** Mismo calendario que usan los demás paneles (dd/MM/uuuu, solo por selección). */
    private static DatePicker crearCalendario() {
        DatePickerSettings configuracion
                = new DatePickerSettings(Locale.forLanguageTag("es-GT"));
 
        configuracion.setFormatForDatesCommonEra("dd/MM/uuuu");
        configuracion.setAllowKeyboardEditing(false);
        configuracion.setAllowEmptyDates(true);
 
        DatePicker calendario = new DatePicker(configuracion);
        calendario.setDate(LocalDate.now());
        return calendario;
    }
 
    protected JPanel barraPeriodo(JButton... botones) {
        JPanel barra = new JPanel();
        barra.add(new JLabel("Desde:"));
        barra.add(dpDesde);
        barra.add(new JLabel("Hasta:"));
        barra.add(dpHasta);
        for (JButton b : botones) {
            barra.add(b);
        }
        return barra;
    }
 
    protected JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> ejecutar(accion));
        return b;
    }
 
    protected LocalDate[] leerPeriodo() {
        LocalDate desde = dpDesde.getDate();
        LocalDate hasta = dpHasta.getDate();
 
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Selecciona la fecha inicial y la fecha final del período.");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial.");
        }
        return new LocalDate[]{desde, hasta};
    }
 
    protected JTable tablaCumplimiento(String tituloDetalle) {
        if (tituloDetalle == null) {
            return crearTabla("Fecha", "Producción", "Meta", "Diferencia", "Cumplimiento", "Estado");
        }
        return crearTabla("Fecha", tituloDetalle, "Producción", "Meta", "Diferencia",
                "Cumplimiento", "Estado");
    }
 
    protected void llenarCumplimiento(JTable tabla, List<FilaReporte> filas, boolean conDetalle) {
        DefaultTableModel modelo = limpiar(tabla);
        for (FilaReporte f : filas) {
            ArrayList<Object> fila = new ArrayList<>();
            fila.add(f.getFecha());
            if (conDetalle) {
                fila.add(f.getDetalle());
            }
            fila.add(f.getReal());
            fila.add(f.getMeta());
            fila.add(String.format("%+d", f.getDiferencia()));
            fila.add(String.format("%.1f %%", f.getPorcentaje()));
            fila.add(f.getEstado());
            modelo.addRow(fila.toArray());
        }
    }
 
    protected void mostrarMetas() {
        lblMetas.setText(metas.estanDefinidas()
                ? "Metas: diaria " + metas.getMetaDiaria() + " | por hora " + metas.getMetaPorHora()
                + " | meta de turno = meta por hora x duración del turno"
                : "Metas sin definir: usa el botón Metas.");
    }
 
    protected void definirMetas() {
        String[] v = formulario("Metas de producción",
                new String[]{"Meta diaria (unidades)", "Meta por hora (unidades)"},
                metas.getMetaDiaria() > 0 ? String.valueOf(metas.getMetaDiaria()) : "",
                metas.getMetaPorHora() > 0 ? String.valueOf(metas.getMetaPorHora()) : "");
        if (v == null) {
            return;
        }
        metas.definir(entero("meta diaria", v[0]), entero("meta por hora", v[1]));
        mostrarMetas();
    }
 
    protected int entero(String campo, String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser un número entero.");
        }
    }
 
    /** Exporta la tabla a CSV (separado por punto y coma, UTF-8, abre bien en Excel). */
    protected void exportar(JTable tabla) {
        if (tabla.getRowCount() == 0) {
            throw new IllegalArgumentException("No hay datos para exportar. Genera primero el reporte.");
        }
 
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new File("reporte.csv"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (archivo.exists() && !confirmar("El archivo ya existe. ¿Reemplazarlo?")) {
            return;
        }
 
        TableModel modelo = tabla.getModel();
        ArrayList<String> lineas = new ArrayList<>();
 
        StringBuilder cabecera = new StringBuilder("\uFEFF");
        for (int c = 0; c < modelo.getColumnCount(); c++) {
            cabecera.append(c > 0 ? ";" : "").append(modelo.getColumnName(c));
        }
        lineas.add(cabecera.toString());
 
        for (int f = 0; f < modelo.getRowCount(); f++) {
            StringBuilder fila = new StringBuilder();
            for (int c = 0; c < modelo.getColumnCount(); c++) {
                fila.append(c > 0 ? ";" : "")
                        .append(String.valueOf(modelo.getValueAt(f, c)).replace(';', ','));
            }
            lineas.add(fila.toString());
        }
 
        try {
            Files.write(archivo.toPath(), lineas, StandardCharsets.UTF_8);
            mensaje("Reporte exportado en:\n" + archivo.getAbsolutePath());
        } catch (IOException ex) {
            mensaje("No se pudo exportar el reporte: " + ex.getMessage());
        }
    }
}
 