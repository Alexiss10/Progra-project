package seguridad;

public final class Usuario {

    public static final int MAX_INTENTOS = 5;

    private final String nombreUsuario;
    private final String hashClave;
    private final int intentosFallidos;
    private final long bloqueadoHasta;

    public Usuario(String nombreUsuario, String hashClave,
            int intentosFallidos, long bloqueadoHasta) {

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debes ingresar el nombre de usuario."
            );
        }

        if (nombreUsuario.contains(";")
                || nombreUsuario.contains("\n")
                || nombreUsuario.contains("\r")) {

            throw new IllegalArgumentException(
                    "El usuario no puede contener punto y coma "
                    + "ni saltos de línea."
            );
        }

        if (hashClave == null || hashClave.isBlank()
                || hashClave.contains(";")
                || hashClave.contains("\n")
                || hashClave.contains("\r")) {

            throw new IllegalArgumentException(
                    "La contraseña guardada no es válida."
            );
        }

        if (intentosFallidos < 0
                || intentosFallidos > MAX_INTENTOS
                || bloqueadoHasta < 0) {

            throw new IllegalArgumentException(
                    "Los datos de intentos o bloqueo no son válidos."
            );
        }

        if ((intentosFallidos == MAX_INTENTOS)
                != (bloqueadoHasta > 0)) {

            throw new IllegalArgumentException(
                    "El bloqueo no coincide con los intentos fallidos."
            );
        }

        this.nombreUsuario = nombreUsuario.trim();
        this.hashClave = hashClave;
        this.intentosFallidos = intentosFallidos;
        this.bloqueadoHasta = bloqueadoHasta;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getHashClave() {
        return hashClave;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public long getBloqueadoHasta() {
        return bloqueadoHasta;
    }

    public String getRol() {
        return "Propietario";
    }
}
