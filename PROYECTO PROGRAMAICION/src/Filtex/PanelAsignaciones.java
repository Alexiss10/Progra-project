package Filtex;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PanelAsignaciones extends PanelBase {

    private final GestionTrabajadores gestionTrabajadores;
    private final GestionTurnos gestionTurnos;
    private final JTable tabla = crearTabla("ID trabajador", "Trabajador",
            "ID turno", "Turno", "Inicio", "Fin");

    public PanelAsignaciones(GestionTrabajadores gestionTrabajadores, GestionTurnos gestionTurnos) {
        this.gestionTrabajadores = gestionTrabajadores;
        this.gestionTurnos = gestionTurnos;

        add(barraBotones(new String[]{"Asignar", "Quitar", "Consultar por turno", "Ver todas"},
                this::asignar, this::quitar, this::consultarTurno, this::actualizar),
                BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizar();
    }

    private void asignar() {
        ArrayList<Trabajador> trabajadores = gestionTrabajadores.listar();
        ArrayList<Turno> turnos = gestionTurnos.listar();

        if (trabajadores.isEmpty() || turnos.isEmpty()) {
            throw new IllegalArgumentException("Registra primero trabajadores y turnos.");
        }

        JComboBox<Trabajador> personas = new JComboBox<>(trabajadores.toArray(new Trabajador[0]));
        JComboBox<Turno> horarios = new JComboBox<>(turnos.toArray(new Turno[0]));
        Object[] campos = {"Trabajador:", personas, "Turno:", horarios};

        if (JOptionPane.showConfirmDialog(this, campos, "Asignar turno",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        Trabajador t = (Trabajador) personas.getSelectedItem();
        gestionTrabajadores.asignarTurno(t.getIdTrabajador(), (Turno) horarios.getSelectedItem());
        actualizar();
    }

    private void quitar() {
        int idTrabajador = idSeleccionado(tabla);
        int idTurno = (Integer) tabla.getValueAt(tabla.getSelectedRow(), 2);

        if (!confirmar("¿Quitar esta asignación?")) {
            return;
        }
        gestionTrabajadores.quitarTurno(idTrabajador, gestionTurnos.buscarPorId(idTurno));
        actualizar();
    }

    private void consultarTurno() {
        ArrayList<Turno> turnos = gestionTurnos.listar();
        if (turnos.isEmpty()) {
            throw new IllegalArgumentException("Primero registra un turno.");
        }

        Turno t = (Turno) JOptionPane.showInputDialog(this, "Selecciona un turno:",
                "Consultar personal", JOptionPane.QUESTION_MESSAGE, null,
                turnos.toArray(), turnos.get(0));

        if (t != null) {
            mostrar(t);
        }
    }

    private void mostrar(Turno filtro) {
        DefaultTableModel modelo = limpiar(tabla);
        for (Trabajador t : gestionTrabajadores.listar()) {
            for (Turno turno : t.getTurnos()) {
                if (filtro == null || turno == filtro) {
                    modelo.addRow(new Object[]{t.getIdTrabajador(), t.getNombre(),
                        turno.getIdTurno(), turno.getNombre(),
                        turno.getHoraInicio(), turno.getHoraFinalizacion()});
                }
            }
        }
    }

    @Override
    public void actualizar() {
        mostrar(null);
    }
}

