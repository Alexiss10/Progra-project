package Filtex;

import clases.GestionTelas;
import clases.gestionProduccion;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import javax.swing.*;

/** Ventana única de FILTEX: pestañas, guardado general y confirmación al cerrar. */
public class VentanaPrincipal extends JFrame {

    private interface Guardado {

        void ejecutar() throws IOException;
    }

    private static class Modulo {

        final String nombre;
        final BooleanSupplier hayCambios;
        final Guardado guardado;

        Modulo(String nombre, BooleanSupplier hayCambios, Guardado guardado) {
            this.nombre = nombre;
            this.hayCambios = hayCambios;
            this.guardado = guardado;
        }
    }

    private final ArrayList<Modulo> modulos = new ArrayList<>();
    private final JTabbedPane pestanias = new JTabbedPane();

    public VentanaPrincipal(GestionTelas telas, GestionTurnos turnos,
            GestionTrabajadores trabajadores, gestionProduccion produccion,
            GestionRecursos recursos, GestionUsoRecursos usos) {

        super("FILTEX");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Orden de guardado: telas, turnos, trabajadores, producción, recursos y usos.
        modulos.add(new Modulo("Telas", telas::hayCambiosPendientes, telas::guardarCambios));
        modulos.add(new Modulo("Turnos", turnos::hayCambiosPendientes, turnos::guardarCambios));
        modulos.add(new Modulo("Trabajadores y asignaciones",
                trabajadores::hayCambiosPendientes, trabajadores::guardarCambios));
        modulos.add(new Modulo("Producción", produccion::hayCambiosPendientes, produccion::guardarCambios));
        // GestionUsoRecursos guarda también las existencias de los recursos.
        modulos.add(new Modulo("Recursos y utilizaciones", usos::hayCambiosPendientes, usos::guardarCambios));

        // Paneles de Alexis: descomentar cuando los entregue.
        // pestanias.addTab("Telas", new PanelTelas(telas, produccion));
        // pestanias.addTab("Producción", new PanelProduccion(produccion, telas, turnos, usos));
        // pestanias.addTab("Análisis de producción", new PanelAnalisisProduccion(produccion));

        pestanias.addTab("Trabajadores", new PanelTrabajadores(trabajadores));
        pestanias.addTab("Turnos", new PanelTurnos(turnos, trabajadores));
        pestanias.addTab("Asignaciones", new PanelAsignaciones(trabajadores, turnos));
        pestanias.addTab("Producción por turno", new PanelProduccionTurno(turnos, produccion));
        pestanias.addTab("Recursos", new PanelRecursos(recursos));
        pestanias.addTab("Uso de recursos", new PanelUsoRecursos(recursos, produccion, usos));

        pestanias.addChangeListener(e -> actualizarSeleccionada());

        JButton btnGuardar = new JButton("Guardar todo");
        btnGuardar.addActionListener(e -> {
            if (!hayCambios()) {
                JOptionPane.showMessageDialog(this, "No hay cambios pendientes.");
            } else if (guardarTodo()) {
                JOptionPane.showMessageDialog(this, "Datos guardados correctamente.");
            }
        });
        JPanel barra = new JPanel();
        barra.add(btnGuardar);

        add(barra, BorderLayout.NORTH);
        add(pestanias, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrar();
            }
        });

        actualizarSeleccionada();
    }

    private void actualizarSeleccionada() {
        Component seleccionado = pestanias.getSelectedComponent();
        if (seleccionado instanceof Actualizable) {
            ((Actualizable) seleccionado).actualizar();
        }
    }

    private boolean hayCambios() {
        for (Modulo m : modulos) {
            if (m.hayCambios.getAsBoolean()) {
                return true;
            }
        }
        return false;
    }

    /** Guarda cada módulo con cambios; si alguno falla informa y devuelve false. */
    private boolean guardarTodo() {
        StringBuilder errores = new StringBuilder();

        for (Modulo m : modulos) {
            if (!m.hayCambios.getAsBoolean()) {
                continue;
            }
            try {
                m.guardado.ejecutar();
            } catch (IOException ex) {
                errores.append("- ").append(m.nombre).append(": ")
                        .append(ex.getMessage()).append("\n");
            }
        }

        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar:\n" + errores,
                    "Error al guardar", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void cerrar() {
        if (!hayCambios()) {
            salir();
            return;
        }

        String[] opciones = {"Guardar y salir", "Salir sin guardar", "Cancelar"};
        int r = JOptionPane.showOptionDialog(this, "Hay cambios sin guardar.",
                "Cerrar FILTEX", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, opciones, opciones[0]);

        if (r == 0) {
            if (guardarTodo()) {
                salir();
            }
            // Si falla el guardado, el programa permanece abierto.
        } else if (r == 1) {
            salir();
        }
    }

    private void salir() {
        dispose();
        System.exit(0);
    }
}