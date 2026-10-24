package seguridad;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class SeguridadClave {

    private static final int ITERACIONES = 600_000;
    private static final int LONGITUD_SAL = 16;
    private static final int LONGITUD_HASH = 256;

    private static final SecureRandom ALEATORIO = new SecureRandom();

    private SeguridadClave() {
    }


    public static String crearHash(char[] clave) {

        if (clave == null || clave.length == 0) {
            throw new IllegalArgumentException(
                    "Debes ingresar una contraseña."
            );
        }

        byte[] sal = new byte[LONGITUD_SAL];
        ALEATORIO.nextBytes(sal);

        byte[] hash = calcularHash(clave, sal);

        return "v1:"
                + Base64.getEncoder().encodeToString(sal)
                + ":"
                + Base64.getEncoder().encodeToString(hash);
    }


    public static boolean verificar(char[] clave, String hashGuardado) {

        if (clave == null || clave.length == 0) {
            return false;
        }

        if (hashGuardado == null) {
            throw new IllegalArgumentException(
                    "Falta la contraseña guardada."
            );
        }

        String[] partes = hashGuardado.split(":", -1);

        if (partes.length != 3 || !partes[0].equals("v1")) {
            throw new IllegalArgumentException(
                    "El formato de la contraseña guardada no es válido."
            );
        }

        byte[] sal;
        byte[] hashEsperado;

        try {

            sal = Base64.getDecoder().decode(partes[1]);
            hashEsperado = Base64.getDecoder().decode(partes[2]);

        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                    "La contraseña guardada contiene datos inválidos.",
                    ex
            );
        }

        if (sal.length != LONGITUD_SAL
                || hashEsperado.length != LONGITUD_HASH / 8) {

            throw new IllegalArgumentException(
                    "La contraseña guardada está incompleta o dañada."
            );
        }

        byte[] hashIngresado = calcularHash(clave, sal);

        return MessageDigest.isEqual(hashEsperado, hashIngresado);
    }


    private static byte[] calcularHash(char[] clave, byte[] sal) {

        PBEKeySpec especificacion = new PBEKeySpec(
                clave,
                sal,
                ITERACIONES,
                LONGITUD_HASH
        );

        try {

            SecretKeyFactory fabrica = SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256"
            );

            return fabrica.generateSecret(especificacion).getEncoded();

        } catch (GeneralSecurityException ex) {

            throw new IllegalStateException(
                    "No se pudo procesar la contraseña.",
                    ex
            );

        } finally {

            especificacion.clearPassword();
        }
    }
}