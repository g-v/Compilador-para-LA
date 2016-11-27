/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Esquilo
 */
public final class Escopos<T> {
    private LinkedList<T> pilhaDeTabelas;
    
    public Escopos(T escopoBase)
    {
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo(escopoBase);
    }
    
    public void criarNovoEscopo(T instance)
    {
        pilhaDeTabelas.push(instance);
    }
    
    public T pegarEscopoAtual()
    {
        return pilhaDeTabelas.peek();
    }
    
    public List<T> percorrerEscopo()
    {
        return pilhaDeTabelas;
    }
    
    public void abandonarEscopo()
    {
        pilhaDeTabelas.pop();
    }
}
