package Filtex;

import clases.Produccion;
import clases.gestionProduccion;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** RF12: registrar la utilización de recursos en una producción. */
public class PanelUsoRecursos extends PanelBase {

    private final GestionRecursos gestionRecursos;
    private final gestionProduccion produccion;
    private final GestionUsoRecursos gestionUsos;

    private final JComboBox<Recurso> cmbRecurso = new JComboBox<>();
    private final JComboBox<Produccion> cmbProduccion = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(LocalDate.now().toString());
    private final JTextField txtCantidad = new JTextField();
    private final JTable tabla = crearTabla("ID", "Recurso", "Unidad",
            "Producción", "Fecha", "Cantidad");

    public PanelUsoRecursos(GestionRecursos gestionRecursos, gestionProduccion produccion,
            GestionUsoRecursos gestionUsos) {
        this.gestionRecursos = gestionRecursos;
        this.produccion = produccion;
        this.gestionUsos = gestionUsos;

        cmbProduccion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor,
                    int indice, boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                if (valor instanceof Produccion) {
                    setText(describir((Produccion) valor));
                }
                return this;
            }
        });

        JButton btnRegistrar = new JButton("Registrar uso");
        btnRegistrar.addActionListener(e -> ejecutar(this::registrar));

        JPanel formulario = new JPanel(new GridLayout(0, 2, 5, 5));
        formulario.add(new JLabel("Recurso:"));
        formulario.add(cmbRecurso);
        formulario.add(new JLabel("Producción:"));
        formulario.add(cmbProduccion);
        formulario.add(new JLabel("Fecha (yyyy-MM-dd):"));
        formulario.add(txtFecha);
        formulario.add(new JLabel("Cantidad utilizada:"));
        formulario.add(txtCantidad);
        formulario.add(new JLabel());
        formulario.add(btnRegistrar);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizar();
    }

    private static String describir(Produccion p) {
        return "#" + p.getIdProduccion() + " | " + p.getFecha() + " " + p.getHora()
                + " | " + p.getTela().getNombre() + " | " + p.getCantProducida();
    }

    private void registrar() {
        Recurso recurso = (Recurso) cmbRecurso.getSelectedItem();
        Produccion prod = (Produccion) cmbProduccion.getSelectedItem();

        if (recurso == null) {
            throw new IllegalArgumentException("Registra primero un recurso.");
        }
        if (prod == null) {
            throw new IllegalArgumentException("No hay registros de producción disponibles.");
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha incorrecta. Usa el formato yyyy-MM-dd.");
        }

        double cantidad = decimal("cantidad utilizada", txtCantidad.getText());

        gestionUsos.registrar(recurso.getIdRecurso(), prod.getIdProduccion(), fecha, cantidad);
        txtCantidad.setText("");
        actualizar();
    }

    @Override
    public void actualizar() {
        Recurso recursoAnterior = (Recurso) cmbRecurso.getSelectedItem();
        Produccion produccionAnterior = (Produccion) cmbProduccion.getSelectedItem();

        DefaultComboBoxModel<Recurso> modeloRecursos = new DefaultComboBoxModel<>();
        Recurso recursoSel = null;
        for (Recurso r : gestionRecursos.listar()) {
            modeloRecursos.addElement(r);
            if (recursoAnterior != null && r.getIdRecurso() == recursoAnterior.getIdRecurso()) {
                recursoSel = r;
            }
        }
        cmbRecurso.setModel(modeloRecursos);
        if (recursoSel != null) {
            cmbRecurso.setSelectedItem(recursoSel);
        }

        DefaultComboBoxModel<Produccion> modeloProd = new DefaultComboBoxModel<>();
        Produccion produccionSel = null;
        for (Produccion p : produccion.listar()) {
            modeloProd.addElement(p);
            if (produccionAnterior != null
                    && p.getIdProduccion() == produccionAnterior.getIdProduccion()) {
                produccionSel = p;
            }
        }
        cmbProduccion.setModel(modeloProd);
        if (produccionSel != null) {
            cmbProduccion.setSelectedItem(produccionSel);
        }

        DefaultTableModel modelo = limpiar(tabla);
        for (UsoRecurso u : gestionUsos.listar()) {
            modelo.addRow(new Object[]{u.getIdUso(), u.getRecurso().getNombre(),
                u.getRecurso().getUnidadMedida(), describir(u.getProduccion()),
                u.getFecha(), Recurso.formato(u.getCantidad())});
        }
    }
}
