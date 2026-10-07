/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package archivos;

import clases.Produccion;
import clases.Tela;
import clases.GestionTelas;
import Filtex.Turno;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ArchivoProduccion {

    private Path ruta = Path.of("datos", "produccion.txt");

    public void guardar(ArrayList<Produccion> producciones) throws IOException {

        Files.createDirectories(ruta.getParent());

        ArrayList<String> lineas = new ArrayList<>();

        for (Produccion produccion : producciones) {

            String linea = produccion.getIdProduccion() + ";" + produccion.getFecha() + ";" + produccion.getCantProducida() + ";" + produccion.getHora() + ";" + produccion.getTela().getIdTela() + ";" + produccion.getTurno().getIdTurno();

            lineas.add(linea);
        }

        Files.write(ruta, lineas, StandardCharsets.UTF_8);
    }

    public ArrayList<Produccion> cargar(GestionTelas gestionTelas, ArrayList<Turno> turnos) throws IOException {

        if (gestionTelas == null || turnos == null) {
            throw new IllegalArgumentException("Debes proporcionar la gestión de telas y la lista de turnos.");
        }

        ArrayList<Produccion> produccionesCargadas = new ArrayList<>();

        Files.createDirectories(ruta.getParent());

        if (!Files.exists(ruta)) {
            return produccionesCargadas;
        }

        List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);

        int numeroLinea = 0;

        for (String linea : lineas) {

            numeroLinea++;

            String[] datos = linea.split(";", -1);

            if (datos.length != 6) {
                throw new IOException("La línea " + numeroLinea + " debe contener seis datos.");
            }

            try {

                int idProduccion = Integer.parseInt(datos[0].trim());
                LocalDate fecha = LocalDate.parse(datos[1].trim());
                int cantidad = Integer.parseInt(datos[2].trim());
                LocalTime hora = LocalTime.parse(datos[3].trim());
                int idTela = Integer.parseInt(datos[4].trim());
                int idTurno = Integer.parseInt(datos[5].trim());

                Tela tela = gestionTelas.buscarTelaPorId(idTela);
                Turno turno = buscarTurnoPorId(idTurno, turnos);

                if (tela == null) {
                    throw new IllegalArgumentException("No existe la tela con ID " + idTela + ".");
                }

                if (turno == null) {
                    throw new IllegalArgumentException("No existe el turno con ID " + idTurno + ".");
                }

                for (Produccion registrada : produccionesCargadas) {

                    if (registrada.getIdProduccion() == idProduccion) {
                        throw new IllegalArgumentException("El ID de producción " + idProduccion + " está repetido.");
                    }
                }

                Produccion produccion = new Produccion(idProduccion, fecha, cantidad, hora, tela, turno);

                produccionesCargadas.add(produccion);

            } catch (DateTimeParseException e) {

                throw new IOException("Fecha u hora inválida en la línea " + numeroLinea + ".", e);

            } catch (IllegalArgumentException e) {

                throw new IOException("Datos inválidos en la línea " + numeroLinea + ": " + e.getMessage(), e);
            }
        }

        return produccionesCargadas;
    }

    private Turno buscarTurnoPorId(int idTurno, ArrayList<Turno> turnos) {

        for (Turno turno : turnos) {

            if (turno.getIdTurno() == idTurno) {
                return turno;
            }
        }

        return null;
    }
}
