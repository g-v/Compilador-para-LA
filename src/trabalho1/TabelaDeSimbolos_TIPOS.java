/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Esquilo
 */
public class TabelaDeSimbolos_TIPOS {
private Map<String, EntradaTS_TIPO> tabelaDeSimbolos;
    
    public TabelaDeSimbolos_TIPOS() {
        tabelaDeSimbolos = new HashMap<>();
    }
    
    public void inserir(String nome, int valor) {
        EntradaTS_TIPO etds = new EntradaTS_TIPO();
        etds.nome = nome;
        etds.valor = valor;
        tabelaDeSimbolos.put(nome, etds);
    }
    
    public EntradaTS_TIPO verificar(String nome) {
        if(!tabelaDeSimbolos.containsKey(nome))
            return null;
        else return tabelaDeSimbolos.get(nome);
    }
}
