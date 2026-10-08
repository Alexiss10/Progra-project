/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Vistas;

import clases.GestionTelas;
import clases.Produccion;
import clases.Tela;
import clases.gestionProduccion;
import Filtex.Turno;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ALEXIS
 */
public class PanelProduccion extends javax.swing.JPanel {

    private gestionProduccion gestorProduccion;
    private GestionTelas gestorTelas;
    private ArrayList<Turno> turnos;
    private DefaultTableModel modeloTabla;
    private Integer idProduccionSeleccionada = null;

    public PanelProduccion(gestionProduccion gestorProduccion, GestionTelas gestorTelas, ArrayList<Turno> turnos) {

        if (gestorProduccion == null || gestorTelas == null || turnos == null) {
            throw new IllegalArgumentException("Debes proporcionar la gestión de producción, las telas y los turnos.");
        }

        initComponents();

        this.gestorProduccion = gestorProduccion;
        this.gestorTelas = gestorTelas;
        this.turnos = turnos;

        modeloTabla = (DefaultTableModel) tblProduccion.getModel();

        cargarOpciones();
        actualizarTabla();
        limpiarCampos();
    }

    public void actualizarTabla() {

        modeloTabla.setRowCount(0);

        for (Produccion produccion : gestorProduccion.listar()) {

            modeloTabla.addRow(new Object[]{
                produccion.getIdProduccion(),
                produccion.getFecha().toString(),
                produccion.getHora().toString(),
                produccion.getCantProducida(),
                produccion.getTela().getNombre(),
                produccion.getTurno().getNombre()
            });
        }
    }

    public void cargarOpciones() {

        cmbTela.removeAllItems();
        cmbTurno.removeAllItems();

        for (Tela tela : gestorTelas.listar()) {
            cmbTela.addItem(tela);
        }

        for (Turno turno : turnos) {
            cmbTurno.addItem(turno);
        }

        cmbTela.setSelectedIndex(-1);
        cmbTurno.setSelectedIndex(-1);
    }

    public void actualizarPanel() {

        cargarOpciones();
        actualizarTabla();
        limpiarCampos();
    }

    private void limpiarCampos() {

        idProduccionSeleccionada = null;

        txtFecha.setText("");
        txtHora.setText("");
        txtCantidad.setText("");

        cmbTela.setSelectedIndex(-1);
        cmbTurno.setSelectedIndex(-1);

        tblProduccion.clearSelection();
        txtFecha.requestFocusInWindow();
    }

