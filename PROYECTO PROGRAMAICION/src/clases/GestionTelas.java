package clases;

import archivos.ArchivoTelas;
import java.io.IOException;

import java.util.ArrayList;

/**
 *
 * @author ALEXIS
 */
public class GestionTelas {

    private ArrayList<Tela> telas;
    private int siguienteId = 1;
    private ArchivoTelas archivo;

    public GestionTelas() throws IOException {

        telas = new ArrayList<>();
        archivo = new ArchivoTelas();

        cargarTelas();
    }

    public Tela buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("debes ingresar un "
                    + "codigo para buscar");
        }

        String codigoBuscado = codigo.trim();

        for (Tela tela : telas) {
            if (tela.getCodigo().equalsIgnoreCase(codigoBuscado)) {
                return tela;
            }
        }
        return null;
    }

    public void registrar(String codigo, String nombre,
            String descripcion) {
        Tela nuevaTela = new Tela(siguienteId, codigo, nombre, descripcion);

        Tela existente = buscarPorCodigo(nuevaTela.getCodigo());

        if (existente != null) {
            throw new IllegalArgumentException("El codigo a registrar "
                    + "ya existe.");
        }

        telas.add(nuevaTela);
        siguienteId++;
    }

    public Tela buscarTelaPorId(int idTela) {
        for (Tela tela : telas) {
            if (idTela == tela.getIdTela()) {
                return tela;
            }
        }
        return null;
    }

    public void modificar(int idTela, String codigo, String nombre,
            String descipcion) {

        Tela tela1 = buscarTelaPorId(idTela);

        if (tela1 == null) {
            throw new IllegalArgumentException("El identificador ingresado no existe");
        }

        Tela tela2 = buscarPorCodigo(codigo);
        if (tela2 != null && tela2.getIdTela() != idTela) {
            throw new IllegalArgumentException("El codigo ingresado ya existe");
        }

        tela1.actualizarDatos(codigo, nombre, descipcion);
    }

    public void eliminar(int idTela) {

        Tela tela = buscarTelaPorId(idTela);
        if (tela == null) {
            throw new IllegalArgumentException("El identificador de la tela "
                    + "ingresado no existe");
        }
        telas.remove(tela);
    }

    public ArrayList<Tela> listar() {
        return new ArrayList<>(telas);
    }

    private void cargarTelas() throws IOException {

        ArrayList<Tela> telasCargadas = archivo.cargar();

        int mayorId = 0;

        for (Tela tela : telasCargadas) {

            if (buscarTelaPorId(tela.getIdTela()) != null) {
                throw new IOException(
                        "El fichero contiene un ID repetido: "
                        + tela.getIdTela()
                );
            }

            if (buscarPorCodigo(tela.getCodigo()) != null) {
                throw new IOException(
                        "El fichero contiene un código repetido: "
                        + tela.getCodigo()
                );
            }

            telas.add(tela);

            if (tela.getIdTela() > mayorId) {
                mayorId = tela.getIdTela();
            }
        }

        siguienteId = mayorId + 1;
    }
    
    public void guardarCambios()throws IOException{
        archivo.guardar(telas);
    }
    
    

}
