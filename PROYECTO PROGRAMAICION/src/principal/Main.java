package principal;

import Filtex.GestionRecursos;
import Filtex.GestionTrabajadores;
import Filtex.GestionTurnos;
import Filtex.GestionUsoRecursos;
import Filtex.VentanaPrincipal;

import clases.GestionTelas;
import clases.gestionProduccion;

import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                GestionTelas telas = new GestionTelas();
                GestionTurnos turnos = new GestionTurnos();

                GestionTrabajadores trabajadores = new GestionTrabajadores(turnos);

                gestionProduccion produccion = new gestionProduccion(telas, turnos.listar());
                turnos.setGestionProduccion(produccion);

                GestionRecursos recursos = new GestionRecursos();

                GestionUsoRecursos usos = new GestionUsoRecursos(recursos, produccion);

                produccion.vincularUsos(usos);

                new VentanaPrincipal(telas, turnos, trabajadores, produccion, recursos, usos).setVisible(true);

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(
                        null,
                        "No se pudieron cargar los datos:\n" + ex.getMessage(),
                        "Error al iniciar FILTEX",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
