package Filtex;

import clases.Produccion;
import clases.gestionProduccion;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Registrar la utilización de recursos en una producción.
 */
public class PanelUsoRecursos extends PanelBase {

    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private final GestionRecursos gestionRecursos;
    private final gestionProduccion produccion;
    private final GestionUsoRecursos gestionUsos;

    private final JComboBox<Recurso> cmbRecurso = new JComboBox<>();
    private final JComboBox<Produccion> cmbProduccion = new JComboBox<>();

    private final DatePicker dpFecha = new DatePicker();
    private final JTextField txtCantidad = new JTextField();

    private final JTable tabla = crearTabla(
            "ID", "Recurso", "Unidad",
            "Producción", "Fecha", "Cantidad"
    );

    public PanelUsoRecursos(GestionRecursos gestionRecursos,
            gestionProduccion produccion,
            GestionUsoRecursos gestionUsos) {

        if (gestionRecursos == null
                || produccion == null
                || gestionUsos == null) {

            throw new IllegalArgumentException(
                    "Debes proporcionar la gestión de recursos, producción y usos."
            );
        }

        this.gestionRecursos = gestionRecursos;
        this.produccion = produccion;
        this.gestionUsos = gestionUsos;

        configurarCalendario();

        cmbProduccion.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean foco) {

                super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, foco
                );

                if (valor instanceof Produccion) {
                    setText(describir((Produccion) valor));
                }

                return this;
            }
        });

        JButton btnRegistrar = new JButton("Registrar uso");

        btnRegistrar.addActionListener(
                e -> ejecutar(this::registrar)
        );

        JPanel formulario = new JPanel(
                new GridLayout(0, 2, 5, 5)
        );

        formulario.add(new JLabel("Recurso:"));
        formulario.add(cmbRecurso);

        formulario.add(new JLabel("Producción:"));
        formulario.add(cmbProduccion);

        formulario.add(new JLabel("Fecha:"));
        formulario.add(dpFecha);

        formulario.add(new JLabel("Cantidad utilizada:"));
        formulario.add(txtCantidad);

        formulario.add(new JLabel());
        formulario.add(btnRegistrar);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

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

    private static String describir(Produccion registro) {

        return "#" + registro.getIdProduccion()
                + " | " + registro.getFecha().format(FORMATO_FECHA)
                + " " + registro.getHora()
                + " | " + registro.getTela().getNombre()
                + " | " + registro.getCantProducida();
    }

    private void registrar() {

        Recurso recurso = (Recurso) cmbRecurso.getSelectedItem();

        Produccion registro
                = (Produccion) cmbProduccion.getSelectedItem();

        if (recurso == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un recurso. Si no hay recursos, registra uno primero."
            );
        }

        if (registro == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un registro de producción."
            );
        }

        LocalDate fecha = dpFecha.getDate();

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar la fecha de utilización."
            );
        }

        String textoCantidad = txtCantidad.getText().trim();

        if (textoCantidad.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debes ingresar la cantidad utilizada."
            );
        }

        double cantidad = decimal(
                "cantidad utilizada", textoCantidad
        );

        gestionUsos.registrar(
                recurso.getIdRecurso(),
                registro.getIdProduccion(),
                fecha,
                cantidad
        );

        txtCantidad.setText("");
        actualizar();

        mensaje(
                "Uso registrado. Presiona Guardar todo para conservar los cambios."
        );
    }

    @Override
    public void actualizar() {

        Recurso recursoAnterior
                = (Recurso) cmbRecurso.getSelectedItem();

        Produccion produccionAnterior
                = (Produccion) cmbProduccion.getSelectedItem();

        DefaultComboBoxModel<Recurso> modeloRecursos
                = new DefaultComboBoxModel<>();

        Recurso recursoSeleccionado = null;

        for (Recurso recurso : gestionRecursos.listar()) {

            modeloRecursos.addElement(recurso);

            if (recursoAnterior != null
                    && recurso.getIdRecurso()
                    == recursoAnterior.getIdRecurso()) {

                recursoSeleccionado = recurso;
            }
        }

        cmbRecurso.setModel(modeloRecursos);

        if (recursoSeleccionado != null) {
            cmbRecurso.setSelectedItem(recursoSeleccionado);
        }

        DefaultComboBoxModel<Produccion> modeloProducciones
                = new DefaultComboBoxModel<>();

        Produccion produccionSeleccionada = null;

        for (Produccion registro : produccion.listar()) {

            modeloProducciones.addElement(registro);

            if (produccionAnterior != null
                    && registro.getIdProduccion()
                    == produccionAnterior.getIdProduccion()) {

                produccionSeleccionada = registro;
            }
        }

        cmbProduccion.setModel(modeloProducciones);

        if (produccionSeleccionada != null) {
            cmbProduccion.setSelectedItem(produccionSeleccionada);
        }

        DefaultTableModel modelo = limpiar(tabla);

        for (UsoRecurso uso : gestionUsos.listar()) {

            modelo.addRow(new Object[]{
                uso.getIdUso(),
                uso.getRecurso().getNombre(),
                uso.getRecurso().getUnidadMedida(),
                describir(uso.getProduccion()),
                uso.getFecha().format(FORMATO_FECHA),
                Recurso.formato(uso.getCantidad())
            });
        }
    }
}
