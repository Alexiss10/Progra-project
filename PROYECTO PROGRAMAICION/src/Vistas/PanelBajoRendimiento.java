/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Vistas;

import clases.gestionProduccion;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ALEXIS
 */
public class PanelBajoRendimiento extends javax.swing.JPanel {

    /**
     * Creates new form PanelBajoRendimiento
     */
    private gestionProduccion gestorProduccion;
    private DefaultTableModel modeloTabla;

    public PanelBajoRendimiento(gestionProduccion gestorProduccion) {

        if (gestorProduccion == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar la gestión de producción."
            );
        }

        initComponents();
        configurarCalendario();

        this.gestorProduccion = gestorProduccion;

        modeloTabla = (DefaultTableModel) tblBajoRendimiento.getModel();

        limpiarCampos();
    }

    private void configurarCalendario() {

        DatePickerSettings configuracion
                = new DatePickerSettings(Locale.forLanguageTag("es-GT"));

        configuracion.setFormatForDatesCommonEra("dd/MM/uuuu");
        configuracion.setAllowKeyboardEditing(false);
        configuracion.setAllowEmptyDates(true);

        dpFecha.setSettings(configuracion);
    }

    private void consultarBajoRendimiento() {

        modeloTabla.setRowCount(0);
        lblResultado.setText("Horas con bajo rendimiento: —");

        try {

            LocalDate fecha = dpFecha.getDate();

            if (fecha == null) {
                throw new IllegalArgumentException(
                        "Debes seleccionar la fecha que deseas consultar."
                );
            }

            String textoMeta = txtMetaPorHora.getText().trim();

            if (textoMeta.isEmpty()) {
                throw new IllegalArgumentException(
                        "Debes ingresar la meta por hora."
                );
            }

            int metaPorHora = Integer.parseInt(textoMeta);

            if (metaPorHora <= 0) {
                throw new IllegalArgumentException(
                        "La meta por hora debe ser mayor a cero."
                );
            }

            if (gestorProduccion.buscarPorFecha(fecha).isEmpty()) {

                lblResultado.setText(
                        "Sin registros para la fecha consultada."
                );

                JOptionPane.showMessageDialog(
                        this,
                        "No hay registros de producción para esa fecha."
                );

                return;
            }

            ArrayList<Integer> horas
                    = gestorProduccion.identificarHorasBajoRendimiento(
                            fecha, metaPorHora
                    );

            for (int hora : horas) {

                int total = gestorProduccion.calcularTotalPorHora(
                        fecha, hora
                );

                int faltante = metaPorHora - total;

                String intervalo = String.format(
                        "%02d:00 - %02d:00",
                        hora,
                        hora + 1
                );

                modeloTabla.addRow(new Object[]{
                    intervalo,
                    total,
                    metaPorHora,
                    faltante
                });
            }

            if (horas.isEmpty()) {

                lblResultado.setText(
                        "Ninguna hora registrada está por debajo de la meta."
                );

            } else {

                lblResultado.setText(
                        "Horas con bajo rendimiento: " + horas.size()
                );
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "La meta debe ser un número entero dentro del rango permitido.",
                    "Meta incorrecta",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "No se pudo consultar",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void limpiarCampos() {

        dpFecha.clear();
        txtMetaPorHora.setText("100");

        modeloTabla.setRowCount(0);
        lblResultado.setText("Horas con bajo rendimiento: —");

        dpFecha.getComponentDateTextField().requestFocusInWindow();
    }

    public void actualizarPanel() {

        limpiarCampos();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMetaPorHora = new javax.swing.JTextField();
        btnConsultar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        lblResultado = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBajoRendimiento = new javax.swing.JTable();
        dpFecha = new com.github.lgooddatepicker.components.DatePicker();

        jLabel1.setText("Fecha:");

        jLabel2.setText("Meta por hora:");

        txtMetaPorHora.setText("100");

        btnConsultar.setText("Consultar");
        btnConsultar.addActionListener(this::btnConsultarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        lblResultado.setText("Horas con bajo rendimiento: ");

        jLabel3.setText("Solo se evalúan horas con registros.");

        tblBajoRendimiento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Hora", "Produccion real", "Meta por hora", "Unidades faltantes"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblBajoRendimiento);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(lblResultado)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(34, 34, 34)
                        .addComponent(txtMetaPorHora, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(dpFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(196, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(dpFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMetaPorHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConsultar)
                    .addComponent(btnLimpiar))
                .addGap(18, 18, 18)
                .addComponent(lblResultado)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        consultarBajoRendimiento();
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnLimpiar;
    private com.github.lgooddatepicker.components.DatePicker dpFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblResultado;
    private javax.swing.JTable tblBajoRendimiento;
    private javax.swing.JTextField txtMetaPorHora;
    // End of variables declaration//GEN-END:variables

}
