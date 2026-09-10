package clases;

/**
 *
 * @author ALEXIS
 */
public class Tela {

    private int idTela;
    private String codigo;
    private String nombre;
    private String descripcion;

    public Tela(int idTela, String codigo, String nombre, String descripcion) {

        if (idTela <= 0) {
            throw new IllegalArgumentException("El identificador debe ser mayor a 0");
        }

        this.idTela = idTela;
        actualizarDatos(codigo, nombre, descripcion);
    }

    private String validarTexto(String valor, String campo) {

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + "es obligatorio");
        }

        if (valor.contains(";") || valor.contains("\n")) {
            throw new IllegalArgumentException("El campo " + campo + "no puede tener punto"
                    + "y coma ni saltos de linea");
        } else {
            return valor.trim();
        }
    }

    public void actualizarDatos(String codigo, String nombre, String descripcion) {

        String codigoValido = validarTexto(codigo, "codigo");
        String nombreValido = validarTexto(nombre, "nombre");
        String descripcionValida = validarTexto(descripcion, "descripcion");
        
        this.codigo = codigoValido;
        this.nombre = nombreValido;
        this.descripcion = descripcionValida;     
    }

    public int getIdTela() {
        return idTela;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = validarTexto(codigo, "codigo");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarTexto(nombre, "nombre");
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = validarTexto(descripcion, "descripcion");
    }

}
