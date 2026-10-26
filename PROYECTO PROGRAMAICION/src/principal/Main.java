package principal;

import Filtex.GestionRecursos;
import Filtex.GestionTrabajadores;
import Filtex.GestionTurnos;
import Filtex.GestionUsoRecursos;
import Filtex.VentanaPrincipal;

import seguridad.GestionAcceso;
import Vistas.DialogoAcceso;

import clases.GestionTelas;
import clases.gestionProduccion;
import Filtex.GestionMetas;
import Filtex.GestionReportes;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {

        GestionAcceso acceso = null;

        try {

            acceso = new GestionAcceso();

            DialogoAcceso dialogo = new DialogoAcceso(null, acceso);
            dialogo.setVisible(true);


            if (!acceso.haySesionActiva()) {
                return;
            }

            
            GestionTelas telas = new GestionTelas();

            GestionTurnos turnos = new GestionTurnos();

            GestionTrabajadores trabajadores
                    = new GestionTrabajadores(turnos);

            gestionProduccion produccion = new gestionProduccion(
                    telas,
                    turnos.listar()
            );

            turnos.setGestionProduccion(produccion);

            GestionRecursos recursos = new GestionRecursos();

            GestionUsoRecursos usos = new GestionUsoRecursos(
                    recursos,
                    produccion
            );

            produccion.vincularUsos(usos);
            
            GestionMetas metas = new GestionMetas();
            GestionReportes reportes = new GestionReportes(produccion, metas);

            VentanaPrincipal ventana = new VentanaPrincipal(
                    telas,
                    turnos,
                    trabajadores,
                    produccion,
                    recursos,
                    usos,
                     metas,
                    reportes,
                    acceso,
                    Main::iniciar
            );

            ventana.setVisible(true);

        } catch (IOException | IllegalArgumentException ex) {

            if (acceso != null) {
                acceso.cerrarSesion();
            }

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo iniciar FILTEX:\n" + ex.getMessage(),
                    "Error al iniciar FILTEX",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
