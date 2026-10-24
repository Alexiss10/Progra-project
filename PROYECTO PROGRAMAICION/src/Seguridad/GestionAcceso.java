package seguridad;

import archivos.ArchivoUsuario;
import java.io.IOException;

public class GestionAcceso {

    private static final long DURACION_BLOQUEO_MS = 5 * 60 * 1000L;

    private final ArchivoUsuario archivo;
    private Usuario propietario;
    private boolean sesionActiva = false;

    public GestionAcceso() throws IOException {

        archivo = new ArchivoUsuario();
        propietario = archivo.cargar();
    }

    public boolean tienePropietario() {
        return propietario != null;
    }

    public void crearPropietario(String nombreUsuario, char[] clave)
            throws IOException {

        if (tienePropietario()) {
            throw new IllegalArgumentException(
                    "Ya existe una cuenta de propietario."
            );
        }

        if (clave == null || clave.length < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres."
            );
        }

        Usuario nuevo = new Usuario(
                nombreUsuario,
                SeguridadClave.crearHash(clave),
                0,
                0
        );

        guardarEstado(nuevo);
    }

    public boolean autenticar(String nombreUsuario, char[] clave)
            throws IOException {

        if (!tienePropietario()) {
            throw new IllegalArgumentException(
                    "Primero debes crear la cuenta del propietario."
            );
        }

        if (sesionActiva) {
            throw new IllegalArgumentException(
                    "Ya hay una sesión iniciada."
            );
        }

        long segundos = getSegundosDeBloqueoRestantes();

        if (segundos > 0) {
            throw new IllegalArgumentException(
                    "Acceso bloqueado. Intenta nuevamente en "
                    + segundos + " segundos."
            );
        }

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()
                || clave == null || clave.length == 0) {

            throw new IllegalArgumentException(
                    "Debes ingresar el usuario y la contraseña."
            );
        }

        int fallos = propietario.getBloqueadoHasta() > 0
                ? 0 : propietario.getIntentosFallidos();

        boolean claveCorrecta = SeguridadClave.verificar(
                clave,
                propietario.getHashClave()
        );

        boolean usuarioCorrecto = propietario.getNombreUsuario().equals(
                nombreUsuario.trim()
        );

        if (usuarioCorrecto && claveCorrecta) {

            Usuario actualizado = new Usuario(
                    propietario.getNombreUsuario(),
                    propietario.getHashClave(),
                    0,
                    0
            );

            guardarEstado(actualizado);

            sesionActiva = true;

            return true;
        }

        fallos++;

        long bloqueadoHasta = 0;

        if (fallos == Usuario.MAX_INTENTOS) {
            bloqueadoHasta = System.currentTimeMillis()
                    + DURACION_BLOQUEO_MS;
        }

        Usuario actualizado = new Usuario(
                propietario.getNombreUsuario(),
                propietario.getHashClave(),
                fallos,
                bloqueadoHasta
        );

        guardarEstado(actualizado);

        return false;
    }

    public int getIntentosRestantes() {

        if (!tienePropietario()) {
            return Usuario.MAX_INTENTOS;
        }

        if (propietario.getBloqueadoHasta() > 0
                && getSegundosDeBloqueoRestantes() == 0) {

            return Usuario.MAX_INTENTOS;
        }

        return Usuario.MAX_INTENTOS - propietario.getIntentosFallidos();
    }

    public long getSegundosDeBloqueoRestantes() {

        if (!tienePropietario()) {
            return 0;
        }

        long restantes = propietario.getBloqueadoHasta()
                - System.currentTimeMillis();

        return restantes <= 0 ? 0 : (restantes + 999) / 1000;
    }

    public boolean haySesionActiva() {
        return sesionActiva;
    }

    public String getNombreUsuario() {
        return tienePropietario() ? propietario.getNombreUsuario() : "";
    }

    public void cerrarSesion() {
        sesionActiva = false;
    }

    private void guardarEstado(Usuario actualizado) throws IOException {

        archivo.guardar(actualizado);
        propietario = actualizado;
    }
}
