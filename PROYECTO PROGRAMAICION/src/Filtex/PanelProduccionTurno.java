package Filtex;

import clases.Produccion;
import clases.gestionProduccion;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Consulta de producción por turno y fecha.
 */
public class PanelProduccionTurno extends PanelBase {

    private final GestionTurnos gestionTurnos;
    private final gestionProduccion produccion;

    private final JComboBox<Turno> cmbTurno = new JComboBox<>();
    private final DatePicker dpFecha = new DatePicker();

    private final JTable tabla = crearTabla(
            "Hora", "Tipo de tela", "Cantidad"
    );

    private final JLabel lblTotal
            = new JLabel("Total del turno: —");

    public PanelProduccionTurno(GestionTurnos gestionTurnos,
            gestionProduccion produccion) {

        if (gestionTurnos == null || produccion == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar la gestión de turnos y producción."
            );
        }

        this.gestionTurnos = gestionTurnos;
        this.produccion = produccion;

        configurarCalendario();

        JButton btnConsultar = new JButton("Consultar");

        btnConsultar.addActionListener(
                e -> ejecutar(this::consultar)
        );

        JPanel filtros = new JPanel();

        filtros.add(new JLabel("Turno:"));
        filtros.add(cmbTurno);
        filtros.add(new JLabel("Fecha:"));
        filtros.add(dpFecha);
        filtros.add(btnConsultar);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);

        actualizar();
    }

    private void configurarCalendario() {

        DatePickerSettings configuracion
                = new DatePickerSettings(Locale.forLanguageTag("es-GT"));

        configuracion.setFormatForDatesCommonEra("dd/MM/uuuu");
        configuracion.setAllowKeyboardEditing(false);
        configuracion.setAllowEmptyDates(true);

        dpFecha.setSettings(configuracion);
        dpFecha.setDate(LocalDate.now());
    }

    private void consultar() {

        DefaultTableModel modelo = limpiar(tabla);
        lblTotal.setText("Total del turno: —");

        Turno turno = (Turno) cmbTurno.getSelectedItem();

        if (turno == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un turno. Si no hay turnos, registra uno primero."
            );
        }

        LocalDate fecha = dpFecha.getDate();

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar la fecha que deseas consultar."
            );
        }

        ArrayList<Produccion> resultados = new ArrayList<>(
                produccion.buscarPorFechaYTurno(
                        fecha, turno.getIdTurno()
                )
        );

        resultados.sort(
                Comparator.comparing(Produccion::getHora)
        );

        if (resultados.isEmpty()) {

            lblTotal.setText("Total del turno: sin registros");

            String fechaFormateada = fecha.format(
                    DateTimeFormatter.ofPattern("dd/MM/uuuu")
            );

            mensaje(
                    "No existen registros de producción para el turno \""
                    + turno.getNombre()
                    + "\" en la fecha "
                    + fechaFormateada
                    + "."
            );

            return;
        }

        int total = 0;

        for (Produccion registro : resultados) {

            modelo.addRow(new Object[]{
                registro.getHora(),
                registro.getTela().getNombre(),
                registro.getCantProducida()
            });

            total += registro.getCantProducida();
        }

        lblTotal.setText(
                "Total del turno: " + total + " unidades"
        );
    }

    @Override
    public void actualizar() {

        Turno anterior = (Turno) cmbTurno.getSelectedItem();

        DefaultComboBoxModel<Turno> modelo
                = new DefaultComboBoxModel<>();

        Turno seleccionar = null;

        for (Turno turno : gestionTurnos.listar()) {

            modelo.addElement(turno);

            if (anterior != null
                    && turno.getIdTurno() == anterior.getIdTurno()) {

                seleccionar = turno;
            }
        }

        cmbTurno.setModel(modelo);

        if (seleccionar != null) {
            cmbTurno.setSelectedItem(seleccionar);
        }

        limpiar(tabla);
        lblTotal.setText("Total del turno: —");
    }
}
