package ec.edu.uce.modelo.interfaces;

import java.util.Collection;

/**
 * Esta interface nos permite manejar las operaciones básicas al momento de almacenar los datos del programa.
 * (Insertar, Editar, Eliminar, Buscar y Listar)
 * 
 * @author Ing. Giovanny Moncayo Unda. MSc. 
 * @version 2019-01-08
 */

public interface InterfazCRUD {
    
     /**
     * Metodo que permite crear un nuevo Objeto de cualquier clase.
     * @param obj Es el nuevo Objeto que se va a crear
     * @return Un mensaje para alertar al usuario
     */    
    public String crear(Object obj);
    
    /**
     * Metodo que permite actualizar el estado de un Objeto.
     * @param id El id del Objeto a ser actualizado
     * @return Un mensaje para alertar al usuario
     */
    public String actualizar(Object id);
    
    /**
     * Metodo que permite eliminar un Objeto
     * @param id El id del Objeto a ser eliminado
     * @return Un mensaje para alertar al usuario
     */
    public String borrar(Object id);
    
    /**
     * Metodo que permite buscar un Objeto
     * @param lista La coleccion donde se va a buscar
     * @param parametro El parametro del Objeto que se está buscando
     * @return El Objeto encontrado, si el objeto no existe, retorna null
     */
    public Object buscarPorParametro(Collection lista[], Object parametro);
        
    /**
     * Metodo que permita listar los Objetos
     * 
     * @return La colección con el resultado
     */
    public Collection listar();
    
}
