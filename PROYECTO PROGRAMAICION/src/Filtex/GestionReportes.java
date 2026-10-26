package Filtex;

import clases.Produccion;
import clases.gestionProduccion;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GestionReportes {

    private final gestionProduccion produccion;
    private final GestionMetas metas;

    public GestionReportes(gestionProduccion produccion, GestionMetas metas) {
        if (produccion == null || metas == null) {
            throw new IllegalArgumentException("Debes proporcionar la producción y las metas.");
        }
        this.produccion = produccion;
        this.metas = metas;
    }

    public ArrayList<FilaReporte> reporteDiario(LocalDate desde, LocalDate hasta) {
        exigirMetas();
        ArrayList<FilaReporte> filas = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> e : totalesPorDia(desde, hasta).entrySet()) {
            filas.add(new FilaReporte(e.getKey(), "", e.getValue(), metas.getMetaDiaria()));
        }
        return filas;
    }

    public ArrayList<FilaReporte> reportePorHora(LocalDate desde, LocalDate hasta) {
        exigirMetas();
        TreeMap<LocalDateTime, Integer> totales = new TreeMap<>();
        for (Produccion p : delPeriodo(desde, hasta)) {
            LocalDateTime clave = p.getFecha().atTime(p.getHora().getHour(), 0);
            totales.merge(clave, p.getCantProducida(), Integer::sum);
        }

        ArrayList<FilaReporte> filas = new ArrayList<>();
        for (Map.Entry<LocalDateTime, Integer> e : totales.entrySet()) {
            int h = e.getKey().getHour();
            filas.add(new FilaReporte(e.getKey().toLocalDate(),
                    String.format("%02d:00 - %02d:59", h, h), e.getValue(), metas.getMetaPorHora()));
        }
        return filas;
    }

    /** Meta del turno = meta por hora x duración del turno. */
    public ArrayList<FilaReporte> reportePorTurno(LocalDate desde, LocalDate hasta) {
        exigirMetas();
        TreeMap<LocalDate, TreeMap<Integer, Integer>> totales = new TreeMap<>();
        Map<Integer, Turno> turnos = new HashMap<>();

        for (Produccion p : delPeriodo(desde, hasta)) {
            int idTurno = p.getTurno().getIdTurno();
            turnos.put(idTurno, p.getTurno());
            totales.computeIfAbsent(p.getFecha(), k -> new TreeMap<>())
                    .merge(idTurno, p.getCantProducida(), Integer::sum);
        }

        ArrayList<FilaReporte> filas = new ArrayList<>();
        for (Map.Entry<LocalDate, TreeMap<Integer, Integer>> dia : totales.entrySet()) {
            for (Map.Entry<Integer, Integer> t : dia.getValue().entrySet()) {
                Turno turno = turnos.get(t.getKey());
                filas.add(new FilaReporte(dia.getKey(), turno.getNombre(),
                        t.getValue(), metaTurno(turno)));
            }
        }
        return filas;
    }

    /** Horas con registros cuyo total es menor que la meta por hora. */
    public ArrayList<FilaReporte> horasBajoRendimiento(LocalDate desde, LocalDate hasta) {
        ArrayList<FilaReporte> bajas = new ArrayList<>();
        for (FilaReporte fila : reportePorHora(desde, hasta)) {
            if (!fila.cumpleMeta()) {
                bajas.add(fila);
            }
        }
        return bajas;
    }

    /** Compara cada día con el día anterior que tenga registros. No requiere metas. */
    public ArrayList<Object[]> variaciones(LocalDate desde, LocalDate hasta) {
        ArrayList<Object[]> filas = new ArrayList<>();
        LocalDate fechaAnterior = null;
        int totalAnterior = 0;

        for (Map.Entry<LocalDate, Integer> e : totalesPorDia(desde, hasta).entrySet()) {
            if (fechaAnterior != null) {
                int diferencia = e.getValue() - totalAnterior;
                String porcentaje = totalAnterior == 0 ? "-"
                        : String.format("%+.1f %%", diferencia * 100.0 / totalAnterior);
                filas.add(new Object[]{e.getKey(), e.getValue(), fechaAnterior, totalAnterior,
                    String.format("%+d", diferencia), porcentaje});
            }
            fechaAnterior = e.getKey();
            totalAnterior = e.getValue();
        }
        return filas;
    }

    private TreeMap<LocalDate, Integer> totalesPorDia(LocalDate desde, LocalDate hasta) {
        TreeMap<LocalDate, Integer> totales = new TreeMap<>();
        for (Produccion p : delPeriodo(desde, hasta)) {
            totales.merge(p.getFecha(), p.getCantProducida(), Integer::sum);
        }
        return totales;
    }

    private ArrayList<Produccion> delPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Debes indicar el período a consultar.");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la inicial.");
        }
        ArrayList<Produccion> resultado = new ArrayList<>();
        for (Produccion p : produccion.listar()) {
            if (!p.getFecha().isBefore(desde) && !p.getFecha().isAfter(hasta)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    private void exigirMetas() {
        if (!metas.estanDefinidas()) {
            throw new IllegalArgumentException("Primero define las metas (botón Metas).");
        }
    }

    private int metaTurno(Turno turno) {
        long minutos = Duration.between(turno.getHoraInicio(), turno.getHoraFinalizacion()).toMinutes();
        if (minutos <= 0) {
            minutos += 24 * 60; // el turno cruza la medianoche
        }
        return (int) Math.round(metas.getMetaPorHora() * minutos / 60.0);
    }
}