    private void guardarRegistro(boolean modificar) {

        if (modificar && idProduccionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Primero selecciona la producción que deseas modificar.");
            return;
        }

        try {
            if (txtFecha.getText().trim().isEmpty()
                    || txtHora.getText().trim().isEmpty()
                    || txtCantidad.getText().trim().isEmpty()) {

                throw new IllegalArgumentException("Debes ingresar la fecha, la hora y la cantidad.");
            }

            Tela tela = (Tela) cmbTela.getSelectedItem();
            Turno turno = (Turno) cmbTurno.getSelectedItem();

            if (tela == null) {
                throw new IllegalArgumentException("Debes seleccionar una tela.");
            }

            if (turno == null) {
                throw new IllegalArgumentException("Debes seleccionar un turno.");
            }

            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            if (modificar) {
                gestorProduccion.modificar(idProduccionSeleccionada, fecha, cantidad, hora, tela.getIdTela(), turno);
            } else {
                gestorProduccion.registrar(fecha, cantidad, hora, tela.getIdTela(), turno);
            }

            actualizarTabla();
            limpiarCampos();

            String mensaje;

            if (modificar) {
                mensaje = "Producción modificada.";
            } else {
                mensaje = "Producción registrada.";
            }

            JOptionPane.showMessageDialog(this, mensaje + " Presiona Guardar para conservar los cambios.");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa una fecha válida en formato AAAA-MM-DD y una hora válida en formato HH:mm.", "Datos incorrectos", JOptionPane.WARNING_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero dentro del rango permitido.", "Cantidad incorrecta", JOptionPane.WARNING_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo completar la operación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void seleccionarProduccion() {

        int fila = tblProduccion.getSelectedRow();

        if (fila == -1) {
            return;
        }

        int filaModelo = tblProduccion.convertRowIndexToModel(fila);
        int idProduccion = (Integer) modeloTabla.getValueAt(filaModelo, 0);

        Produccion produccion = gestorProduccion.buscarPorId(idProduccion);

        if (produccion == null) {
            actualizarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "La producción seleccionada ya no existe.");
            return;
        }

        idProduccionSeleccionada = produccion.getIdProduccion();

        txtFecha.setText(produccion.getFecha().toString());
        txtHora.setText(produccion.getHora().toString());
        txtCantidad.setText(String.valueOf(produccion.getCantProducida()));

        cmbTela.setSelectedIndex(-1);

        for (int i = 0; i < cmbTela.getItemCount(); i++) {

            Tela tela = cmbTela.getItemAt(i);

            if (tela.getIdTela() == produccion.getTela().getIdTela()) {
                cmbTela.setSelectedIndex(i);
                break;
            }
        }

        cmbTurno.setSelectedIndex(-1);

        for (int i = 0; i < cmbTurno.getItemCount(); i++) {

            Turno turno = cmbTurno.getItemAt(i);

            if (turno.getIdTurno() == produccion.getTurno().getIdTurno()) {
                cmbTurno.setSelectedIndex(i);
                break;
            }
        }
    }

    private void eliminarProduccion() {

        if (idProduccionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Primero selecciona la producción que deseas eliminar.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Deseas eliminar la producción seleccionada?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            gestorProduccion.eliminar(idProduccionSeleccionada);

            actualizarTabla();
            limpiarCampos();

            JOptionPane.showMessageDialog(this, "Producción eliminada. Presiona Guardar para conservar los cambios.");

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo eliminar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void guardarCambios() {

        if (!gestorProduccion.hayCambiosPendientes()) {
            JOptionPane.showMessageDialog(this, "No hay cambios pendientes para guardar.");
            return;
        }

        try {
            // Las telas deben estar guardadas antes que las producciones.
            gestorTelas.guardarCambios();
            gestorProduccion.guardarCambios();

            JOptionPane.showMessageDialog(this, "Cambios guardados correctamente.");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo completar el guardado: " + ex.getMessage(), "Error de guardado", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFecha = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtHora = new javax.swing.JTextField();
        txtCantidad = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbTela = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cmbTurno = new javax.swing.JComboBox<>();
        btnrRegistrar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduccion = new javax.swing.JTable();

        jLabel1.setText("Fecha (AAAA-MM-DD)");

        jLabel2.setText("Hora (HH:mm)");

        jLabel3.setText("Cantidad producida:");

        jLabel4.setText("Tela:");

        jLabel5.setText("Turno:");

        btnrRegistrar.setText("Registrar");
        btnrRegistrar.addActionListener(this::btnrRegistrarActionPerformed);

        btnModificar.setText("Modificar");
        btnModificar.addActionListener(this::btnModificarActionPerformed);

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        tblProduccion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Fecha", "Hora", "Cantidad", "Tela", "Turno"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProduccion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProduccionMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProduccion);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtFecha)
                                    .addComponent(txtHora)
                                    .addComponent(txtCantidad)
                                    .addComponent(cmbTela, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmbTurno, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnLimpiar)
                                    .addComponent(btnrRegistrar))
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnModificar)
                                    .addComponent(btnGuardar))
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminar)))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbTela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cmbTurno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnrRegistrar)
                    .addComponent(btnModificar)
                    .addComponent(btnEliminar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLimpiar)
                    .addComponent(btnGuardar))
                .addGap(33, 33, 33)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnrRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrRegistrarActionPerformed
        guardarRegistro(false);
    }//GEN-LAST:event_btnrRegistrarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        guardarRegistro(true);
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarProduccion();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarCambios();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void tblProduccionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProduccionMouseClicked
        seleccionarProduccion();
    }//GEN-LAST:event_tblProduccionMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnrRegistrar;
    private javax.swing.JComboBox<Tela> cmbTela;
    private javax.swing.JComboBox<Turno> cmbTurno;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblProduccion;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtHora;
    // End of variables declaration//GEN-END:variables
}
