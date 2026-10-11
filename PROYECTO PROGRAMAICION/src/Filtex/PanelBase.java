package Filtex;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Base común de los paneles: tablas no editables, botones, mensajes y formularios. */
public abstract class PanelBase extends JPanel implements Actualizable {

    protected PanelBase() {
        super(new BorderLayout());
    }

    protected JTable crearTabla(String... columnas) {
        JTable tabla = new JTable(new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tabla;
    }

    protected DefaultTableModel limpiar(JTable tabla) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        return modelo;
    }

    protected JPanel barraBotones(String[] nombres, Runnable... acciones) {
        JPanel barra = new JPanel();
        for (int i = 0; i < nombres.length; i++) {
            JButton boton = new JButton(nombres[i]);
            Runnable accion = acciones[i];
            boton.addActionListener(e -> ejecutar(accion));
            barra.add(boton);
        }
        return barra;
    }

    protected void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException ex) {
            mensaje(ex.getMessage());
        } catch (DateTimeParseException ex) {
            mensaje("Formato incorrecto. Fecha: yyyy-MM-dd. Hora: HH:mm.");
        }
    }

    protected void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    protected boolean confirmar(String texto) {
        return JOptionPane.showConfirmDialog(this, texto, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** ID (columna 0) de la fila seleccionada. */
    protected int idSeleccionado(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            throw new IllegalArgumentException("Selecciona una fila en la tabla.");
        }
        return (Integer) tabla.getValueAt(fila, 0);
    }

    protected double decimal(String campo, String texto) {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo " + campo
                    + " debe ser un número.");
        }
    }

    /** Devuelve null si el usuario cancela. */
    protected String[] formulario(String titulo, String[] nombres, String... valores) {
        JPanel panel = new JPanel(new GridLayout(nombres.length, 2, 5, 5));
        JTextField[] campos = new JTextField[nombres.length];

        for (int i = 0; i < nombres.length; i++) {
            panel.add(new JLabel(nombres[i]));
            campos[i] = new JTextField(valores[i], 20);
            panel.add(campos[i]);
        }

        if (JOptionPane.showConfirmDialog(this, panel, titulo,
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return null;
        }

        String[] datos = new String[nombres.length];
        for (int i = 0; i < datos.length; i++) {
            datos[i] = campos[i].getText().trim();
            if (datos[i].isEmpty()) {
                throw new IllegalArgumentException("Completa todos los campos.");
            }
        }
        return datos;
    }
}
