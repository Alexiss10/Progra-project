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

/** Único punto de inicio de FILTEX. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Telas y turnos
                GestionTelas telas = new GestionTelas();
                GestionTurnos turnos = new GestionTurnos();

                // 2. Trabajadores y asignaciones
                GestionTrabajadores trabajadores = new GestionTrabajadores(turnos);

                // 3. Producción (necesita telas y turnos)
                gestionProduccion produccion = new gestionProduccion(telas, turnos.listar());
                turnos.setGestionProduccion(produccion);

                // 4. Recursos
                GestionRecursos recursos = new GestionRecursos();

                // 5. Utilizaciones
                GestionUsoRecursos usos = new GestionUsoRecursos(recursos, produccion);

                new VentanaPrincipal(telas, turnos, trabajadores, produccion,
                        recursos, usos).setVisible(true);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "No se pudieron cargar los datos:\n" + ex.getMessage(),
                        "Error al iniciar FILTEX", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
