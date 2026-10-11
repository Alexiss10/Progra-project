package Filtex;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ArchivoTrabajadores {

    private static final String FICHERO = "trabajadores.txt";
    private final Path ruta = Path.of("datos", FICHERO);

    public void guardar(List<Trabajador> trabajadores) throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (Trabajador t : trabajadores) {
            lineas.add(t.getIdTrabajador() + ";" + t.getNombre() + ";"
                    + t.getIdentificacion() + ";" + t.getPuestoTrabajo() + ";"
                    + t.getAreaAsignada());
        }
        UtilArchivo.escribir(ruta, lineas);
    }

    public ArrayList<Trabajador> cargar() throws IOException {
        ArrayList<Trabajador> cargados = new ArrayList<>();
        int numero = 0;

        for (String linea : UtilArchivo.leer(ruta)) {
            numero++;
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] d = UtilArchivo.campos(linea, 5, numero, FICHERO);

            try {
                int id = Integer.parseInt(d[0]);
                if (id <= 0) {
                    throw new IllegalArgumentException("El ID debe ser mayor a cero.");
                }
                String nombre = UtilArchivo.texto(d[1], "nombre");
                String identificacion = UtilArchivo.texto(d[2], "identificación");
                String puesto = UtilArchivo.texto(d[3], "puesto");
                String area = UtilArchivo.texto(d[4], "área");

                for (Trabajador otro : cargados) {
                    if (otro.getIdTrabajador() == id) {
                        throw new IllegalArgumentException("El ID " + id + " está repetido.");
                    }
                    if (otro.getIdentificacion().equalsIgnoreCase(identificacion)) {
                        throw new IllegalArgumentException("La identificación "
                                + identificacion + " está repetida.");
                    }
                }
                cargados.add(new Trabajador(id, nombre, identificacion, puesto, area));

            } catch (IllegalArgumentException e) {
                throw UtilArchivo.invalido(FICHERO, numero, e.getMessage(), e);
            }
        }
        return cargados;
    }
}

