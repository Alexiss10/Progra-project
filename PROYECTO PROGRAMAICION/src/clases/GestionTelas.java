package clases;
import java.util.ArrayList;
/**
 *
 * @author ALEXIS
 */
public class GestionTelas {
    private ArrayList<Tela> telas;
    private int siguienteId = 1;
    
    public GestionTelas(){
        telas = new ArrayList<>();
    }
    
    public Tela buscarPorCodigo(String codigo){
        if (codigo == null || codigo.trim().isEmpty()){
            throw new IllegalArgumentException("debes ingresar un "
                    + "codigo para buscar");
        }
        
        String codigoBuscado = codigo.trim();
        
        for(Tela tela:telas){
            if(tela.getCodigo().equalsIgnoreCase(codigoBuscado)){
                return tela;
            }
        }
        return null;
    }
    
    public void registrar(String codigo, String nombre, 
            String descripcion){
        Tela nuevaTela = new Tela(siguienteId, codigo, nombre, descripcion);
        
        Tela existente = buscarPorCodigo(nuevaTela.getCodigo());
        
        if(existente != null){
         throw new IllegalArgumentException("El codigo a registrar "
                 + "ya existe.");
        }
        
        telas.add(nuevaTela);
        siguienteId ++;
    }
    
    
    
}
