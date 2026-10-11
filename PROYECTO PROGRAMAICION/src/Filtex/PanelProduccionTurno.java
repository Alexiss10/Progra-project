package Filtex;

import clases.Produccion;
import clases.gestionProduccion;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** RF10: consulta de producción por turno y fecha. */
public class PanelProduccionTurno extends PanelBase {

    private final GestionTurnos gestionTurnos;
    private final gestionProduccion produccion;

    private final JComboBox<Turno> cmbTurno = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(LocalDate.now().toString(), 10);
    private final JTable tabla = crearTabla("Hora", "Tipo de tela", "Cantidad");
    private final JLabel lblTotal = new JLabel("Total del turno: -");

    public PanelProduccionTurno(GestionTurnos gestionTurnos, gestionProduccion produccion) {
        this.gestionTurnos = gestionTurnos;
        this.produccion = produccion;

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> ejecutar(this::consultar));

        JPanel filtros = new JPanel();
        filtros.add(new JLabel("Turno:"));
        filtros.add(cmbTurno);
        filtros.add(new JLabel("Fecha (yyyy-MM-dd):"));
        filtros.add(txtFecha);
        filtros.add(btnConsultar);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);
        actualizar();
    }

    private void consultar() {
        Turno turno = (Turno) cmbTurno.getSelectedItem();
        if (turno == null) {
            throw new IllegalArgumentException("Registra primero un turno.");
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha incorrecta. Usa el formato yyyy-MM-dd. Ejemplo: 2026-10-11.");
        }

        ArrayList<Produccion> resultados = new ArrayList<>(
                produccion.buscarPorFechaYTurno(fecha, turno.getIdTurno()));
        resultados.sort(Comparator.comparing(Produccion::getHora));

        DefaultTableModel modelo = limpiar(tabla);

        if (resultados.isEmpty()) {
            lblTotal.setText("Total del turno: sin registros");
            mensaje("No existen registros de producción para el turno \""
                    + turno.getNombre() + "\" en la fecha " + fecha + ".");
            return;
        }

        int total = 0;
        for (Produccion p : resultados) {
            modelo.addRow(new Object[]{p.getHora(), p.getTela().getNombre(), p.getCantProducida()});
            total += p.getCantProducida();
        }
        lblTotal.setText("Total del turno: " + total);
    }

    @Override
    public void actualizar() {
        Turno anterior = (Turno) cmbTurno.getSelectedItem();
        DefaultComboBoxModel<Turno> modelo = new DefaultComboBoxModel<>();
        Turno seleccionar = null;

        for (Turno t : gestionTurnos.listar()) {
            modelo.addElement(t);
            if (anterior != null && t.getIdTurno() == anterior.getIdTurno()) {
                seleccionar = t;
            }
        }
        cmbTurno.setModel(modelo);
        if (seleccionar != null) {
            cmbTurno.setSelectedItem(seleccionar);
        }

        limpiar(tabla);
        lblTotal.setText("Total del turno: -");
    }
}
