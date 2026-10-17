package Filtex;

import Vistas.PanelTelas;
import Vistas.PanelProduccion;
import Vistas.PanelProduccionDiaria;
import Vistas.PanelComparacion;
import Vistas.PanelBajoRendimiento;
import clases.GestionTelas;
import clases.gestionProduccion;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class VentanaPrincipal extends JFrame {

    private final GestionTelas telas;
    private final GestionTurnos turnos;
    private final GestionTrabajadores trabajadores;
    private final gestionProduccion produccion;
    private final GestionRecursos recursos;
    private final GestionUsoRecursos usos;

    private final JTabbedPane pestanias = new JTabbedPane();

    private final ArrayList<Turno> turnosDisponibles;

    private final PanelTelas panelTelas;
    private final PanelProduccion panelProduccion;
    private final PanelProduccionDiaria panelProduccionDiaria;
    private final PanelComparacion panelComparacion;
    private final PanelBajoRendimiento panelBajoRendimiento;

    public VentanaPrincipal(GestionTelas telas, GestionTurnos turnos,
            GestionTrabajadores trabajadores, gestionProduccion produccion,
            GestionRecursos recursos, GestionUsoRecursos usos) {

        super("FILTEX");

        this.telas = telas;
        this.turnos = turnos;
        this.trabajadores = trabajadores;
        this.produccion = produccion;
        this.recursos = recursos;
        this.usos = usos;

        setSize(1200, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        turnosDisponibles = turnos.listar();

        panelTelas = new PanelTelas(telas);
        panelProduccion = new PanelProduccion(produccion, telas, turnosDisponibles);
        panelProduccionDiaria = new PanelProduccionDiaria(produccion);
        panelComparacion = new PanelComparacion(produccion);
        panelBajoRendimiento = new PanelBajoRendimiento(produccion);
        panelTelas.configurarGuardado(this::guardarConMensaje);
        panelProduccion.configurarGuardado(this::guardarConMensaje);

        pestanias.addTab("Trabajadores", new PanelTrabajadores(trabajadores));
        pestanias.addTab("Turnos", new PanelTurnos(turnos, trabajadores));
        pestanias.addTab("Asignaciones", new PanelAsignaciones(trabajadores, turnos));

        pestanias.addTab("Telas", panelTelas);
        pestanias.addTab("Producción", panelProduccion);
        pestanias.addTab("Producción diaria", panelProduccionDiaria);
        pestanias.addTab("Comparación", panelComparacion);
        pestanias.addTab("Bajo rendimiento", panelBajoRendimiento);

        pestanias.addTab("Producción por turno", new PanelProduccionTurno(turnos, produccion));
        pestanias.addTab("Recursos", new PanelRecursos(recursos));
        pestanias.addTab("Uso de recursos", new PanelUsoRecursos(recursos, produccion, usos));

        pestanias.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        pestanias.addChangeListener(e -> actualizarSeleccionada());

        JButton btnGuardarTodo = new JButton("Guardar todo");

        btnGuardarTodo.addActionListener(e -> guardarConMensaje());

        JPanel barra = new JPanel();
        barra.add(btnGuardarTodo);

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

        if (seleccionado == panelTelas) {

            panelTelas.actualizarTabla();

        } else if (seleccionado == panelProduccion) {

            // Se actualiza la misma lista que recibió el panel.
            turnosDisponibles.clear();
            turnosDisponibles.addAll(turnos.listar());

            panelProduccion.actualizarPanel();

        } else if (seleccionado == panelProduccionDiaria) {

            panelProduccionDiaria.actualizarPanel();

        } else if (seleccionado == panelComparacion) {

            panelComparacion.actualizarPanel();

        } else if (seleccionado == panelBajoRendimiento) {

            panelBajoRendimiento.actualizarPanel();

        } else if (seleccionado instanceof Actualizable) {

            ((Actualizable) seleccionado).actualizar();
        }
    }

    private boolean hayCambios() {

        return telas.hayCambiosPendientes()
                || turnos.hayCambiosPendientes()
                || trabajadores.hayCambiosPendientes()
                || produccion.hayCambiosPendientes()
                || recursos.hayCambiosPendientes()
                || usos.hayCambiosPendientes();
    }

    private boolean guardarTodo() {

        String modulo = "telas";

        try {
            telas.guardarCambios();

            modulo = "turnos";
            turnos.guardarCambios();

            modulo = "trabajadores y asignaciones";
            trabajadores.guardarCambios();

            modulo = "producción";
            produccion.guardarCambios();

            // Este gestor guarda los recursos y después sus usos.
            modulo = "recursos y utilizaciones";
            usos.guardarCambios();

            return true;

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo completar el guardado de " + modulo
                    + ".\n" + ex.getMessage()
                    + "\nLos módulos anteriores pueden haberse guardado."
                    + "\nCorrige el problema y vuelve a presionar Guardar todo.",
                    "Error de guardado",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }

    private void cerrar() {

        if (!hayCambios()) {
            salir();
            return;
        }

        String[] opciones = {
            "Guardar y salir",
            "Salir sin guardar",
            "Cancelar"
        };

        int respuesta = JOptionPane.showOptionDialog(
                this,
                "Hay cambios pendientes de guardar.",
                "Cerrar FILTEX",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (respuesta == 0) {

            if (guardarTodo()) {
                salir();
            }

        } else if (respuesta == 1) {

            salir();
        }
    }

    private void salir() {

        dispose();
        System.exit(0);
    }

    private void guardarConMensaje() {

        if (!hayCambios()) {
            JOptionPane.showMessageDialog(this, "No hay cambios pendientes.");
            return;
        }

        if (guardarTodo()) {
            JOptionPane.showMessageDialog(this, "Datos guardados correctamente.");
        }
    }
}
