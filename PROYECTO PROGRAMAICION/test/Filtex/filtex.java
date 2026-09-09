package Filtex;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class filtex extends JFrame {

    private final ArrayList<Trabajador> trabajadores = new ArrayList<>();
    private final ArrayList<Turno> turnos = new ArrayList<>();

    private int idTrabajador = 1, idTurno = 1;

    private final JTable tablaTrabajadores = tabla(
            "ID", "Nombre", "Identificacion", "Puesto", "Area");

    private final JTable tablaTurnos = tabla(
            "ID", "Nombre", "Inicio", "Fin");

    private final JTable tablaAsignaciones = tabla(
            "ID trabajador", "Trabajador", "ID turno", "Turno", "Inicio", "Fin");

    public filtex() {
        setTitle("FILTEX");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane pestanias = new JTabbedPane();
        String[] botones = {"Registrar", "Modificar", "Eliminar", "Consultar todos"};

        pestanias.addTab("Trabajadores", panel(tablaTrabajadores, botones,
                () -> trabajador(false), () -> trabajador(true),
                () -> eliminar(false), () -> actualizar()));

        pestanias.addTab("Turnos", panel(tablaTurnos, botones,
                () -> turno(false), () -> turno(true),
                () -> eliminar(true), () -> actualizar()));

        pestanias.addTab("Asignaciones", panel(tablaAsignaciones,
                new String[]{"Asignar", "Quitar", "Consultar por turno", "Ver todas"},
                () -> asignar(), () -> quitar(), () -> consultarTurno(),
                () -> mostrarAsignaciones(null)));

        add(pestanias);
    }

    private JTable tabla(String... columnas) {
        JTable tabla = new JTable(new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tabla;
    }

    // Reutiliza la misma estructura para las tres pestanias.
    private JPanel panel(JTable tabla, String[] nombres, Runnable... acciones) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel botones = new JPanel();

        for (int i = 0; i < nombres.length; i++) {
            JButton boton = new JButton(nombres[i]);
            Runnable accion = acciones[i];

            boton.addActionListener(e -> {
                try {
                    accion.run();
                } catch (IllegalArgumentException ex) {
                    mensaje(ex.getMessage());
                } catch (DateTimeParseException ex) {
                    mensaje("Hora incorrecta. Usa HH:mm, entre 00:00 y 23:59.");
                }
            });

            botones.add(boton);
        }

        panel.add(botones, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    private int seleccion(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila == -1)
            throw new IllegalArgumentException("Selecciona una fila en la tabla.");
        return fila;
    }

    private boolean confirmar(String texto) {
        return JOptionPane.showConfirmDialog(this, texto, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // Formulario compartido para trabajadores y turnos.
    private String[] formulario(String titulo, String[] nombres, String... valores) {
        JPanel panel = new JPanel(new GridLayout(nombres.length, 2, 5, 5));
        JTextField[] campos = new JTextField[nombres.length];

        for (int i = 0; i < nombres.length; i++) {
            panel.add(new JLabel(nombres[i]));
            campos[i] = new JTextField(valores[i], 20);
            panel.add(campos[i]);
        }

        if (JOptionPane.showConfirmDialog(this, panel, titulo,
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return null;

        String[] datos = new String[nombres.length];

        for (int i = 0; i < datos.length; i++) {
            datos[i] = campos[i].getText().trim();
            if (datos[i].isEmpty())
                throw new IllegalArgumentException("Completa todos los campos.");
        }

        return datos;
    }

    private void trabajador(boolean editar) {
        Trabajador t = editar ? trabajadores.get(seleccion(tablaTrabajadores)) : null;

        String[] datos = formulario("Trabajador",
                new String[]{"Nombre", "Identificacion", "Puesto", "Area"},
                editar ? t.getNombre() : "",
                editar ? t.getIdentificacion() : "",
                editar ? t.getPuestoTrabajo() : "",
                editar ? t.getAreaAsignada() : "");

        if (datos == null) return;

        for (Trabajador otro : trabajadores) {
            if (otro != t && otro.getIdentificacion().equalsIgnoreCase(datos[1]))
                throw new IllegalArgumentException("La identificacion ya esta registrada.");
        }

        if (editar)
            t.modificar(datos[0], datos[1], datos[2], datos[3]);
        else
            trabajadores.add(new Trabajador(
                    idTrabajador++, datos[0], datos[1], datos[2], datos[3]));

        actualizar();
    }

    private void turno(boolean editar) {
        Turno t = editar ? turnos.get(seleccion(tablaTurnos)) : null;

        String[] datos = formulario("Turno",
                new String[]{"Nombre", "Inicio (HH:mm)", "Fin (HH:mm)"},
                editar ? t.getNombre() : "",
                editar ? t.getHoraInicio().toString() : "08:00",
                editar ? t.getHoraFinalizacion().toString() : "16:00");

        if (datos == null) return;

        if (!datos[1].matches("[0-9]{2}:[0-9]{2}")
                || !datos[2].matches("[0-9]{2}:[0-9]{2}"))
            throw new IllegalArgumentException("Usa el formato HH:mm. Ejemplo: 08:30.");

        LocalTime inicio = LocalTime.parse(datos[1]);
        LocalTime fin = LocalTime.parse(datos[2]);

        if (inicio.equals(fin))
            throw new IllegalArgumentException("Las horas deben ser distintas.");

        if (editar)
            t.modificar(datos[0], inicio, fin);
        else
            turnos.add(new Turno(idTurno++, datos[0], inicio, fin));

        actualizar();
    }

    private void eliminar(boolean esTurno) {
        int fila = seleccion(esTurno ? tablaTurnos : tablaTrabajadores);

        if (!confirmar("¿Eliminar el registro y sus asignaciones?")) return;

        if (esTurno) {
            Turno t = turnos.remove(fila);
            for (Trabajador trabajador : trabajadores)
                trabajador.quitarTurno(t);
        } else {
            trabajadores.remove(fila);
        }

        actualizar();
    }

    private void asignar() {
        if (trabajadores.isEmpty() || turnos.isEmpty())
            throw new IllegalArgumentException("Registra primero trabajadores y turnos.");

        JComboBox<Trabajador> personas =
                new JComboBox<>(trabajadores.toArray(new Trabajador[0]));

        JComboBox<Turno> horarios =
                new JComboBox<>(turnos.toArray(new Turno[0]));

        Object[] campos = {"Trabajador:", personas, "Turno:", horarios};

        if (JOptionPane.showConfirmDialog(this, campos, "Asignar turno",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;

        Trabajador t = (Trabajador) personas.getSelectedItem();

        if (!t.asignarTurno((Turno) horarios.getSelectedItem()))
            throw new IllegalArgumentException("El trabajador ya tiene ese turno.");

        mostrarAsignaciones(null);
    }

    private void quitar() {
        int fila = seleccion(tablaAsignaciones);

        if (!confirmar("¿Quitar esta asignacion?")) return;

        int persona = (Integer) tablaAsignaciones.getValueAt(fila, 0);
        int horario = (Integer) tablaAsignaciones.getValueAt(fila, 2);

        for (Trabajador t : trabajadores) {
            if (t.getIdTrabajador() == persona) {
                for (Turno turno : t.getTurnos()) {
                    if (turno.getIdTurno() == horario)
                        t.quitarTurno(turno);
                }
            }
        }

        mostrarAsignaciones(null);
    }

    private void consultarTurno() {
        if (turnos.isEmpty())
            throw new IllegalArgumentException("Primero registra un turno.");

        Turno t = (Turno) JOptionPane.showInputDialog(
                this, "Selecciona un turno:", "Consultar personal",
                JOptionPane.QUESTION_MESSAGE, null,
                turnos.toArray(), turnos.get(0));

        if (t != null) mostrarAsignaciones(t);
    }

    private DefaultTableModel limpiar(JTable tabla) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        return modelo;
    }

    private void actualizar() {
        DefaultTableModel personas = limpiar(tablaTrabajadores);
        DefaultTableModel horarios = limpiar(tablaTurnos);

        for (Trabajador t : trabajadores)
            personas.addRow(new Object[]{
                t.getIdTrabajador(), t.getNombre(), t.getIdentificacion(),
                t.getPuestoTrabajo(), t.getAreaAsignada()
            });

        for (Turno t : turnos)
            horarios.addRow(new Object[]{
                t.getIdTurno(), t.getNombre(),
                t.getHoraInicio(), t.getHoraFinalizacion()
            });

        mostrarAsignaciones(null);
    }

    private void mostrarAsignaciones(Turno filtro) {
        DefaultTableModel modelo = limpiar(tablaAsignaciones);

        for (Trabajador t : trabajadores) {
            for (Turno turno : t.getTurnos()) {
                if (filtro == null || turno == filtro)
                    modelo.addRow(new Object[]{
                        t.getIdTrabajador(), t.getNombre(),
                        turno.getIdTurno(), turno.getNombre(),
                        turno.getHoraInicio(), turno.getHoraFinalizacion()
                    });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new filtex().setVisible(true));
    }
}