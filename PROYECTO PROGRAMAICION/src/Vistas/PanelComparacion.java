/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Vistas;

import clases.gestionProduccion;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;
import java.util.Locale;
import javax.swing.JOptionPane;

/**
 *
 * @author ALEXIS
 */
public class PanelComparacion extends javax.swing.JPanel {

    /**
     * Creates new form PanelComparacion
     */
    private gestionProduccion gestorProduccion;

    public PanelComparacion(gestionProduccion gestorProduccion) {

        if (gestorProduccion == null) {
            throw new IllegalArgumentException(
                    "Debes proporcionar la gestión de producción."
            );
        }

        initComponents();
        configurarCalendario();

        this.gestorProduccion = gestorProduccion;

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

    private void compararProduccion() {

        limpiarResultados();

        try {

            LocalDate fecha = dpFecha.getDate();

            if (fecha == null) {
                throw new IllegalArgumentException(
                        "Debes seleccionar la fecha que deseas comparar."
                );
            }

            String textoMeta = txtMetaDiaria.getText().trim();

            if (textoMeta.isEmpty()) {
                throw new IllegalArgumentException(
                        "Debes ingresar la meta diaria."
                );
            }

            int metaDiaria = Integer.parseInt(textoMeta);

            if (metaDiaria <= 0) {
                throw new IllegalArgumentException(
                        "La meta diaria debe ser mayor a cero."
                );
            }

            if (gestorProduccion.buscarPorFecha(fecha).isEmpty()) {

                lblEstado.setText(
                        "Estado: sin registros para esa fecha."
                );

                JOptionPane.showMessageDialog(
                        this,
                        "No hay registros de producción para comparar en esa fecha."
                );

                return;
            }

            int total = gestorProduccion.calcularTotalDiario(fecha);

            int diferencia = gestorProduccion.calcularDiferenciaDiaria(
                    fecha, metaDiaria
            );

            double porcentaje
                    = gestorProduccion.calcularPorcentajeCumplimiento(
                            fecha, metaDiaria
                    );

            String estado = gestorProduccion.obtenerEstadoProduccion(
                    fecha, metaDiaria
            );

            lblProduccionReal.setText(
                    "Producción real: " + total + " unidades"
            );

            lblMeta.setText(
                    "Meta diaria: " + metaDiaria + " unidades"
            );

            lblDiferencia.setText(
                    "Diferencia (real - meta): "
                    + diferencia + " unidades"
            );

            lblCumplimiento.setText(
                    "Cumplimiento: "
                    + String.format("%.2f", porcentaje) + " %"
            );

            lblEstado.setText(
                    "Estado: " + estado
            );

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
                    "No se pudo comparar",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void limpiarResultados() {

        lblProduccionReal.setText("Producción real: —");
        lblMeta.setText("Meta diaria: —");
        lblDiferencia.setText("Diferencia (real - meta): —");
        lblCumplimiento.setText("Cumplimiento: —");
        lblEstado.setText("Estado: —");
    }

    private void limpiarCampos() {

        dpFecha.clear();
        txtMetaDiaria.setText("2400");

        limpiarResultados();

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
        txtMetaDiaria = new javax.swing.JTextField();
        btnComprar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        lblProduccionReal = new javax.swing.JLabel();
        lblMeta = new javax.swing.JLabel();
        lblDiferencia = new javax.swing.JLabel();
        lblCumplimiento = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        dpFecha = new com.github.lgooddatepicker.components.DatePicker();

        jLabel1.setText("Fecha:");

        jLabel2.setText("Meta diaria:");

        txtMetaDiaria.setText("2400");

        btnComprar.setText("Comparar");
        btnComprar.addActionListener(this::btnComprarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        lblProduccionReal.setText("Producción real::");

        lblMeta.setText("Meta diaria:    ");

        lblDiferencia.setText("Diferencia (real - meta): ");

        lblCumplimiento.setText("Cumplimiento: ");

        lblEstado.setText("Estado:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEstado)
                    .addComponent(lblCumplimiento)
                    .addComponent(lblMeta)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnComprar)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMetaDiaria, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dpFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblProduccionReal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblDiferencia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(102, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(dpFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtMetaDiaria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnComprar)
                    .addComponent(btnLimpiar))
                .addGap(18, 18, 18)
                .addComponent(lblProduccionReal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMeta)
                .addGap(18, 18, 18)
                .addComponent(lblDiferencia)
                .addGap(18, 18, 18)
                .addComponent(lblCumplimiento)
                .addGap(18, 18, 18)
                .addComponent(lblEstado)
                .addContainerGap(31, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnComprarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComprarActionPerformed
        compararProduccion();
    }//GEN-LAST:event_btnComprarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnComprar;
    private javax.swing.JButton btnLimpiar;
    private com.github.lgooddatepicker.components.DatePicker dpFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblCumplimiento;
    private javax.swing.JLabel lblDiferencia;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblMeta;
    private javax.swing.JLabel lblProduccionReal;
    private javax.swing.JTextField txtMetaDiaria;
    // End of variables declaration//GEN-END:variables
}
