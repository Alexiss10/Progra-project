package Filtex;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GestionMetas {

    private static final String FICHERO = "metas.txt";
    private final Path ruta = Path.of("datos", FICHERO);
    private int metaDiaria = 2400;
    private int metaPorHora = 100;
    private boolean cambiosPendientes = false;

    public GestionMetas() throws IOException {
        int numero = 0;
        for (String linea : UtilArchivo.leer(ruta)) {
            numero++;
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] d = UtilArchivo.campos(linea, 2, numero, FICHERO);
            try {
                int diaria = Integer.parseInt(d[0]);
                int porHora = Integer.parseInt(d[1]);
                validar(diaria, porHora);
                metaDiaria = diaria;
                metaPorHora = porHora;
            } catch (IllegalArgumentException e) {
                throw UtilArchivo.invalido(FICHERO, numero, e.getMessage(), e);
            }
        }
    }

    private static void validar(int diaria, int porHora) {
        if (diaria <= 0 || porHora <= 0) {
            throw new IllegalArgumentException("Las metas deben ser números enteros mayores que cero.");
        }
    }

    public void definir(int diaria, int porHora) {
        validar(diaria, porHora);
        metaDiaria = diaria;
        metaPorHora = porHora;
        cambiosPendientes = true;
    }

    public boolean estanDefinidas() {
        return metaDiaria > 0 && metaPorHora > 0;
    }

    public int getMetaDiaria() {
        return metaDiaria;
    }

    public int getMetaPorHora() {
        return metaPorHora;
    }

    public void guardarCambios() throws IOException {
        if (estanDefinidas()) {
            UtilArchivo.escribir(ruta, List.of(metaDiaria + ";" + metaPorHora));
        }
        cambiosPendientes = false;
    }

    public boolean hayCambiosPendientes() {
        return cambiosPendientes;
    }
}

