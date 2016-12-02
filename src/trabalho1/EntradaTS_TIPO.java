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
public class EntradaTS_TIPO extends EntradaTabelaDeSimbolos{
    
    private List<String> listaConversoes;
    boolean isStructure;
    int nPonteiros;
    String tipoAlias;

    public EntradaTS_TIPO() {
        listaConversoes = new LinkedList<>();
    }
    
    boolean convertePara(String tipo)
    {
        return listaConversoes.contains(tipo);
    }
    
    void insereConversao(String tipo)
    {
        if(convertePara(tipo) == false)
            listaConversoes.add(tipo);
    }
    
    List<String> getConversoes()
    {
        return listaConversoes;
    }
}
